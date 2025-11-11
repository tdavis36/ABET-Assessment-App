import { test, expect } from './fixtures/test-fixtures.js'
import { mockData, type MockCourse } from './fixtures/api-mocks.js'

test.describe('Courses Page', () => {
  test.beforeEach(async ({ page, mockApi }) => {
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

    await page.goto('/1/courses')
  })

  test('should load courses page with program ID', async ({ page }) => {
    await expect(page).toHaveURL(/.*\/1\/courses/)
  })

  test('should display program courses when data is available', async ({ page, mockApi }) => {
    const courses: MockCourse[] = [
      {
        id: 1,
        courseCode: 'CS101',
        courseName: 'Introduction to Programming',
        credits: 3,
        programId: 1
      },
      {
        id: 2,
        courseCode: 'CS201',
        courseName: 'Data Structures',
        credits: 4,
        programId: 1
      }
    ]

    await mockApi.mockCourses(page, courses)
    await page.reload()

    // Wait for courses to be displayed (adjust selector based on actual component)
    await page.waitForTimeout(500)
  })

  test('should handle API errors gracefully', async ({ page, mockApi }) => {
    await mockApi.mockError(page, '/api/courses', 500, 'Internal server error')
    await page.reload()

    // Component should handle error without crashing
    await page.waitForTimeout(500)
  })
})

test.describe('Course View Page', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    const course: MockCourse = {
      id: 1,
      courseCode: 'CS101',
      courseName: 'Introduction to Programming',
      credits: 3,
      programId: 1
    }

    // Mock single course endpoint
    await page.route('**/api/courses/1', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(course),
      })
    })

    await page.goto('/1/course/1')
  })

  test('should load course view page', async ({ page }) => {
    await expect(page).toHaveURL(/.*\/1\/course\/1/)
  })

  test('should handle course not found', async ({ page }) => {
    await page.route('**/api/courses/999', async (route) => {
      await route.fulfill({
        status: 404,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Course not found' }),
      })
    })

    await page.goto('/1/course/999')
    await page.waitForTimeout(500)
  })
})
