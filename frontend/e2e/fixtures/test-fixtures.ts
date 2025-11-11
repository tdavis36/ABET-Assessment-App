import { test as base } from '@playwright/test'
import { MockApiRoutes, mockData } from './api-mocks.js'

/**
 * Extended test fixtures with API mocking
 */
export const test = base.extend<{
  mockApi: MockApiRoutes
  authenticatedPage: void
}>({
  /**
   * Fixture that provides MockApiRoutes instance
   */
  mockApi: async ({ page }, use) => {
    const mockApi = new MockApiRoutes()
    await mockApi.setupAll(page)

    // Don't add a catch-all route - let specific mocks handle everything
    // Tests should explicitly mock what they need

    await use(mockApi)
  },

  /**
   * Fixture that automatically sets up authentication
   */
  authenticatedPage: async ({ page, mockApi }, use) => {
    // Mock authentication
    await mockApi.mockUsers(page)

    // Set authentication token in localStorage
    await page.goto('/')
    await page.evaluate(() => {
      localStorage.setItem('authToken', 'mock-jwt-token')
      localStorage.setItem(
        'currentUser',
        JSON.stringify({
          id: 1,
          username: 'admin',
          email: 'admin@example.com',
          role: 'ADMIN',
        })
      )
    })

    await use()
  },
})

export { expect } from '@playwright/test'
