import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import App from '@/App.vue'
import { useUserStore } from '@/stores/user-store'
import { routerStubs } from './helpers/router'

describe('App', () => {
  beforeEach(() => {
    const pinia = createPinia()
    setActivePinia(pinia)
    localStorage.clear()
  })

  it('renders properly', () => {
    const wrapper = mount(App, {
      global: {
        stubs: routerStubs,
      },
    })
    expect(wrapper.text()).toContain('ABET Assessment App')
  })

  it('renders layout elements properly', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          NavBar: true,
          RouterView: true,
          GlobalToast: true, // Stub all child components
        },
      },
    })
    expect(wrapper.find('#app').exists()).toBe(true)
    expect(wrapper.find('header').exists()).toBe(true)
    expect(wrapper.find('main').exists()).toBe(true)
    expect(wrapper.find('footer').exists()).toBe(true)
  })

  it('initializes with default (logged out) user state', () => {
    // Mount the app. `onMounted` will fire, `loadFromStorage` will run,
    // and find an empty localStorage. The store will be in its default state.
    const wrapper = mount(App, {
      global: {
        stubs: {
          NavBar: true,
          RouterView: true,
          GlobalToast: true,
        },
      },
    })

    // Get the NavBar component
    const navBar = wrapper.findComponent({ name: 'NavBar' })

    // Check that NavBar receives correct default props
    // Based on your store, default `user` and `authToken` are null
    expect(navBar.props('loggedIn')).toBe(false) // `isLoggedIn` getter will be false
    expect(navBar.props('username')).toBe('')   // `userFullName` getter will be ""
  })

  it('handles logout correctly', async () => {
    // 1. Mount the component (it will be in a logged-out state)
    const wrapper = mount(App, {
      global: {
        stubs: {
          NavBar: true,
          RouterView: true,
          GlobalToast: true,
        },
      },
    })

    // 2. Get the store instance and NavBar
    const userStore = useUserStore()
    const navBar = wrapper.findComponent({ name: 'NavBar' })

    // 3. Verify it's initially logged out
    expect(navBar.props('loggedIn')).toBe(false)

    // 4. Manually patch the store to simulate a login
    // This will trigger reactivity and update the component
    userStore.$patch({
      authToken: 'fake-token-123',
      user: {
        id: 1,
        email: 'test@user.com',
        firstName: 'Test',
        lastName: 'User',
        role: 'USER',
      },
    })

    // 5. Wait for Vue to process the state change
    await wrapper.vm.$nextTick()

    // 6. Verify the component updated to a "logged in" state
    expect(navBar.props('loggedIn')).toBe(true) // `isLoggedIn` is now true
    expect(navBar.props('username')).toBe('Test User') // `userFullName` is now "Test User"

    // 7. Emit logout event from NavBar, which triggers `handleLogout`
    await navBar.vm.$emit('logout')

    // 8. Wait for Vue to process the logout action
    await wrapper.vm.$nextTick()

    // 9. Verify state changed back to "logged out"
    // `logout` action sets user and authToken to null
    expect(navBar.props('loggedIn')).toBe(false)
    expect(navBar.props('username')).toBe('')
  })
})
