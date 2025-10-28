<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const firstname_input = ref('')
const lastname_input = ref('')
const email_input = ref('')
const password_input = ref('')
const confirm_password_input = ref('')

const display_error = ref(false)
const error_message = ref('')

const router = useRouter()

async function signup() {
  if (password_input.value !== confirm_password_input.value) {
    display_error.value = true
    error_message.value = 'Passwords do not match.'
    return
  }

  try {
    // Send signup data to backend
    const response = await fetch('/api/users/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        first_name: firstname_input.value,
        last_name: lastname_input.value,
        email: email_input.value,
        password: password_input.value,
      }),
    })

    if (!response.ok) {
      const errText = await response.text()
      display_error.value = true
      error_message.value = errText || 'Unable to create account.'
      return
    }

    const data = await response.json()

    if (data.token) {
      localStorage.setItem('authToken', data.token)
    }

    router.push('/')
  } catch (error) {
    display_error.value = true
    error_message.value = 'Unable to reach server.'
  }
}
</script>

<template>
  <div id="login_component">
    <h3>Sign Up</h3>
    <div class="input_div">
      <input v-model="firstname_input" placeholder="First Name" />
    </div>
    <div class="input_div">
      <input v-model="lastname_input" placeholder="Last Name" />
    </div>
    <div class="input_div">
      <input v-model="email_input" placeholder="Email" />
    </div>
    <div class="input_div">
      <input v-model="password_input" placeholder="Password" type="password" />
    </div>
    <div class="input_div">
      <input v-model="confirm_password_input" placeholder="Confirm Password" type="password" />
    </div>
    <div id="submit_div">
      <button id="submit" @click="signup">Submit</button>
    </div>
    <p>Already have an account? Log in <router-link to="/login">here</router-link>.</p>
  </div>

  <div v-if="display_error" id="error">
    <p>Error: {{ error_message }}</p>
  </div>
</template>

<style>
#login_component {
  padding: 1rem;
  background-color: #e2e2e2;
  width: 30%;
  border: 1px solid black;
  padding: 2rem;
  margin: auto;
  text-align: left;
}

.input_div {
  margin-top: 1rem;
  margin-bottom: 1rem;
}

#submit_div {
  margin: auto;
}

h3 {
  margin-top: 1rem;
}

#error {
  background-color: rgb(255, 168, 168);
  border: 1px solid red;
  width: 30%;
  margin-left: auto;
  margin-right: auto;
  margin-top: 0.5rem;
}

#error p {
  color: rgb(204, 0, 0);
}
</style>
