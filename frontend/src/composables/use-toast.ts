import { ref, readonly } from 'vue'
import type { ToastProps } from '@/components/ui/BaseToast.vue'

export interface Toast extends ToastProps {
  id: string
}

const toasts = ref<Toast[]>([])

let idCounter = 0

export function useToast() {
  const addToast = (toast: Omit<Toast, 'id'>) => {
    const id = `toast-${Date.now()}-${idCounter++}`
    toasts.value.push({ ...toast, id })
    return id
  }

  const removeToast = (id: string) => {
    const index = toasts.value.findIndex((t) => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  const success = (message: string, title?: string, duration?: number) => {
    return addToast({ type: 'success', message, title, duration })
  }

  const error = (message: string, title?: string, duration?: number) => {
    return addToast({ type: 'error', message, title, duration })
  }

  const warning = (message: string, title?: string, duration?: number) => {
    return addToast({ type: 'warning', message, title, duration })
  }

  const info = (message: string, title?: string, duration?: number) => {
    return addToast({ type: 'info', message, title, duration })
  }

  const clear = () => {
    toasts.value = []
  }

  return {
    toasts: readonly(toasts),
    addToast,
    removeToast,
    success,
    error,
    warning,
    info,
    clear,
  }
}
