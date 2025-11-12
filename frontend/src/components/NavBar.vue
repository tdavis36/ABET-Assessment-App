<script lang="ts" setup>
const props = defineProps({
  loggedIn: Boolean,
  userID: Number,
})


const isAdmin = true

const emits = defineEmits(["logout"])
</script>
<template>
  <nav id="navbar">
    <div class="navbar-container">
      <div id="logo">ABET Assessment App</div>

      <div class="navbar-links">
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
      </div>

      <!-- Log In / Log Out -->
      <div class="navbar-auth">
        <router-link
          v-if="loggedIn"
          to="/"
          class="nav_button auth-button"
          id="logout"
          @click="$emit('logout')"
        >
          <span class="user-info">{{ userID }}</span>
          <span class="separator">|</span>
          <span>Log Out</span>
        </router-link>

        <router-link
          v-else
          to="/login"
          class="nav_button auth-button"
          id="login"
        >
          Log In
        </router-link>
      </div>
    </div>
  </nav>
  <h1 hidden>This hidden element checks if the site loaded</h1>
</template>


<style scoped>
#navbar {
  background-color: var(--color-primary);
  width: 100%;
  padding: 0;
  box-shadow: var(--shadow-lg);
}

.navbar-container {
  display: flex;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
}

#logo {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  padding: var(--spacing-lg) var(--spacing-xl);
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  flex: 1;
  padding-left: var(--spacing-md);
}

.navbar-auth {
  margin-left: auto;
}

.nav_button {
  padding: var(--spacing-lg) var(--spacing-xl);
  background-color: var(--overlay-transparent);
  color: var(--color-text-primary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: background-color var(--transition-base);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  white-space: nowrap;
  border-radius: var(--radius-md);
}

.nav_button:hover {
  background-color: var(--color-primary-dark);
}

.nav_button.router-link-active {
  background-color: var(--color-primary-dark);
  font-weight: var(--font-weight-semibold);
}

.auth-button {
  font-weight: var(--font-weight-semibold);
}

.user-info {
  font-weight: var(--font-weight-bold);
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: var(--color-primary-dark);
  border-radius: var(--radius-md);
}

.separator {
  opacity: 0.6;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .navbar-container {
    padding: 0 var(--spacing-md);
  }

  #logo {
    font-size: var(--font-size-lg);
    padding: var(--spacing-md) var(--spacing-lg);
  }

  .nav_button {
    padding: var(--spacing-md) var(--spacing-lg);
    font-size: var(--font-size-sm);
  }
}

@media (max-width: 768px) {
  .navbar-container {
    flex-wrap: wrap;
    padding: var(--spacing-sm);
  }

  #logo {
    font-size: var(--font-size-base);
    padding: var(--spacing-md);
    width: 100%;
    text-align: center;
  }

  .navbar-links {
    width: 100%;
    justify-content: center;
    padding: var(--spacing-sm) 0;
    flex-wrap: wrap;
  }

  .navbar-auth {
    width: 100%;
    margin-left: 0;
    display: flex;
    justify-content: center;
    padding-bottom: var(--spacing-sm);
  }

  .nav_button {
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--font-size-xs);
  }
}
</style>
