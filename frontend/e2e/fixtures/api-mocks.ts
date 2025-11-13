import { Page, Route } from '@playwright/test'

/**
 * Mock API response types matching backend DTOs
 */
export interface MockProgram {
  id: number
  name: string
  institution: string
  active: boolean
}

export interface MockCourse {
  id: number
  courseCode: string
  courseName: string
  credits: number
  programId: number
}

export interface MockSemester {
  id: number
  name: string
  startDate: string
  endDate: string
}

export interface MockUser {
  id: number
  username: string
  email: string
  role: string
}

export interface MockPagedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface MockApiResponse<T> {
  data: T
  message?: string
  success: boolean
}

/**
 * Default mock data
 */
export const mockData = {
  programs: [
    { id: 1, name: 'Computer Science', institution: 'Example University', active: true },
    { id: 2, name: 'Software Engineering', institution: 'Example University', active: true },
    { id: 3, name: 'Information Systems', institution: 'Example University', active: false },
  ] as MockProgram[],

  courses: [
    { id: 1, courseCode: 'CS101', courseName: 'Introduction to Programming', credits: 3, programId: 1 },
    { id: 2, courseCode: 'CS201', courseName: 'Data Structures', credits: 4, programId: 1 },
    { id: 3, courseCode: 'SE301', courseName: 'Software Architecture', credits: 3, programId: 2 },
  ] as MockCourse[],

  semesters: [
    { id: 1, name: 'Fall 2024', startDate: '2024-09-01', endDate: '2024-12-15' },
    { id: 2, name: 'Spring 2025', startDate: '2025-01-15', endDate: '2025-05-15' },
  ] as MockSemester[],

  users: [
    { id: 1, username: 'admin', email: 'admin@example.com', role: 'ADMIN' },
    { id: 2, username: 'instructor1', email: 'instructor1@example.com', role: 'INSTRUCTOR' },
  ] as MockUser[],
}

/**
 * Helper to create paged response
 */
export function createPagedResponse<T>(
  content: T[],
  page: number = 0,
  size: number = 10
): MockPagedResponse<T> {
  const start = page * size
  const end = start + size
  const pageContent = content.slice(start, end)

  return {
    content: pageContent,
    totalElements: content.length,
    totalPages: Math.ceil(content.length / size),
    size: size,
    number: page,
  }
}

/**
 * Helper to create API response wrapper
 */
export function createApiResponse<T>(data: T, message?: string): MockApiResponse<T> {
  return {
    data,
    message,
    success: true,
  }
}

/**
 * Mock API Routes Configuration
 */
export class MockApiRoutes {
  private baseUrl: string

  constructor(baseUrl: string = 'http://localhost:8080') {
    this.baseUrl = baseUrl
  }

  /**
   * Set up all API route mocks for a page
   */
  async setupAll(page: Page, customData?: Partial<typeof mockData>) {
    const data = { ...mockData, ...customData }

    await this.mockPrograms(page, data.programs)
    await this.mockCourses(page, data.courses)
    await this.mockSemesters(page, data.semesters)
    await this.mockUsers(page, data.users)
  }

