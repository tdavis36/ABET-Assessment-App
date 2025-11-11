import { test, expect } from './fixtures/test-fixtures.js'
import { mockData, type MockProgram } from './fixtures/api-mocks.js'

test.describe('Programs API Integration', () => {
  test('should fetch programs from API', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    // Test that programs API is called and returns data
    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs?page=0&size=10')
      return await res.json()
    })

    expect(response).toHaveProperty('content')
    expect(response.content).toBeInstanceOf(Array)
    expect(response.content.length).toBeGreaterThan(0)
  })

  test('should fetch single program by ID', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs/1')
      return await res.json()
    })

    expect(response).toHaveProperty('id')
    expect(response.id).toBe(1)
    expect(response).toHaveProperty('name')
    expect(response).toHaveProperty('institution')
  })

  test('should handle program not found', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs/999')
      return { status: res.status, body: await res.json() }
    })

    expect(response.status).toBe(404)
    expect(response.body.message).toContain('not found')
  })

  test('should create new program via API', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: 'New Test Program',
          institution: 'Test University',
          active: true
        })
      })
      return await res.json()
    })

    expect(response).toHaveProperty('id')
    expect(response.name).toBe('New Test Program')
    expect(response.institution).toBe('Test University')
  })

  test('should update program via API', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs/1', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: 'Updated Program Name',
          institution: 'Example University',
          active: true
        })
      })
      return await res.json()
    })

    expect(response).toHaveProperty('name')
    expect(response.name).toBe('Updated Program Name')
  })

  test('should delete program via API', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs/1', {
        method: 'DELETE'
      })
      return res.status
    })

    expect(response).toBe(204)
  })

  test('should handle paginated programs response', async ({ page, mockApi }) => {
    await mockApi.mockPrograms(page, mockData.programs)

    await page.goto('/')

    const response = await page.evaluate(async () => {
      const res = await fetch('http://localhost:8080/api/programs?page=0&size=2')
      return await res.json()
    })

    expect(response).toHaveProperty('content')
    expect(response).toHaveProperty('totalElements')
    expect(response).toHaveProperty('totalPages')
    expect(response).toHaveProperty('size')
    expect(response).toHaveProperty('number')
    expect(response.content.length).toBeLessThanOrEqual(2)
  })
})

test.describe('Program Routes', () => {
  test('should navigate to program courses page', async ({ page, mockApi }) => {
    const courses = [
      { id: 1, courseCode: 'CS101', courseName: 'Intro to CS', credits: 3, programId: 1 }
    ]
    await mockApi.mockCourses(page, courses)

    await page.goto('/1/courses')
    await expect(page).toHaveURL(/.*\/1\/courses/)
  })

  test('should navigate to program instructors page', async ({ page, mockApi }) => {
    await page.goto('/1/instructors')
    await expect(page).toHaveURL(/.*\/1\/instructors/)
  })

  test('should navigate to program summary page', async ({ page, mockApi }) => {
    await page.goto('/1/summary/')
    await expect(page).toHaveURL(/.*\/1\/summary/)
  })
})
