<script lang="ts" setup>
import { onMounted } from 'vue'
import NavBar from '@/components/NavBar.vue'
import GlobalToast from '@/components/ui/GlobalToast.vue'
import { useUserStore } from '@/stores/user-store.js'

const userStore = useUserStore()

// Load user from localStorage on app mount
onMounted(() => {
  userStore.loadFromStorage()
})

function handleLogout() {
  userStore.logout()
}
</script>

<template>
  <div id="app">
    <header>
      <NavBar
        :loggedIn="userStore.isLoggedIn"
        :username="userStore.userFullName"
        @logout="handleLogout"
      />
    </header>

    <main>
      <router-view />
    </main>
    <GlobalToast />
  </div>
</template>

<style>
#app {
  margin: 0 auto;
  text-align: center;
  font-family: Noto Sans, system-ui, -apple-system, sans-serif;
}

header {
  margin-bottom: 1rem;
}
</style>
