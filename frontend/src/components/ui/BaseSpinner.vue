<template>
  <div :class="spinnerClasses">
    <div class="spinner-circle"></div>
    <p v-if="text" class="spinner-text">{{ text }}</p>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

export interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  text?: string
  centered?: boolean
}

const props = withDefaults(defineProps<SpinnerProps>(), {
  size: 'md',
  centered: false,
})

const spinnerClasses = computed(() => {
  return [
    'base-spinner',
    `spinner-${props.size}`,
    {
      'spinner-centered': props.centered,
    },
  ]
})
</script>

<style scoped>
.base-spinner {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
}

.spinner-centered {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.spinner-circle {
  border-radius: 50%;
  border: 3px solid var(--color-border-light);
  border-top-color: var(--color-primary);
  animation: spin 0.8s linear infinite;
}

.spinner-sm .spinner-circle {
  width: 1.5rem;
  height: 1.5rem;
  border-width: 2px;
}

.spinner-md .spinner-circle {
  width: 2.5rem;
  height: 2.5rem;
  border-width: 3px;
}

.spinner-lg .spinner-circle {
  width: 4rem;
  height: 4rem;
  border-width: 4px;
}

.spinner-text {
  color: #6b7280;
  font-size: var(--font-size-sm);
  margin: 0;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
