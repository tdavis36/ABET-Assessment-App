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

header {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

nav {
  margin: 1rem 0;
}

nav a {
  margin: 0 1rem;
  color: #007bff;
  text-decoration: none;
}

nav a:hover {
  text-decoration: underline;
}

nav a.router-link-active {
  color: #0056b3;
  font-weight: bold;
}

main {
  margin-top: 2rem;
}
</style>
