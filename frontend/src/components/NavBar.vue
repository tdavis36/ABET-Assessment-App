<script lang="ts" setup>
const props = defineProps({
  loggedIn: Boolean,
  username: String,
})

const emit = defineEmits(["logout"])
</script>

<template>
  <nav id="navbar">
    <div class="navbar-container">
      <div id="logo">ABET Assessment App</div>

      <div class="navbar-links">
        <router-link to="/" class="nav_button">Home</router-link>
        <div class="nav-divider"></div>
        <router-link to="/about" class="nav_button">About</router-link>
        <div class="nav-divider"></div>
        <router-link to="/fcar/1" class="nav_button">FCAR (Testing)</router-link>
        <div class="nav-divider"></div>
        <router-link to="/setup" class="nav_button">Setup</router-link>
      </div>

      <!-- USER + LOGIN/LOGOUT SECTION -->
      <div class="navbar-auth">
        <div class="nav-divider"></div>

        <!-- When logged in -->
        <template v-if="loggedIn">
          <!-- Username displayed separately -->
          <div class="username-display">
            {{ username }}
          </div>

          <div class="nav-divider"></div>

          <!-- Stand-alone logout button -->
          <button
            class="nav_button auth-button logout-btn"
            @click="$emit('logout')"
          >
            Log Out
          </button>
        </template>

        <!-- When logged out -->
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

.username-display {
  display: flex;
  align-items: center;
  padding: 0 var(--navbar-item-padding-x);
  font-weight: var(--font-weight-bold);
  border-radius: var(--radius-md);
  color: white;
  white-space: nowrap;
}

.logout-btn {
  background: none;
  color: var(--navbar-text);
  border: none;
  cursor: pointer;
  font-size: inherit;
  display: flex;
  align-items: center;
  padding: 0 var(--navbar-item-padding-x);
  height: 100%;
}

.logout-btn:hover {
  background-color: var(--navbar-hover-bg);
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
