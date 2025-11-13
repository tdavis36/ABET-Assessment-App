<script lang="ts" setup>
import { ref } from 'vue'
import { BaseButton, BaseCard, BaseInput } from "@/components/ui"
import { useRouter } from 'vue-router'
import { useToast } from "@/composables/use-toast.ts"
import { useUserStore } from '@/stores/user-store.ts'

const router = useRouter()
const toast = useToast()
const userStore = useUserStore()

const email_input = ref('')
const password_input = ref('')

async function login() {
  try {
    await userStore.login(email_input.value, password_input.value)

    toast.success("Successfully logged in!", "Welcome")

    if (userStore.isAdmin) {
      await router.push('/admin-dashboard')
    } else if (userStore.isInstructor) {
      await router.push('/instructor-dashboard')
    } else {
      await router.push('/')
    }
  } catch (error) {
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
        <BaseInput
          id="email"
          class="login-input"
          v-model="email_input"
          placeholder="Email"
          :disabled="userStore.isLoading"
        />

        <BaseInput
          id="password"
          class="login-input"
          v-model="password_input"
          placeholder="Password"
          type="password"
          :disabled="userStore.isLoading"
          @keyup.enter="login"
        />

        <BaseButton
          id="submit"
          class="submit-button"
          @click="login"
          :loading="userStore.isLoading"
          :disabled="!email_input || !password_input || userStore.isLoading"
        >
          Submit
        </BaseButton>
      </div>

      <p class="tooltip">
        Don't have an account? Sign up
        <router-link to="/signup">here</router-link>.
      </p>
    </BaseCard>
  </div>
</template>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 50vw;
}

.login-input {
  margin-bottom: var(--spacing-md);
}
</style>
