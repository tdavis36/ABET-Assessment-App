<script lang="ts" setup>
import { onMounted } from 'vue'
import NavBar from '@/components/NavBar.vue'
import GlobalToast from '@/components/ui/GlobalToast.vue'
import { useUserStore } from '@/stores/user-store.js'

const userStore = useUserStore()

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

    <footer class="footer">
      <hr />
      <p>© 2025 ABET Assessment App</p>
      <p>Definitions adapted from ABET documentation.</p>
    </footer>

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

.footer {
  text-align: center;
  color: #555;
  font-size: 0.9rem;
  margin-top: 3rem;
}

.footer hr {
  border: none;
  border-top: 1px solid #ccc;
  margin-bottom: 1rem;
  width: 100%;
}
</style>
