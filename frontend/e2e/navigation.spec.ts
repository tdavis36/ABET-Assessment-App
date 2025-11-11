// navigation.spec.ts
import { test, expect } from './fixtures/test-fixtures.js'
import type { MockCourse } from './fixtures/api-mocks.js'

test.describe('Navigation Tests', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    // Mock programs for the routes that require program_id
    await page.route('**/api/programs/**', async (route) => {
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

    await page.goto('/')
  })

  test('should redirect root to dashboard', async ({ page }) => {
    await page.goto('/')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('should navigate to dashboard', async ({ page }) => {
    await page.goto('/dashboard')
    await expect(page).toHaveURL(/.*dashboard/)
  })

  test('should navigate to admin dashboard', async ({ page }) => {
    await page.goto('/admin-dashboard')
    await expect(page).toHaveURL(/.*admin-dashboard/)
    await expect(page.locator('h1', { hasText: 'Administrator Dashboard' })).toBeVisible()
  })

  test('should navigate to test connection page', async ({ page }) => {
    await page.goto('/test-connection')
    await expect(page).toHaveURL(/.*test-connection/)
  })

  test('should navigate to login page', async ({ page }) => {
    await page.goto('/login')
    await expect(page).toHaveURL(/.*login/)
  })

  test('should navigate to signup page', async ({ page }) => {
    await page.goto('/signup')
    await expect(page).toHaveURL(/.*signup/)
  })

  test('should navigate to program courses page with program ID', async ({ page, mockApi }) => {
    const courses: MockCourse[] = [
      {
        id: 1,
        courseCode: 'CS101',
        courseName: 'Introduction to Programming',
        credits: 3,
        programId: 1
      }
    ]
    await mockApi.mockCourses(page, courses)

    await page.goto('/1/courses')
    await expect(page).toHaveURL(/.*\/1\/courses/)
  })

  test('should navigate to program instructors page with program ID', async ({ page }) => {
    await page.goto('/1/instructors')
    await expect(page).toHaveURL(/.*\/1\/instructors/)
  })

  test('should navigate to course view page', async ({ page }) => {
    await page.goto('/1/course/1')
    await expect(page).toHaveURL(/.*\/1\/course\/1/)
  })

  test('should navigate to instructor view page', async ({ page }) => {
    await page.goto('/1/instructor/1')
    await expect(page).toHaveURL(/.*\/1\/instructor\/1/)
  })

  test('should navigate to summary page', async ({ page }) => {
    await page.goto('/1/summary/')
    await expect(page).toHaveURL(/.*\/1\/summary/)
  })

  test('should navigate to FCAR page', async ({ page }) => {
    await page.goto('/1/fcar/1')
    await expect(page).toHaveURL(/.*\/1\/fcar\/1/)
  })
})
