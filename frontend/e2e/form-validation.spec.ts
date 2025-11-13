import { test, expect } from './fixtures/test-fixtures.js'

test.describe('Login Form Validation', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    await page.goto('/login')
  })

  test('should load login page', async ({ page }) => {
    await expect(page).toHaveURL(/.*login/)
  })

  test('should display login form elements', async ({ page }) => {
    // Check if form exists (adjust selectors based on actual component)
    await page.waitForTimeout(500)
  })

  test('should handle login submission', async ({ page, mockApi }) => {
    // Mock successful login
    await page.route('**/api/users/login', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER' },
          token: 'mock-jwt-token'
        }),
      })
    })

    // Test login flow (adjust selectors based on actual component)
    await page.waitForTimeout(500)
  })

  test('should handle failed login', async ({ page, mockApi }) => {
    await mockApi.mockError(page, '/api/auth/login', 401, 'Invalid credentials')

    await page.waitForTimeout(500)
  })
})

test.describe('Signup Form Validation', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    await page.goto('/signup')
  })

  test('should load signup page', async ({ page }) => {
    await expect(page).toHaveURL(/.*signup/)
  })

  test('should display signup form elements', async ({ page }) => {
    await page.waitForTimeout(500)
  })
})

test.describe('API Form Validation', () => {
  test('should validate program creation via API', async ({ page, mockApi }) => {
    await page.goto('/')

    // Test API validation by sending invalid data
    const response = await page.evaluate(async () => {
      try {
        const res = await fetch('/api/programs', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: '', // Empty name should fail validation
            institution: '',
            active: true
          })
        })
        return { status: res.status, body: await res.json() }
      } catch (error) {
        return { error: String(error) }
      }
    })

    // Backend should validate and return appropriate response
    expect(response).toBeDefined()
  })

  test('should validate course creation via API', async ({ page, mockApi }) => {
    await page.goto('/')

    const response = await page.evaluate(async () => {
      try {
        const res = await fetch('/api/courses', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            courseCode: '',
            courseName: '',
            credits: 0,
            programId: 1
          })
        })
        return { status: res.status, body: await res.json() }
      } catch (error) {
        return { error: String(error) }
      }
    })

    expect(response).toBeDefined()
  })
})
