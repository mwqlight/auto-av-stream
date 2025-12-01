<template>
  <div class="particles-background">
    <vue-particles
      id="tsparticles"
      :options="particleOptions"
      @particles-loaded="onParticlesLoaded"
    />
  </div>
</template>

<script setup lang="ts">
import { loadFull } from 'tsparticles'
import type { Engine } from 'tsparticles-engine'

const particleOptions = {
  background: {
    color: {
      value: 'transparent'
    }
  },
  fpsLimit: 120,
  interactivity: {
    events: {
      onClick: {
        enable: true,
        mode: 'push'
      },
      onHover: {
        enable: true,
        mode: 'repulse'
      },
      resize: true
    },
    modes: {
      push: {
        quantity: 4
      },
      repulse: {
        distance: 200,
        duration: 0.4
      }
    }
  },
  particles: {
    color: {
      value: ['#00FFFF', '#FF00FF', '#FFFF00', '#00FF00']
    },
    links: {
      color: '#ffffff',
      distance: 150,
      enable: true,
      opacity: 0.2,
      width: 1
    },
    move: {
      direction: 'none',
      enable: true,
      outModes: {
        default: 'bounce'
      },
      random: false,
      speed: 2,
      straight: false
    },
    number: {
      density: {
        enable: true,
        area: 800
      },
      value: 80
    },
    opacity: {
      value: 0.5
    },
    shape: {
      type: 'circle'
    },
    size: {
      value: { min: 1, max: 5 }
    }
  },
  detectRetina: true
}

const onParticlesLoaded = async (engine: Engine) => {
  await loadFull(engine)
}
</script>

<style scoped>
.particles-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

#tsparticles {
  width: 100%;
  height: 100%;
}
</style>