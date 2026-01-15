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
import BaseToast from './BaseToast.vue'
import { useToast } from '@/composables/use-toast.ts'

const { toasts, removeToast } = useToast()
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
