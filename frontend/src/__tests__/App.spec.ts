import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import App from '../App.vue'

describe('App', () => {
  it('renders properly', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          NavBar: true,
          RouterView: true
        }
      }
    })
    expect(wrapper.find('#app').exists()).toBe(true)
    expect(wrapper.find('header').exists()).toBe(true)
    expect(wrapper.find('main').exists()).toBe(true)
  })

  it('initializes with default user state', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          NavBar: true,
          RouterView: true
        }
      }
    })

    // Check that NavBar receives correct props
    const navBar = wrapper.findComponent({ name: 'NavBar' })
    expect(navBar.props('loggedIn')).toBe(true)
    expect(navBar.props('userID')).toBe(310297)
  })

  it('handles logout correctly', async () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          NavBar: true,
          RouterView: true
        }
      }
    })

    // Emit logout event from NavBar
    const navBar = wrapper.findComponent({ name: 'NavBar' })
    await navBar.vm.$emit('logout')

    // Verify state changed
    expect(navBar.props('loggedIn')).toBe(false)
    expect(navBar.props('userID')).toBe(0)
  })
})
