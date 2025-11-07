<script lang="ts" setup>
  import { ref } from 'vue'
  import NavBar from '@/components/NavBar.vue'

  const userID = ref(NaN)
  const user_name = ref({
    first: '',
    full: ''
  })
  const loggedIn = ref(false)

  function handle_logout(){
    userID.value = NaN;
    loggedIn.value = false;
  }

  function handle_login(user_id: number){
    userID.value = user_id
    loggedIn.value = true;
    retrieve_user_info(userID.value)
  }

  async function retrieve_user_info(user_id:number){
    try {
      const response = await fetch(`/api/users/${user_id}`);
      const data = await response.json(); // Await the JSON parsing
      console.log('Fetched JSON data:', data);
      user_name.value = {
        first: data.data.firstName,
        full: data.data.fullName
      }
    } catch (error) {
      console.error('Error fetching or parsing data:', error);
    }
  }
</script>

<template>
  <div id="app">
    <header>
      <NavBar :loggedIn="loggedIn" :userID="userID" :user_first_name="user_name.first" @logout="handle_logout" />
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
