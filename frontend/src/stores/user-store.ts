import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface User {
  id: number
  username?: string
  email: string
  firstName?: string
  lastName?: string
  role: 'ADMIN' | 'INSTRUCTOR' | 'STUDENT' | 'USER'
}

export const useUserStore = defineStore('user', () => {
  // State
  const user = ref<User | null>(null)
  const authToken = ref<string | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const isLoggedIn = computed(() => user.value !== null && authToken.value !== null)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isInstructor = computed(() => user.value?.role === 'INSTRUCTOR')
  const userId = computed(() => user.value?.id ?? 0)
  const userFullName = computed(() => {
    if (!user.value) return ''
    if (user.value.firstName && user.value.lastName) {
      return `${user.value.firstName} ${user.value.lastName}`
    }
    return user.value.username || user.value.email
  })

  // Actions
  async function login(email: string, password: string) {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('/api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      })

      if (!response.ok) {
        const errorData = await response.text()
        throw new Error(errorData || 'Login failed')
      }

      const data = await response.json()

      // Store the user and token
      user.value = data.user
      authToken.value = data.token

      // Persist to localStorage
      localStorage.setItem('authToken', data.token)
      localStorage.setItem('currentUser', JSON.stringify(data.user))

      return data
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Login failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function signup(userData: {
    firstName: string
    lastName: string
    email: string
    password: string
  }) {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('/api/users/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      })

      if (!response.ok) {
        const errorData = await response.text()
        throw new Error(errorData || 'Signup failed')
      }

      const data = await response.json()

      // Store the user and token
      user.value = data.user
      authToken.value = data.token

      // Persist to localStorage
      if (data.token) {
        localStorage.setItem('authToken', data.token)
        localStorage.setItem('currentUser', JSON.stringify(data.user))
      }

      return data
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Signup failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    user.value = null
    authToken.value = null
    error.value = null

    // Clear localStorage
    localStorage.removeItem('authToken')
    localStorage.removeItem('currentUser')
  }

  function loadFromStorage() {
    const token = localStorage.getItem('authToken')
    const userData = localStorage.getItem('currentUser')

    if (token && userData) {
      try {
        authToken.value = token
        user.value = JSON.parse(userData)
      } catch (err) {
        console.error('Failed to parse user data from localStorage:', err)
        logout()
      }
    }
  }

  async function refreshUser() {
    if (!authToken.value) return

    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('/api/users/', {
        headers: {
          Authorization: `Bearer ${authToken.value}`,
        },
      })

      if (!response.ok) {
        throw new Error('Failed to refresh user data')
      }

      const userData = await response.json()
      user.value = userData

      // Update localStorage
      localStorage.setItem('currentUser', JSON.stringify(userData))
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to refresh user'
      logout()
    } finally {
      isLoading.value = false
    }
  }

  function setUser(userData: User) {
    user.value = userData
    localStorage.setItem('currentUser', JSON.stringify(userData))
  }

  function setToken(token: string) {
    authToken.value = token
    localStorage.setItem('authToken', token)
  }

  return {
    // State
    user,
    authToken,
    isLoading,
    error,

    // Getters
    isLoggedIn,
    isAdmin,
    isInstructor,
    userId,
    userFullName,

    // Actions
    login,
    signup,
    logout,
    loadFromStorage,
    refreshUser,
    setUser,
    setToken,
  }
})
