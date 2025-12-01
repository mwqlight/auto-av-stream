<template>
  <canvas
    ref="canvasRef"
    class="particles-canvas"
    :style="canvasStyle"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  radius: number
  color: string
  opacity: number
  speed: number
}

interface Props {
  density?: number
  speed?: number
  colors?: string[]
  connectDistance?: number
  enableInteraction?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  density: 0.5,
  speed: 1,
  colors: () => [
    'rgba(64, 158, 255, 0.8)',
    'rgba(103, 194, 58, 0.6)',
    'rgba(230, 162, 60, 0.6)',
    'rgba(245, 108, 108, 0.6)',
    'rgba(144, 147, 153, 0.6)'
  ],
  connectDistance: 150,
  enableInteraction: true
})

const canvasRef = ref<HTMLCanvasElement>()
const canvasStyle = ref({
  position: 'fixed' as const,
  top: 0,
  left: 0,
  width: '100%',
  height: '100%',
  zIndex: -1,
  pointerEvents: 'none' as const
})

let canvas: HTMLCanvasElement
let ctx: CanvasRenderingContext2D
let animationFrameId: number
let particles: Particle[] = []
let mouseX = 0
let mouseY = 0
let mouseRadius = 0

const createParticle = (): Particle => {
  const radius = Math.random() * 2 + 0.5
  const speed = (Math.random() * 0.5 + 0.5) * props.speed
  
  return {
    x: Math.random() * canvas.width,
    y: Math.random() * canvas.height,
    vx: (Math.random() - 0.5) * speed,
    vy: (Math.random() - 0.5) * speed,
    radius,
    color: props.colors[Math.floor(Math.random() * props.colors.length)],
    opacity: Math.random() * 0.5 + 0.3,
    speed
  }
}

const initCanvas = () => {
  if (!canvasRef.value) return
  
  canvas = canvasRef.value
  ctx = canvas.getContext('2d')!
  
  // 设置canvas尺寸
  const resizeCanvas = () => {
    const dpr = window.devicePixelRatio || 1
    const rect = canvas.getBoundingClientRect()
    
    canvas.width = rect.width * dpr
    canvas.height = rect.height * dpr
    
    ctx.scale(dpr, dpr)
    canvas.style.width = rect.width + 'px'
    canvas.style.height = rect.height + 'px'
  }
  
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)
  
  // 创建粒子
  const particleCount = Math.floor((canvas.width * canvas.height) / 10000 * props.density)
  particles = Array.from({ length: particleCount }, createParticle)
  
  // 鼠标交互
  if (props.enableInteraction) {
    const handleMouseMove = (e: MouseEvent) => {
      const rect = canvas.getBoundingClientRect()
      mouseX = e.clientX - rect.left
      mouseY = e.clientY - rect.top
      mouseRadius = 100
    }
    
    const handleMouseLeave = () => {
      mouseRadius = 0
    }
    
    canvas.addEventListener('mousemove', handleMouseMove)
    canvas.addEventListener('mouseleave', handleMouseLeave)
    
    onUnmounted(() => {
      canvas.removeEventListener('mousemove', handleMouseMove)
      canvas.removeEventListener('mouseleave', handleMouseLeave)
    })
  }
  
  onUnmounted(() => {
    window.removeEventListener('resize', resizeCanvas)
  })
  
  return resizeCanvas
}

const drawParticles = () => {
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 绘制连线
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)'
  ctx.lineWidth = 0.5
  
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < props.connectDistance) {
        const opacity = 1 - distance / props.connectDistance
        ctx.strokeStyle = `rgba(255, 255, 255, ${opacity * 0.1})`
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.stroke()
      }
    }
  }
  
  // 绘制粒子
  particles.forEach(particle => {
    // 鼠标交互
    if (mouseRadius > 0) {
      const dx = particle.x - mouseX
      const dy = particle.y - mouseY
      const distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < mouseRadius) {
        const force = (mouseRadius - distance) / mouseRadius
        const angle = Math.atan2(dy, dx)
        
        particle.vx -= Math.cos(angle) * force * 0.1
        particle.vy -= Math.sin(angle) * force * 0.1
      }
    }
    
    // 更新位置
    particle.x += particle.vx
    particle.y += particle.vy
    
    // 边界检测
    if (particle.x < 0 || particle.x > canvas.width) {
      particle.vx = -particle.vx
      particle.x = Math.max(0, Math.min(canvas.width, particle.x))
    }
    
    if (particle.y < 0 || particle.y > canvas.height) {
      particle.vy = -particle.vy
      particle.y = Math.max(0, Math.min(canvas.height, particle.y))
    }
    
    // 绘制粒子
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2)
    
    // 渐变填充
    const gradient = ctx.createRadialGradient(
      particle.x, particle.y, 0,
      particle.x, particle.y, particle.radius
    )
    gradient.addColorStop(0, particle.color.replace('0.8', '1'))
    gradient.addColorStop(1, particle.color.replace('0.8', '0'))
    
    ctx.fillStyle = gradient
    ctx.fill()
    
    // 发光效果
    ctx.shadowColor = particle.color
    ctx.shadowBlur = 10
    ctx.fill()
    ctx.shadowBlur = 0
  })
  
  animationFrameId = requestAnimationFrame(drawParticles)
}

const startAnimation = () => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  drawParticles()
}

const stopAnimation = () => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
}

// 响应式更新
watch(
  () => [props.density, props.speed, props.colors, props.connectDistance],
  () => {
    stopAnimation()
    const resizeCanvas = initCanvas()
    if (resizeCanvas) {
      resizeCanvas()
    }
    startAnimation()
  },
  { deep: true }
)

onMounted(() => {
  initCanvas()
  startAnimation()
})

onUnmounted(() => {
  stopAnimation()
})

// 暴露方法给父组件
defineExpose({
  startAnimation,
  stopAnimation,
  updateParticles: (newParticles: Particle[]) => {
    particles = newParticles
  }
})
</script>

<style scoped>
.particles-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  pointer-events: none;
  background: transparent;
}
</style>