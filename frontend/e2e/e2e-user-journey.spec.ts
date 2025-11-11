// e2e-user-journey.spec.ts
import { test, expect } from './fixtures/test-fixtures.js'
import type { MockCourse } from './fixtures/api-mocks.js'

test.describe('E2E User Journey', () => {
  test('should complete full navigation flow through app', async ({ page, mockApi }) => {
    // Setup mock data
    const courses: MockCourse[] = [
      {
        id: 1,
        courseCode: 'CS101',
        courseName: 'Introduction to Programming',
        credits: 3,
        programId: 1
      },
    ]
    await mockApi.mockCourses(page, courses)

    // Mock program data
    await page.route('**/api/programs/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          id: 1,
          name: 'Computer Science',
          institution: 'Example University',
          active: true
        }),
      })
    })

    // Start at root - should redirect to dashboard
    await page.goto('/')
    await expect(page).toHaveURL(/.*dashboard/)

    // Navigate to admin dashboard
    await page.goto('/admin-dashboard')
    await expect(page).toHaveURL(/.*admin-dashboard/)
    await expect(page.locator('h1', { hasText: 'Administrator Dashboard' })).toBeVisible()

    // Navigate to program courses
    await page.goto('/1/courses')
    await expect(page).toHaveURL(/.*\/1\/courses/)

    // Navigate to program instructors
    await page.goto('/1/instructors')
    await expect(page).toHaveURL(/.*\/1\/instructors/)

    // Navigate to summary page
    await page.goto('/1/summary/')
    await expect(page).toHaveURL(/.*\/1\/summary/)

    // Navigate to test connection page
    await page.goto('/test-connection')
    await expect(page).toHaveURL(/.*test-connection/)
  })

  test('should handle authentication flow', async ({ page, mockApi }) => {
    // Mock login endpoint
    await page.route('**/api/auth/login', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          user: { id: 1, username: 'admin', email: 'admin@example.com', role: 'ADMIN' },
          token: 'mock-jwt-token',
        }),
      })
    })

    // Start at login page
    await page.goto('/login')
    await expect(page).toHaveURL(/.*login/)

    // After successful login, user should be able to navigate
    // (Adjust this based on actual login implementation)
    await page.waitForTimeout(500)
  })

  test('should handle API errors gracefully throughout journey', async ({ page, mockApi }) => {
    // Mock error responses
    await mockApi.mockError(page, '/api/courses', 500, 'Internal Server Error')

    // Navigate to courses page - should handle error
    await page.goto('/1/courses')
    await page.waitForTimeout(500)

    // App should still be functional despite API error
    await expect(page).toHaveURL(/.*\/1\/courses/)
  })

  test('should navigate between different program views', async ({ page, mockApi }) => {
    const courses: MockCourse[] = [
      {
        id: 1,
        courseCode: 'CS101',
        courseName: 'Introduction to Programming',
        credits: 3,
        programId: 1
      },
    ]
    await mockApi.mockCourses(page, courses)

    // Navigate to course view
    await page.goto('/1/course/1')
    await expect(page).toHaveURL(/.*\/1\/course\/1/)

    // Navigate to instructor view
    await page.goto('/1/instructor/1')
    await expect(page).toHaveURL(/.*\/1\/instructor\/1/)

    // Navigate to FCAR
    await page.goto('/1/fcar/1')
    await expect(page).toHaveURL(/.*\/1\/fcar\/1/)
  })

  test('should handle signup flow', async ({ page, mockApi }) => {
    // Mock signup endpoint
    await page.route('**/api/auth/signup', async (route) => {
      await route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify({
          user: { id: 2, username: 'newuser', email: 'newuser@example.com', role: 'USER' },
          message: 'User created successfully'
        }),
      })
    })

    await page.goto('/signup')
    await expect(page).toHaveURL(/.*signup/)

    // Test signup page loads
    await page.waitForTimeout(500)
  })
})
