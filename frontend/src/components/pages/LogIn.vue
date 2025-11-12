<script lang="ts" setup>
import { ref } from 'vue';
import {BaseButton, BaseCard, BaseInput} from "@/components/ui";
import { useRouter } from 'vue-router'
import {useToast} from "@/composables/useToast.ts";

const API_BASE = 'http://localhost:8080/api'
const router = useRouter()

const email_input = ref('');
const password_input = ref('');
const toast = useToast();

const emits = defineEmits(["login"])

async function login() {
  try {
    // POST credentials to your backend
    const response = await fetch('/api/users/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email_input.value,
        password: password_input.value
      })
    });

    if (!response.ok) {
      // Show error toast instead of inline error
      const errText = await response.text();
      toast.error(errText || "Email or password is incorrect.", "Login Failed");
      return;
    }

    // Success case
    const json_obj = await response.json()
    toast.success("Successfully logged in!", "Welcome");
    emits("login", json_obj.data)
    router.push("/")

  } catch (error) {
    // Handle network errors
    toast.error("Unable to reach server. Please try again.", "Connection Error");
  }
}
</script>

<template>
  <BaseCard title="Log In">
    <div>
      <div>
        <BaseInput id="email" class="login-input" v-model="email_input" placeholder="Email"></BaseInput>
      </div>
      <div>
        <BaseInput id="password" class="login-input" v-model="password_input" placeholder="Password" type="password"></BaseInput>
      </div>
      <div><BaseButton id="submit" class="submit-button" @click="login">Submit</BaseButton></div>
    </div>
    <p class="tooltip">Don't have an account? Sign up <router-link to="/signup">here</router-link>.</p>
  </BaseCard>
</template>

<style>
.login-input {
  padding-bottom: var(--spacing-md);
}

.submit-button {
  padding-bottom: var(--spacing-md);
}

.tooltip {
  padding-top: var(--spacing-md);
}
</style>
