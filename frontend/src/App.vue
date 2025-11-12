<script lang="ts" setup>
import { ref } from 'vue'
import NavBar from '@/components/NavBar.vue'
import GlobalToast from '@/components/ui/GlobalToast.vue'
import '@/assets/styles/variables.css'

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
    <GlobalToast />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  text-align: center;
  font-family: var(--font-family-sans), sans-serif;
  background-color: var(--color-bg-primary);
}

header {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  box-shadow: var(--shadow-md);
}

main {
  flex: 1;
  padding: var(--spacing-xl);
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
}
</style>
