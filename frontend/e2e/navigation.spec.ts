// navigation.spec.ts
import { test, expect } from './fixtures.js';

test.describe('Navigation Tests', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    await mockApi.mockLoginSuccess({ id: 1, name: 'Nav Tester', role: 'user' });
    await page.goto('/');
  });

  test('should show navigation bar', async ({ page }) => {
    const navbar = page.locator('nav');
    await expect(navbar).toBeVisible();
  });

  test('should navigate to dashboard', async ({ page }) => {
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await expect(page).toHaveURL(/.*dashboard/);
    await expect(page.locator('h1')).toContainText('Dashboard');
  });

  test('should navigate to courses page', async ({ page, mockApi }) => {
    await mockApi.mockCourses([{ id: 1, course_code: 'CS101', instructor_name: 'Dr. Smith' }]);
    await page.getByRole('link', { name: 'Courses' }).click();
    await expect(page).toHaveURL(/.*courses/);
    await expect(page.locator('h2')).toContainText('Courses');
    await expect(page.locator('td', { hasText: 'CS101' })).toBeVisible();
  });

  test('should logout and redirect to login page', async ({ page, mockApi }) => {
    await mockApi.mockLogout();
    await page.getByRole('button', { name: /logout/i }).click();
    await expect(page).toHaveURL(/.*login/);
    await expect(page.locator('#login')).toBeVisible();
  });
});
