<script lang="ts" setup>
  import { ref } from 'vue'
  import NavBar from '@/components/NavBar.vue'

  const userID = ref(310297)
  const loggedIn = ref(true)

  function handle_logout(){
    userID.value = 0
    loggedIn.value = false;
  }
</script>

<template>
  <div id="app">
    <header>
      <NavBar :loggedIn="loggedIn" :userID="userID" @logout="handle_logout" />
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
  font-family: Noto Sans, system-ui, -apple-system, sans-serif;
}

header {
  margin-bottom: 1rem;
}
</style>