  /**
   * Mock Programs API
   */
  async mockPrograms(page: Page, programs: MockProgram[] = mockData.programs) {
    // GET /api/programs (paginated)
    await page.route(`**/api/programs**`, async (route: Route) => {
      const url = new URL(route.request().url())
      const page = parseInt(url.searchParams.get('page') || '0')
      const size = parseInt(url.searchParams.get('size') || '10')

      if (route.request().method() === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(createPagedResponse(programs, page, size)),
        })
      }
    })

    // GET /api/programs/{id}
    await page.route(`**/api/programs**`, async (route: Route) => {
      if (route.request().method() === 'GET') {
        const id = parseInt(route.request().url().split('/').pop() || '0')
        const program = programs.find((p) => p.id === id)

        if (program) {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(program),
          })
        } else {
          await route.fulfill({
            status: 404,
            contentType: 'application/json',
            body: JSON.stringify({ message: `Program not found with id: ${id}` }),
          })
        }
      }
    })

    // POST /api/programs
    await page.route(`**/api/programs**`, async (route: Route) => {
      if (route.request().method() === 'POST') {
        const requestBody = JSON.parse(route.request().postData() || '{}')
        const newProgram = {
          id: programs.length + 1,
          ...requestBody,
        }
        programs.push(newProgram)

        await route.fulfill({
          status: 201,
          contentType: 'application/json',
          body: JSON.stringify(newProgram),
        })
      }
    })

    // PUT /api/programs/{id}
    await page.route(`**/api/programs**`, async (route: Route) => {
      if (route.request().method() === 'PUT') {
        const id = parseInt(route.request().url().split('/').pop() || '0')
        const requestBody = JSON.parse(route.request().postData() || '{}')
        const index = programs.findIndex((p) => p.id === id)

        if (index !== -1) {
          programs[index] = { ...programs[index], ...requestBody, id }
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(programs[index]),
          })
        } else {
          await route.fulfill({
            status: 404,
            contentType: 'application/json',
            body: JSON.stringify({ message: `Program not found with id: ${id}` }),
          })
        }
      }
    })

    // DELETE /api/programs/{id}
    await page.route(`**/api/programs**`, async (route: Route) => {
      if (route.request().method() === 'DELETE') {
        const id = parseInt(route.request().url().split('/').pop() || '0')
        const index = programs.findIndex((p) => p.id === id)

        if (index !== -1) {
          programs.splice(index, 1)
          await route.fulfill({
            status: 204,
          })
        } else {
          await route.fulfill({
            status: 404,
            contentType: 'application/json',
            body: JSON.stringify({ message: `Program not found with id: ${id}` }),
          })
        }
      }
    })
  }

  /**
   * Mock Courses API
   */
  async mockCourses(page: Page, courses: MockCourse[] = mockData.courses) {
    // GET /api/courses
    await page.route(`**/api/courses**`, async (route: Route) => {
      if (route.request().method() === 'GET') {
        const url = new URL(route.request().url())
        const page = parseInt(url.searchParams.get('page') || '0')
        const size = parseInt(url.searchParams.get('size') || '10')

        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(createPagedResponse(courses, page, size)),
        })
      }
    })

    // GET /api/courses/{id}
    await page.route(`**/api/courses**`, async (route: Route) => {
      if (route.request().method() === 'GET') {
        const id = parseInt(route.request().url().split('/').pop() || '0')
        const course = courses.find((c) => c.id === id)

        if (course) {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(course),
          })
        } else {
          await route.fulfill({
            status: 404,
            contentType: 'application/json',
            body: JSON.stringify({ message: `Course not found with id: ${id}` }),
          })
        }
      }
    })
  }

  /**
   * Mock Semesters API
   */
  async mockSemesters(page: Page, semesters: MockSemester[] = mockData.semesters) {
    await page.route(`**/api/semesters**`, async (route: Route) => {
      if (route.request().method() === 'GET') {
        const url = new URL(route.request().url())
        const page = parseInt(url.searchParams.get('page') || '0')
        const size = parseInt(url.searchParams.get('size') || '10')

        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(createPagedResponse(semesters, page, size)),
        })
      }
    })
  }

  /**
   * Mock Users API (for authentication)
   */
  async mockUsers(page: Page, users: MockUser[] = mockData.users) {
    // Mock login endpoint
    await page.route(`**/api/semesters**`, async (route: Route) => {
      if (route.request().method() === 'POST') {
        const requestBody = JSON.parse(route.request().postData() || '{}')
        const user = users.find(
          (u) => u.username === requestBody.username || u.email === requestBody.email
        )

        if (user) {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({
              user,
              token: 'mock-jwt-token',
            }),
          })
        } else {
          await route.fulfill({
            status: 401,
            contentType: 'application/json',
            body: JSON.stringify({ message: 'Invalid credentials' }),
          })
        }
      }
    })

    // Mock current user endpoint
    await page.route(`**/api/auth/me`, async (route: Route) => {
      if (route.request().method() === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(users[0]), // Return first user as logged in
        })
      }
    })
  }

  /**
   * Mock error responses for testing error handling
   */
  async mockError(page: Page, endpoint: string, statusCode: number, message: string) {
    await page.route(`${this.baseUrl}${endpoint}`, async (route: Route) => {
      await route.fulfill({
        status: statusCode,
        contentType: 'application/json',
        body: JSON.stringify({ message }),
      })
    })
  }

  /**
   * Mock network delay for testing loading states
   */
  async mockWithDelay(page: Page, endpoint: string, delay: number, response: any) {
    await page.route(`${this.baseUrl}${endpoint}`, async (route: Route) => {
      await new Promise((resolve) => setTimeout(resolve, delay))
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(response),
      })
    })
  }
}
