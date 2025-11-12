<template>
  <Transition name="toast">
    <div
      v-if="isVisible"
      :class="toastClasses"
      role="alert"
      @mouseenter="pauseTimer"
      @mouseleave="resumeTimer"
    >
      <div class="toast-icon">
        <svg v-if="type === 'success'" viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
            clip-rule="evenodd"
          />
        </svg>
        <svg v-else-if="type === 'error'" viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
            clip-rule="evenodd"
          />
        </svg>
        <svg v-else-if="type === 'warning'" viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
            clip-rule="evenodd"
          />
        </svg>
        <svg v-else viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
            clip-rule="evenodd"
          />
        </svg>
      </div>

      <div class="toast-content">
        <p v-if="title" class="toast-title">{{ title }}</p>
        <p class="toast-message">{{ message }}</p>
      </div>

      <button class="toast-close" @click="close" aria-label="Close">
        <svg viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
            clip-rule="evenodd"
          />
        </svg>
      </button>
    </div>
  </Transition>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

export interface ToastProps {
  type?: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message: string
  duration?: number
  id?: string
}

const props = withDefaults(defineProps<ToastProps>(), {
  type: 'info',
  duration: 5000,
})

const emit = defineEmits<{
  close: [id: string | undefined]
}>()

const isVisible = ref(false)
let timeoutId: ReturnType<typeof setTimeout> | null = null
let remainingTime = ref(props.duration)
let startTime = 0

const toastClasses = computed(() => {
  return ['toast', `toast-${props.type}`]
})

const startTimer = () => {
  if (props.duration > 0) {
    startTime = Date.now()
    timeoutId = setTimeout(() => {
      close()
    }, remainingTime.value)
  }
}

const pauseTimer = () => {
  if (timeoutId) {
    clearTimeout(timeoutId)
    remainingTime.value -= Date.now() - startTime
  }
}

const resumeTimer = () => {
  startTimer()
}

const close = () => {
  isVisible.value = false
  setTimeout(() => {
    emit('close', props.id)
  }, 300)
}

onMounted(() => {
  isVisible.value = true
  startTimer()
})

onUnmounted(() => {
  if (timeoutId) {
    clearTimeout(timeoutId)
  }
})
</script>

<style scoped>
.toast {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  min-width: 300px;
  max-width: 500px;
  padding: 1rem;
  background-color: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border-left: 4px solid currentColor;
  margin-bottom: 0.75rem;
}

.toast-success {
  color: var(--color-success, #10b981);
}

.toast-error {
  color: var(--color-error, #ef4444);
}

.toast-warning {
  color: var(--color-warning, #f59e0b);
}

.toast-info {
  color: var(--color-info, #3b82f6);
}

.toast-icon {
  flex-shrink: 0;
  width: 1.5rem;
  height: 1.5rem;
}

.toast-icon svg {
  width: 100%;
  height: 100%;
}

.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  font-weight: 600;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  margin: 0 0 0.25rem 0;
}

.toast-message {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
}

.toast-close {
  flex-shrink: 0;
  width: 1.25rem;
  height: 1.25rem;
  padding: 0;
  background: none;
  border: none;
  color: var(--color-gray-500);
  cursor: pointer;
  transition: color 0.2s ease-in-out;
}

.toast-close:hover {
  color: var(--color-gray-700);
}

.toast-close svg {
  width: 100%;
  height: 100%;
}

/* Transitions */
.toast-enter-active,
.toast-leave-active {
  transition: var(--transition-slow);
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
