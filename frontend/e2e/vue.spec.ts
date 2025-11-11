import { test, expect } from './fixtures/test-fixtures.js'

/**
 * Basic smoke tests for the application
 */
test.describe('Basic Application Tests', () => {
  test('should load the application root URL', async ({ page }) => {
    await page.goto('/')

    // Should redirect to /dashboard
    await expect(page).toHaveURL('/dashboard')
  })

  test('should display the hidden h1 element indicating site loaded', async ({ page }) => {
    await page.goto('/')

    // Check for the hidden h1 in navbar that confirms the site loaded
    const hiddenH1 = page.locator('h1[hidden]')
    await expect(hiddenH1).toBeAttached()
    await expect(hiddenH1).toContainText('This hidden element checks if the site loaded')
  })

  test('should render the navbar', async ({ page }) => {
    await page.goto('/')

    // Check that navbar exists
    await expect(page.locator('#navbar')).toBeVisible()
  })

  test('should have the correct app logo text', async ({ page }) => {
    await page.goto('/')

    // Check logo text
    await expect(page.locator('#logo')).toContainText('Abet Assessment App')
  })

  test('should render navigation links', async ({ page }) => {
    await page.goto('/')

    // Check for main navigation buttons
    await expect(page.locator('.nav_button').filter({ hasText: 'Home' })).toBeVisible()
    await expect(page.locator('.nav_button').filter({ hasText: 'Test Connection' })).toBeVisible()
  })

  test('should show login link when not authenticated', async ({ page }) => {
    await page.goto('/')

    // Should show login link
    await expect(page.locator('#login')).toBeVisible()
  })

  test('should display HomePage content on dashboard route', async ({ page }) => {
    await page.goto('/dashboard')

    // Check for HomePage heading
    await expect(page.getByRole('heading', { level: 1 })).toContainText('ABET Assessment App')

    // Check for some key content sections
    await expect(page.locator('h2').filter({ hasText: 'Fundamental Assessment Concepts' })).toBeVisible()
    await expect(page.locator('h3').filter({ hasText: 'Program Educational Objectives' })).toBeVisible()
  })

  test('should render footer on HomePage', async ({ page }) => {
    await page.goto('/dashboard')

    // Check for footer
    const footer = page.locator('.footer')
    await expect(footer).toBeVisible()
    await expect(footer).toContainText('2025 ABET Assessment App')
    await expect(footer).toContainText('Definitions adapted from ABET documentation')
  })
})

/**
 * Tests for route accessibility
 */
test.describe('Route Accessibility', () => {
  test('should access login page', async ({ page }) => {
    await page.goto('/login')
    await expect(page).toHaveURL('/login')
    await expect(page.getByRole('heading', { level: 3, name: 'Log In' })).toBeVisible()
  })

  test('should access signup page', async ({ page }) => {
    await page.goto('/signup')
    await expect(page).toHaveURL('/signup')
    await expect(page.getByRole('heading', { level: 3, name: 'Sign Up' })).toBeVisible()
  })

  test('should access test connection page', async ({ page }) => {
    await page.goto('/test-connection')
    await expect(page).toHaveURL('/test-connection')
  })

  test('should access admin dashboard page', async ({ page }) => {
    await page.goto('/admin-dashboard')
    await expect(page).toHaveURL('/admin-dashboard')
    await expect(page.locator('h1', { hasText: 'Administrator Dashboard' })).toBeVisible()
  })

  test('should access program courses page', async ({ page }) => {
    await page.goto('/1/courses')
    await expect(page).toHaveURL(/.*\/1\/courses/)
  })

  test('should access program instructors page', async ({ page }) => {
    await page.goto('/1/instructors')
    await expect(page).toHaveURL(/.*\/1\/instructors/)
  })

  test('should access summary page', async ({ page }) => {
    await page.goto('/1/summary/')
    await expect(page).toHaveURL(/.*\/1\/summary/)
  })

  test('should access course view page', async ({ page }) => {
    await page.goto('/1/course/1')
    await expect(page).toHaveURL(/.*\/1\/course\/1/)
  })

  test('should access instructor view page', async ({ page }) => {
    await page.goto('/1/instructor/1')
    await expect(page).toHaveURL(/.*\/1\/instructor\/1/)
  })

  test('should access FCAR page', async ({ page }) => {
    await page.goto('/1/fcar/1')
    await expect(page).toHaveURL(/.*\/1\/fcar\/1/)
  })
})

/**
 * Tests for page metadata and structure
 */
test.describe('Page Structure', () => {
  test('should have proper document structure', async ({ page }) => {
    await page.goto('/')

    // Check for basic HTML structure
    const html = page.locator('html')
    await expect(html).toBeAttached()

    const body = page.locator('body')
    await expect(body).toBeAttached()
  })

  test('should render Vue app mount point', async ({ page }) => {
    await page.goto('/')

    // Vue typically mounts to #app
    const appMount = page.locator('#app')
    await expect(appMount).toBeAttached()
  })
})

/**
 * Tests for responsive behavior
 */
test.describe('Responsive Design', () => {
  test('should render on mobile viewport', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 })
    await page.goto('/')

    // Navbar should still be visible
    await expect(page.locator('#navbar')).toBeVisible()
  })

  test('should render on tablet viewport', async ({ page }) => {
    // Set tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 })
    await page.goto('/')

    // Content should be visible
    await expect(page.locator('#navbar')).toBeVisible()
  })

  test('should render on desktop viewport', async ({ page }) => {
    // Set desktop viewport
    await page.setViewportSize({ width: 1920, height: 1080 })
    await page.goto('/')

    // All navigation elements should be visible
    await expect(page.locator('#navbar')).toBeVisible()
    await expect(page.locator('#logo')).toBeVisible()
  })
})

/**
 * Performance and loading tests
 */
test.describe('Performance', () => {
  test('should load the homepage within reasonable time', async ({ page }) => {
    const startTime = Date.now()

    await page.goto('/')
    await page.waitForLoadState('networkidle')

    const loadTime = Date.now() - startTime

    // Should load in under 5 seconds (adjust as needed)
    expect(loadTime).toBeLessThan(5000)
  })

  test('should have no console errors on homepage', async ({ page }) => {
    const consoleErrors: string[] = []

    page.on('console', (msg) => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text())
      }
    })

    await page.goto('/')
    await page.waitForLoadState('networkidle')

    // Should have no console errors
    expect(consoleErrors.length).toBe(0)
  })
})
