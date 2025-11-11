// connection-test.spec.ts
import { test, expect } from './fixtures/test-fixtures.js'

test.describe('Connection and API Health', () => {
  test('should verify mock API is working', async ({ page, mockApi }) => {
    await page.route('**/api/ping', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ status: 'ok' }),
      })
    })

    await page.goto('/')
    const response = await page.evaluate(async () => {
      const res = await fetch('/api/ping')
      return await res.json()
    })

    expect(response.status).toBe('ok')
  })

  test('should handle server error response', async ({ page, mockApi }) => {
    await page.route('**/api/status', async (route) => {
      await route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Internal Server Error' }),
      })
    })

    await page.goto('/')
    const response = await page.evaluate(async () => {
      try {
        const res = await fetch('/api/status')
        return await res.json()
      } catch {
        return { error: 'failed' }
      }
    })

    expect(response).toHaveProperty('message')
    expect(response.message).toBe('Internal Server Error')
  })

  test('should simulate network failure', async ({ page, mockApi }) => {
    await page.route('**/api/check', async (route) => {
      await route.abort('failed')
    })

    const result = await page.evaluate(async () => {
      try {
        await fetch('/api/check')
        return 'ok'
      } catch {
        return 'network-failed'
      }
    })

    expect(result).toBe('network-failed')
  })
})
