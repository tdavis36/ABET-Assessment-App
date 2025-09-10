<template>
  <div class="test-container">
    <h1>Frontend/Backend Connection Test</h1>

    <div class="test-section">
      <h2>1. Simple GET Request</h2>
      <button @click="testHello" :disabled="loading">Test Hello</button>
      <div v-if="helloResult" class="result">
        <strong>Response:</strong> {{ helloResult }}
      </div>
    </div>

    <div class="test-section">
      <h2>2. Status Check</h2>
      <button @click="checkStatus" :disabled="loading">Check Status</button>
      <div v-if="statusResult" class="result">
        <strong>Status:</strong> {{ statusResult }}
      </div>
    </div>

    <div class="test-section">
      <h2>3. POST Request Test</h2>
      <input
        v-model="messageInput"
        placeholder="Enter a message"
        @keyup.enter="testEcho"
      />
      <button @click="testEcho" :disabled="loading || !messageInput">Send Echo</button>
      <div v-if="echoResult" class="result">
        <strong>Echo Response:</strong> {{ echoResult }}
      </div>
    </div>

    <div class="test-section">
      <h2>Connection Status</h2>
      <div :class="['status-indicator', connectionStatus]">
        {{ connectionStatus === 'connected' ? '✅ Connected' : '❌ Disconnected' }}
      </div>
    </div>

    <div v-if="error" class="error">
      <strong>Error:</strong> {{ error }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const loading = ref(false)
const helloResult = ref('')
const statusResult = ref('')
const echoResult = ref('')
const messageInput = ref('')
const error = ref('')
const connectionStatus = ref('disconnected')

const API_BASE = 'http://localhost:8080/api'

async function makeRequest(url: string, options: RequestInit = {}) {
  loading.value = true
  error.value = ''

  try {
    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
      },
      ...options
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    connectionStatus.value = 'connected'
    return data
  } catch (err) {
    connectionStatus.value = 'disconnected'
    error.value = err instanceof Error ? err.message : 'Unknown error'
    throw err
  } finally {
    loading.value = false
  }
}

async function testHello() {
  try {
    const result = await makeRequest(`${API_BASE}/hello`)
    helloResult.value = JSON.stringify(result, null, 2)
  } catch (err) {
    console.error('Hello test failed:', err)
  }
}

async function checkStatus() {
  try {
    const result = await makeRequest(`${API_BASE}/status`)
    statusResult.value = JSON.stringify(result, null, 2)
  } catch (err) {
    console.error('Status check failed:', err)
  }
}

async function testEcho() {
  if (!messageInput.value) return

  try {
    const result = await makeRequest(`${API_BASE}/echo`, {
      method: 'POST',
      body: JSON.stringify({ message: messageInput.value })
    })
    echoResult.value = JSON.stringify(result, null, 2)
    messageInput.value = ''
  } catch (err) {
    console.error('Echo test failed:', err)
  }
}
</script>

<style scoped>
.test-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.test-section {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

button {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin: 5px;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background: #0056b3;
}

input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin: 5px;
  width: 200px;
}

.result {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  padding: 10px;
  margin: 10px 0;
  border-radius: 4px;
  white-space: pre-wrap;
  font-family: monospace;
}

.error {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 10px;
  margin: 10px 0;
  border-radius: 4px;
}

.status-indicator {
  padding: 10px;
  border-radius: 4px;
  font-weight: bold;
}

.status-indicator.connected {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-indicator.disconnected {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style>
