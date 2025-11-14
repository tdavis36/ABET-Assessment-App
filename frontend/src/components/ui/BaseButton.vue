<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :class="buttonClasses"
    @click="handleClick"
  >
    <span v-if="loading" class="spinner"></span>
    <slot v-else></slot>
  </button>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

export interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  type?: 'button' | 'submit' | 'reset'
  disabled?: boolean
  loading?: boolean
  fullWidth?: boolean
}

const props = withDefaults(defineProps<ButtonProps>(), {
  variant: 'primary',
  size: 'md',
  type: 'button',
  disabled: false,
  loading: false,
  fullWidth: false,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}

const buttonClasses = computed(() => {
  return [
    'base-button',
    `button-${props.variant}`,
    `button-${props.size}`,
    {
      'button-disabled': props.disabled || props.loading,
      'button-loading': props.loading,
      'button-full-width': props.fullWidth,
    },
  ]
})
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-weight: 500;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  font-family: inherit;
  line-height: 1.5;
}

.base-button:focus-visible {
  outline: 2px solid currentColor;
  outline-offset: 2px;
}

/* Sizes */
.button-sm {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
}

.button-md {
  padding: 0.5rem 1rem;
  font-size: 1rem;
}

.button-lg {
  padding: 0.75rem 1.5rem;
  font-size: 1.125rem;
}

/* Variants */
.button-primary {
  background-color: var(--color-primary);
  color: white;
}

.button-primary:hover:not(.button-disabled) {
  background-color: var(--color-primary-dark);
}

.button-secondary {
  background-color: var(--color-bg-secondary);
  color: white;
}

.button-secondary:hover:not(.button-disabled) {
  background-color: var(--color-bg-tertiary);
}

.button-success {
  background-color: var(--color-success);
  color: white;
}

.button-success:hover:not(.button-disabled) {
  background-color: var(--color-success-dark);
}

.button-danger {
  background-color: var(--color-error);
  color: white;
}

.button-danger:hover:not(.button-disabled) {
  background-color: var(--color-error-dark);
}

.button-ghost {
  background-color: transparent;
  color: var(--color-text-tertiary);
  border: 1px solid var(--color-border-light);
}

.button-ghost:hover:not(.button-disabled) {
  background-color: var(--color-bg-tertiary);
}

/* States */
.button-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-full-width {
  width: 100%;
}

.button-loading {
  cursor: wait;
}

/* Spinner */
.spinner {
  width: 1em;
  height: 1em;
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
