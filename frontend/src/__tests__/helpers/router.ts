import { createRouter, createMemoryHistory } from 'vue-router'

/**
 * Creates a router instance for testing
 * Uses memory history to avoid browser dependencies
 */
export function createTestRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      {
        path: '/',
        name: 'Home',
        component: { template: '<div>Home</div>' },
      },
      {
        path: '/test-connection',
        name: 'TestConnection',
        component: { template: '<div>Test</div>' },
      },
      {
        path: '/login',
        name: 'Login',
        component: { template: '<div>Login</div>' },
      },
      // Add minimal routes as needed
    ],
  })
}

/**
 * Router stubs for components that use router-link or router-view
 * Use this when you don't need actual routing functionality
 */
export const routerStubs = {
  'router-link': {
    template: '<a><slot /></a>',
  },
  'router-view': {
    template: '<div><slot /></div>',
  },
}
