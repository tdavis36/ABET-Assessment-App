<template>
  <div :class="cardClasses">
    <div v-if="$slots.header || title" class="card-header">
      <slot name="header">
        <h3 class="card-title">{{ title }}</h3>
      </slot>
    </div>

    <div class="card-body" :class="{ 'card-body-padded': padded }">
      <slot></slot>
    </div>

    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

export interface CardProps {
  title?: string
  variant?: 'default' | 'bordered' | 'elevated'
  padded?: boolean
  hoverable?: boolean
}

const props = withDefaults(defineProps<CardProps>(), {
  variant: 'default',
  padded: true,
  hoverable: false,
})

const cardClasses = computed(() => {
  return [
    'base-card',
    `card-${props.variant}`,
    {
      'card-hoverable': props.hoverable,
    },
  ]
})
</script>

<style scoped>
.base-card {
  background-color: white;
  border-radius: 0.5rem;
  overflow: hidden;
  transition: all 0.2s ease-in-out;
}

.card-default {
  border: 1px solid #e5e7eb;
}

.card-bordered {
  border: 2px solid #d1d5db;
}

.card-elevated {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.card-hoverable:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.card-body {
  background-color: white;
}

.card-body-padded {
  padding: 1.5rem;
}

.card-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  background-color: #f9fafb;
}
</style>
