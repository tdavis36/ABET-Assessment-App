<script lang="ts" setup>
  import { ref } from 'vue'
  import NavBar from '@/components/NavBar.vue'

  const userID = ref(NaN)
  const loggedIn = ref(false)

  function handle_logout(){
    userID.value = 0
    loggedIn.value = false;
  }

  function handle_login(user_id: number){
    userID.value = user_id
    loggedIn.value = true;
  }
</script>

<template>
  <div id="app">
    <header>
      <NavBar :loggedIn="loggedIn" :userID="userID" @logout="handle_logout" />
    </header>

    <main>
      <router-view @login="handle_login"/>
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
  margin-bottom: 1rem;
}
</style>
