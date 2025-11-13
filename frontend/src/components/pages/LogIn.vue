<script lang="ts" setup>
import { ref } from 'vue';
import { BaseButton, BaseCard, BaseInput } from "@/components/ui";
import { useRouter } from 'vue-router'
import { useToast } from "@/composables/useToast.ts";
import { useUserStore } from '@/stores/user-store.ts'

const router = useRouter()
const toast = useToast()
const userStore = useUserStore()

const email_input = ref('')
const password_input = ref('')

async function login() {
  try {
    await userStore.login(email_input.value, password_input.value)

    // Success - show toast and redirect
    toast.success("Successfully logged in!", "Welcome")

    // Redirect based on role
    if (userStore.isAdmin) {
      router.push('/admin-dashboard')
    } else if (userStore.isInstructor) {
      router.push('/instructor-dashboard')
    } else {
      router.push('/')
    }
  } catch (error) {
    // Error toast - userStore.error contains the error message
    toast.error(
      userStore.error || "Email or password is incorrect.",
      "Login Failed"
    )
  }
}
</script>

<template>
  <div class="login">
  <BaseCard class="login-card" title="Log In">
    <div>
      <div>
        <BaseInput
          id="email"
          class="login-input"
          v-model="email_input"
          placeholder="Email"
          :disabled="userStore.isLoading"
        />
      </div>
      <div>
        <BaseInput
          id="password"
          class="login-input"
          v-model="password_input"
          placeholder="Password"
          type="password"
          :disabled="userStore.isLoading"
          @keyup.enter="login"
        />
      </div>
      <div>
        <BaseButton
          id="submit"
          class="submit-button"
          @click="login"
          :loading="userStore.isLoading"
          :disabled="!email_input || !password_input"
        >
          Submit
        </BaseButton>
      </div>
    </div>
    <p class="tooltip">Don't have an account? Sign up <router-link to="/signup">here</router-link>.</p>
  </BaseCard>
  </div>
</template>

<style scoped>
.login {
  display: flex;
  justify-content: center;
}

.login-card {
  width: 70vw;
}

.login-input {
  padding-bottom: var(--spacing-md);
}

.tooltip {
  padding-top: var(--spacing-md);
}
</style>
