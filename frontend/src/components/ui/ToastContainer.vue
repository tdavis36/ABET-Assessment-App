<template>
  <Teleport to="body">
    <div class="toast-container">
      <BaseToast
        v-for="toast in toasts"
        :key="toast.id"
        :id="toast.id"
        :type="toast.type"
        :title="toast.title"
        :message="toast.message"
        :duration="toast.duration"
        @close="(id) => id && removeToast(id)"
      />
    </div>
  </Teleport>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import BaseToast, { type ToastProps } from './BaseToast.vue'

export interface Toast extends ToastProps {
  id: string
}

const toasts = ref<Toast[]>([])

const addToast = (toast: Omit<Toast, 'id'>) => {
  const id = `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
  toasts.value.push({ ...toast, id })
  return id
}

const removeToast = (id?: string) => {
  if (id) {
    const index = toasts.value.findIndex((t) => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }
}

defineExpose({
  addToast,
  removeToast,
})
</script>

<style scoped>
.toast-container {
  position: fixed;
  bottom: 1rem;
  right: 1rem;
  z-index: 9999;
  display: flex;
  flex-direction: column-reverse;
  pointer-events: none;
}

.toast-container > * {
  pointer-events: auto;
}
</style>
