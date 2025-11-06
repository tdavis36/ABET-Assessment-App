// e2e-user-journey.spec.ts
import { test, expect } from './fixtures.js';

test.describe('E2E User Journey', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    await mockApi.mockLoginSuccess({ id: 10, name: 'Journey Tester', role: 'admin' });
    await mockApi.mockCourses([
      { id: 1, course_code: 'BIO101', instructor_name: 'Dr. Jones', measures_completed: 3, measures_total: 5 },
    ]);
    await page.goto('/');
  });

  test('should start at login, authenticate, and show dashboard', async ({ page, mockApi }) => {
    await mockApi.mockLoginSuccess();
    await page.goto('/login');
    await page.locator('input[name="username"]').fill('admin');
    await page.locator('input[name="password"]').fill('password');
    await page.locator('button[type="submit"]').click();

    await expect(page).toHaveURL(/.*dashboard/);
    await expect(page.locator('h1')).toContainText('Dashboard');
  });

  test('should navigate to courses and view details', async ({ page }) => {
    await page.goto('/courses');
    await expect(page.locator('table')).toBeVisible();
    await expect(page.locator('td', { hasText: 'BIO101' })).toBeVisible();
  });

  test('should handle 401 unauthorized gracefully', async ({ page, mockApi }) => {
    await mockApi.mockLoginFailure();
    await page.goto('/dashboard');
    await expect(page).toHaveURL(/.*login/);
    await expect(page.locator('#login')).toBeVisible();
  });
});
