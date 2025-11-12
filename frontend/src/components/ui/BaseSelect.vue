<template>
  <div class="select-wrapper">
    <label v-if="label" :for="id" class="select-label">
      {{ label }}
      <span v-if="required" class="required-indicator">*</span>
    </label>

    <div class="select-container">
      <select
        :id="id"
        :value="modelValue"
        :disabled="disabled"
        :required="required"
        :class="selectClasses"
        @change="handleChange"
      >
        <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
        <option
          v-for="option in options"
          :key="getOptionValue(option)"
          :value="getOptionValue(option)"
        >
          {{ getOptionLabel(option) }}
        </option>
      </select>
      <div class="select-icon">
        <svg viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
            clip-rule="evenodd"
          />
        </svg>
      </div>
    </div>

    <p v-if="error" class="error-message">{{ error }}</p>
    <p v-else-if="hint" class="hint-message">{{ hint }}</p>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

export interface SelectOption {
  label: string
  value: string | number
}

export interface SelectProps {
  id?: string
  modelValue?: string | number
  options: (SelectOption | string | number)[]
  label?: string
  placeholder?: string
  hint?: string
  error?: string
  disabled?: boolean
  required?: boolean
}

const props = withDefaults(defineProps<SelectProps>(), {
  disabled: false,
  required: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const handleChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  emit('update:modelValue', target.value)
}

const getOptionValue = (option: SelectOption | string | number): string | number => {
  if (typeof option === 'object') {
    return option.value
  }
  return option
}

const getOptionLabel = (option: SelectOption | string | number): string => {
  if (typeof option === 'object') {
    return option.label
  }
  return String(option)
}

const selectClasses = computed(() => {
  return [
    'base-select',
    {
      'select-error': props.error,
      'select-disabled': props.disabled,
    },
  ]
})
</script>

<style scoped>
.select-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
  width: 100%;
}

.select-label {
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 0.25rem;
}

.required-indicator {
  color: var(--color-error);
  margin-left: 0.125rem;
}

.select-container {
  position: relative;
}

.base-select {
  width: 100%;
  padding: 0.5rem 2.5rem 0.5rem 0.75rem;
  font-size: var(--font-size-base);
  line-height: 1.5;
  color: var(--color-text-primary);
  background-color: var(--color-bg-secondary);
  border: 1px solid var(--color-border-dark);
  border-radius: var(--radius-md);
  transition: var(--transition-base);
  font-family: inherit;
  cursor: pointer;
  appearance: none;
}

.base-select:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.select-error {
  border-color: var(--color-error);
}

.select-error:focus {
  border-color: var(--color-error-dark);
  box-shadow: var(--shadow-md);
}

.select-disabled {
  background-color: var(--color-bg-tertiary);
  cursor: not-allowed;
  opacity: 0.6;
}

.select-icon {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  width: 1.25rem;
  height: 1.25rem;
  color: var(--color-text-secondary);
  pointer-events: none;
}

.select-icon svg {
  width: 100%;
  height: 100%;
}

.error-message {
  font-size: var(--font-size-sm);
  color: var(--color-error);
  margin: 0;
}

.hint-message {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
  margin: 0;
}
</style>
