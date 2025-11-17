<script setup lang="ts">
interface Props {
  mid?: number
}

const props = defineProps<Props>()
</script>

<template>
  <p>Measure {{ mid }}</p>
</template>
