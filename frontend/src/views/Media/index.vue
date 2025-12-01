.media-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-lighter);
  }
  
  &.media-item--selected {
    background: rgba(64, 158, 255, 0.1);
    border-left: 3px solid var(--primary-color);
  }
  
  &:last-child {
    border-bottom: none;
  }
}

.item-preview {
  width: 60px;
  height: 40px;
  border-radius: var(--border-radius-base);
  overflow: hidden;
  background: var(--bg-color-lighter);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--spacing-md);
  
  .preview-video,
  .preview-audio,
  .preview-image {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    
    i {
      font-size: 20px;
      color: var(--text-color-light);
    }
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.item-info {
  .file-name {
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--spacing-xs);
    word-break: break-all;
  }
  
  .file-meta {
    display: flex;
    gap: var(--spacing-md);
    font-size: 12px;
    color: var(--text-color-light);
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
    }
  }
}

.item-type,
.item-size,
.item-status,
.item-time {
  flex: 1;
  padding: 0 var(--spacing-md);
}

.type-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  
  &.type-video {
    background: rgba(64, 158, 255, 0.1);
    color: var(--primary-color);
  }
  
  &.type-audio {
    background: rgba(103, 194, 58, 0.1);
    color: var(--success-color);
  }
  
  &.type-image {
    background: rgba(230, 162, 60, 0.1);
    color: var(--warning-color);
  }
}

.item-size,
.item-time {
  color: var(--text-color-light);
  font-size: 14px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  
  &.status-uploaded {
    background: rgba(144, 147, 153, 0.1);
    color: var(--info-color);
  }
  
  &.status-processing {
    background: rgba(230, 162, 60, 0.1);
    color: var(--warning-color);
  }
  
  &.status-completed {
    background: rgba(103, 194, 58, 0.1);
    color: var(--success-color);
  }
  
  &.status-error {
    background: rgba(245, 108, 108, 0.1);
    color: var(--danger-color);
  }
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: var(--bg-color-lighter);
  border-radius: 2px;
  margin-top: var(--spacing-xs);
  overflow: hidden;
  
  .progress-fill {
    height: 100%;
    background: var(--primary-color);
    border-radius: 2px;
    transition: width 0.3s ease;
  }
}

.item-actions {
  .action-buttons {
    display: flex;
    gap: var(--spacing-xs);
  }
  
  .action-btn {
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    border-radius: var(--border-radius-base);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-color-light);
    cursor: pointer;
    transition: var(--transition-fast);
    
    &:hover {
      background: var(--bg-color-lighter);
      color: var(--text-color);
    }
    
    &.action-btn--danger:hover {
      background: rgba(245, 108, 108, 0.1);
      color: var(--danger-color);
    }
  }
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--bg-color-light);
  border-radius: var(--border-radius-base);
}

.pagination-info {
  color: var(--text-color-light);
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.pagination-btn,
.pagination-page {
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-color);
  background: var(--bg-color-lighter);
  border-radius: var(--border-radius-base);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: 14px;
  
  &:hover:not(:disabled) {
    border-color: var(--primary-color);
    color: var(--primary-color);
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  &.pagination-page--active {
    background: var(--primary-color);
    border-color: var(--primary-color);
    color: white;
  }
}

.batch-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--bg-color-light);
  border-top: 1px solid var(--border-color);
  padding: var(--spacing-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-medium);
  z-index: 1000;
}

.batch-info {
  color: var(--text-color);
  font-weight: 600;
}

.batch-buttons {
  display: flex;
  gap: var(--spacing-md);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-large);
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  
  &.modal-content--large {
    width: 600px;
  }
  
  &.modal-content--preview {
    width: 800px;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  
  h3 {
    margin: 0;
    color: var(--text-color);
  }
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-light);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-lighter);
    color: var(--text-color);
  }
}

.modal-body {
  padding: var(--spacing-lg);
  flex: 1;
  overflow-y: auto;
}

.modal-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

.upload-area {
  border: 2px dashed var(--border-color);
  border-radius: var(--border-radius-base);
  padding: var(--spacing-xl);
  text-align: center;
  transition: var(--transition-fast);
  cursor: pointer;
  margin-bottom: var(--spacing-lg);
  
  &.upload-area--dragover {
    border-color: var(--primary-color);
    background: rgba(64, 158, 255, 0.05);
  }
  
  .icon-upload-cloud {
    font-size: 48px;
    color: var(--text-color-light);
    margin-bottom: var(--spacing-md);
  }
  
  h4 {
    margin-bottom: var(--spacing-sm);
    color: var(--text-color);
  }
  
  p {
    color: var(--text-color-light);
    margin-bottom: var(--spacing-md);
  }
}

.upload-queue {
  h4 {
    margin-bottom: var(--spacing-md);
    color: var(--text-color);
  }
}

.queue-items {
  max-height: 300px;
  overflow-y: auto;
}

.queue-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md);
  background: var(--bg-color-lighter);
  border-radius: var(--border-radius-base);
  margin-bottom: var(--spacing-sm);
  
  &:last-child {
    margin-bottom: 0;
  }
}

.queue-info {
  flex: 1;
  
  .queue-name {
    font-weight: 600;
    color: var(--text-color);
    margin-bottom: var(--spacing-xs);
  }
  
  .queue-size {
    font-size: 12px;
    color: var(--text-color-light);
  }
}

.queue-progress {
  width: 200px;
  margin: 0 var(--spacing-md);
  
  .progress-text {
    font-size: 12px;
    color: var(--text-color-light);
    text-align: center;
    margin-top: var(--spacing-xs);
  }
}

.queue-actions {
  display: flex;
  gap: var(--spacing-xs);
}

.queue-action {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: var(--border-radius-base);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-light);
  cursor: pointer;
  transition: var(--transition-fast);
  
  &:hover {
    background: var(--bg-color-light);
    color: var(--text-color);
  }
  
  &.queue-action--danger:hover {
    background: rgba(245, 108, 108, 0.1);
    color: var(--danger-color);
  }
}

.preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  
  .preview-video,
  .preview-audio {
    width: 100%;
    max-width: 100%;
  }
  
  .preview-image {
    max-width: 100%;
    max-height: 500px;
    object-fit: contain;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// 响应式设计
@media (max-width: 1200px) {
  .media-page {
    padding: var(--spacing-lg);
  }
  
  .page-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filter-bar {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }
  
  .filter-group {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .media-page {
    padding: var(--spacing-md);
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .list-header {
    display: none;
  }
  
  .media-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  
  .item-preview {
    margin-right: 0;
  }
  
  .item-info,
  .item-type,
  .item-size,
  .item-status,
  .item-time,
  .item-actions {
    flex: none;
    width: 100%;
    padding: 0;
  }
  
  .pagination {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .modal-content--large,
  .modal-content--preview {
    width: 95vw;
    margin: var(--spacing-md);
  }
}
</style>