<script lang="ts" setup>
  import NavBar from '@/components/NavBar.vue'
</script>

<template>
  <div id="app">
    <header>
      <NavBar />
    </header>

    <main>
      <router-view />
    </main>
  </div>
</template>

<style>
#app {
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

header{
  margin-bottom: 1rem;
}
</style>
