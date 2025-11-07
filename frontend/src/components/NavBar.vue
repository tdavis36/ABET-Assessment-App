<script lang="ts" setup>
    import { ref } from 'vue'
    
    const props = defineProps({
        loggedIn: Boolean,
        userID:{
            type: Number,
            default: NaN
        },
        user_first_name: String
    })

    const isAdmin = true
    const emits = defineEmits(["logout"])
</script>
<template>
  <div id="navbar">
    <div id="logo">Abet Assessment App</div>

    <router-link to="/" class="nav_button">Home</router-link>
    <router-link to="/test-connection" class="nav_button">Test Connection</router-link>

    <!-- Instructor dashboard link only -->
    <router-link
      v-if="loggedIn && !isAdmin"
      to="/instructor-dashboard"
      class="nav_button"
    >
      Instructor Dashboard
    </router-link>

    
    <router-link
        v-if="loggedIn"
        to="/"
        class="nav_button"
        id="logout"
        @click="$emit('logout')"
    >
        Hello, <strong>{{ user_first_name }}</strong> | Log Out
    </router-link>

    <router-link
        v-else
        to="/login"
        class="nav_button"
        id="login"
    >
        Log In
    </router-link>
  </div>
  <h1 hidden>This hidden element checks if the site loaded</h1>
</template>


<style>
#navbar {
    display: flex;
    width: 80%;
    margin: auto;
}

#logo {
    padding: 2rem;
    border-right: 1px solid black;
}

.nav_button {
    padding: 2rem;
    color: black;
    text-decoration: none;
}

.nav_button:hover {
    background-color: rgb(200, 200, 200);
    color: black;
    text-decoration: none;
}

#login, #logout {
    margin-left: auto;
}
</style>
