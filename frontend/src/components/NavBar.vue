<script lang="ts" setup>
const props = defineProps({
  loggedIn: Boolean,
  username: String,
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
        <div class="nav-divider"></div>
        <router-link to="/test-connection" class="nav_button">Test Connection</router-link>

        <!-- Instructor dashboard link only -->
        <template v-if="loggedIn && !isAdmin">
          <div class="nav-divider"></div>
          <router-link to="/instructor-dashboard" class="nav_button">
            Instructor Dashboard
          </router-link>
        </template>
      </div>

      <!-- Log In / Log Out -->
      <div class="navbar-auth">
        <div class="nav-divider"></div>
        <router-link
          v-if="loggedIn"
          to="/"
          class="nav_button auth-button"
          id="logout"
          @click="$emit('logout')"
        >
          <span class="user-info">{{ username }}</span>
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
  background-color: var(--navbar-bg);
  width: 100%;
  height: var(--navbar-height);
  box-shadow: var(--shadow-lg);
}

.navbar-container {
  display: flex;
  align-items: stretch;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--navbar-padding-y) var(--navbar-padding-x);
}

#logo {
  display: flex;
  align-items: center;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--navbar-text);
  padding: 0 var(--spacing-xl);
  border-right: 2px solid var(--navbar-divider);
  white-space: nowrap;
  letter-spacing: 0.5px;
  flex-shrink: 0;
}

.navbar-links {
  display: flex;
  align-items: stretch;
  flex: 1;
}

.navbar-auth {
  display: flex;
  align-items: stretch;
  margin-left: auto;
}

.nav-divider {
  width: 1px;
  background-color: var(--navbar-divider);
  align-self: stretch;
}

.nav_button {
  display: flex;
  align-items: center;
  padding: 0 var(--navbar-item-padding-x);
  color: var(--navbar-text);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: background-color var(--transition-base);
  gap: var(--spacing-sm);
  white-space: nowrap;
  height: 100%;
}

.nav_button:hover {
  background-color: var(--navbar-hover-bg);
}

.nav_button.router-link-active {
  background-color: var(--navbar-active-bg);
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
    padding: var(--navbar-padding-y) var(--spacing-md);
  }

  #logo {
    font-size: var(--font-size-lg);
    padding: 0 var(--spacing-lg);
  }

  .nav_button {
    padding: 0 var(--spacing-md);
    font-size: var(--font-size-sm);
  }
}

@media (max-width: 768px) {
  #navbar {
    height: auto;
  }

  .navbar-container {
    flex-direction: column;
    align-items: stretch;
    padding: 0;
  }

  #logo {
    font-size: var(--font-size-base);
    padding: var(--spacing-md);
    border-right: none;
    border-bottom: 2px solid var(--navbar-divider);
    justify-content: center;
  }

  .navbar-links {
    flex-direction: column;
    width: 100%;
  }

  .navbar-auth {
    width: 100%;
    margin-left: 0;
    flex-direction: column;
  }

  .nav-divider {
    width: 100%;
    height: 1px;
  }

  .nav_button {
    padding: var(--spacing-md);
    font-size: var(--font-size-sm);
    justify-content: center;
    height: auto;
  }

  .user-info {
    font-size: var(--font-size-xs);
  }
}
</style>
