// admin-dashboard.spec.ts
import { test, expect } from './fixtures.js';

/**
 * Tests for Admin Dashboard
 * Based on ProgramInstructorsPage.vue which is the main admin section
 */
test.describe('Admin Dashboard', () => {
  test.beforeEach(async ({ page, mockApi }) => {
    // Setup mock data matching the actual API response format
    const courses = [
      {
        id: 1,
        course_code: 'CS101',
        instructor_name: 'Dr. Smith',
        measures_completed: 5,
        measures_total: 10,
      },
      {
        id: 2,
        course_code: 'CS201',
        instructor_name: 'Dr. Jones',
        measures_completed: 8,
        measures_total: 10,
      },
      {
        id: 3,
        course_code: 'ECE260',
        instructor_name: 'Dr. Brown',
        measures_completed: 3,
        measures_total: 12,
      },
    ];

    const instructors = [
      { id: 1, name: 'Dr. Alice Smith' },
      { id: 2, name: 'Dr. Bob Jones' },
      { id: 3, name: 'Dr. Carol Brown' },
    ];

    // Mock authentication and API endpoints
    await mockApi.mockLoginSuccess({ id: 99, name: 'Admin Tester', role: 'admin' });
    await mockApi.mockCourses(courses);
    await mockApi.mockInstructors(instructors);

    // Navigate to admin dashboard
    await page.goto('/admin-dashboard');
  });

  test('should display admin dashboard header', async ({ page }) => {
    await expect(page.locator('h2', { hasText: 'Admin Dashboard' })).toBeVisible();
    await expect(page.locator('h2', { hasText: 'York College of Pennsylvania' })).toBeVisible();
  });

  test('should display courses section', async ({ page }) => {
    await expect(page.locator('h3', { hasText: 'Courses' })).toBeVisible();
    const editButton = page
      .locator('.section-header')
      .filter({ has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await expect(editButton).toBeVisible();
  });

  test('should display courses table with data', async ({ page }) => {
    const table = page
      .locator('.section')
      .filter({ has: page.locator('h3', { hasText: 'Courses' }) })
      .locator('table');
    await expect(table).toBeVisible();

    await expect(table.locator('th', { hasText: 'Course' })).toBeVisible();
    await expect(table.locator('th', { hasText: 'Instructor' })).toBeVisible();
    await expect(table.locator('th', { hasText: 'Measures Completed' })).toBeVisible();

    await expect(table.locator('td', { hasText: 'CS101' })).toBeVisible();
    await expect(table.locator('td', { hasText: 'Dr. Smith' })).toBeVisible();
    await expect(table.locator('td', { hasText: '5/10' })).toBeVisible();

    await expect(table.locator('td', { hasText: 'CS201' })).toBeVisible();
    await expect(table.locator('td', { hasText: '8/10' })).toBeVisible();
  });

  test('should display instructors section', async ({ page }) => {
    await expect(page.locator('h3', { hasText: 'Instructors' })).toBeVisible();
    const editButton = page
      .locator('.section-header')
      .filter({ has: page.locator('h3', { hasText: 'Instructors' }) })
      .getByRole('button', { name: 'Edit' });
    await expect(editButton).toBeVisible();
  });

  test('should display instructors list', async ({ page }) => {
    const instructorList = page.locator('.instructor-list');
    await expect(instructorList).toBeVisible();

    await expect(instructorList.locator('li', { hasText: 'Dr. Alice Smith' })).toBeVisible();
    await expect(instructorList.locator('li', { hasText: 'Dr. Bob Jones' })).toBeVisible();
    await expect(instructorList.locator('li', { hasText: 'Dr. Carol Brown' })).toBeVisible();

    const removeButtons = instructorList.locator('button.remove-btn');
    await expect(removeButtons).toHaveCount(3);
  });

  test('should open course edit popup when Edit button clicked', async ({ page }) => {
    const coursesEditButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await coursesEditButton.click();

    const popup = page.locator('.popup-overlay');
    await expect(popup).toBeVisible();
    await expect(popup.locator('.popup-header h3')).toContainText('Edit Courses');
    await expect(popup.locator('.close-btn')).toBeVisible();
    await expect(popup.locator('table.edit-table')).toBeVisible();
    await expect(popup.locator('table.edit-table tbody input')).toHaveCount(6);
  });

  test('should close course edit popup when X button clicked', async ({ page }) => {
    const editButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await editButton.click();
    await expect(page.locator('.popup-overlay')).toBeVisible();

    await page.locator('.close-btn').click();
    await expect(page.locator('.popup-overlay')).not.toBeVisible();
  });

  test('should close course edit popup when clicking outside', async ({ page }) => {
    const editButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await editButton.click();
    await expect(page.locator('.popup-overlay')).toBeVisible();

    await page.locator('.popup-overlay').click({ position: { x: 10, y: 10 } });
    await expect(page.locator('.popup-overlay')).not.toBeVisible();
  });

  test('should allow editing course data in popup', async ({ page }) => {
    const editButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await editButton.click();

    const firstInput = page.locator('table.edit-table tbody tr').first().locator('input').first();
    await firstInput.clear();
    await firstInput.fill('CS100');
    await expect(firstInput).toHaveValue('CS100');
  });

  test('should save course changes when Save button clicked', async ({ page, mockApi }) => {
    await mockApi.mockSuccess('/api/courses/update', { success: true });

    const editButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await editButton.click();

    const firstInput = page.locator('table.edit-table tbody tr').first().locator('input').first();
    await firstInput.fill('CS100');

    page.on('dialog', dialog => dialog.accept());
    await page.locator('.popup-footer .save-btn').click();
    await expect(page.locator('.popup-overlay')).not.toBeVisible({ timeout: 5000 });
  });

  test('should cancel course changes when Cancel button clicked', async ({ page }) => {
    const editButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await editButton.click();

    const firstInput = page.locator('table.edit-table tbody tr').first().locator('input').first();
    await firstInput.fill('CHANGED');

    await page.locator('.popup-footer .cancel-btn').click();
    await expect(page.locator('.popup-overlay')).not.toBeVisible();
  });

  test('should open instructor edit popup when Edit button clicked', async ({ page }) => {
    const instructorsEditButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Instructors' }) })
      .getByRole('button', { name: 'Edit' });
    await instructorsEditButton.click();

    const popup = page.locator('.popup-overlay');
    await expect(popup).toBeVisible();
    await expect(popup.locator('.popup-header h3')).toContainText('Edit Instructors');
    await expect(popup.locator('.popup-body ul li input')).toHaveCount(3);
  });

  test('should add new instructor in popup', async ({ page }) => {
    const instructorsEditButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Instructors' }) })
      .getByRole('button', { name: 'Edit' });
    await instructorsEditButton.click();

    const addInput = page.locator('.add-new input');
    await addInput.fill('Dr. David Wilson');
    await page.locator('.add-new .add-btn').click();

    const instructorInputs = page.locator('.popup-body ul li input');
    await expect(instructorInputs).toHaveCount(4);
    await expect(page.locator('ul li input[value="Dr. David Wilson"]')).toBeVisible();
    await expect(addInput).toHaveValue('');
  });

  test('should remove instructor from list', async ({ page }) => {
    const removeButtons = page.locator('.instructor-list button.remove-btn');
    const initialCount = await removeButtons.count();
    await removeButtons.first().click();
    await expect(removeButtons).toHaveCount(initialCount - 1);
  });

  test('should save instructor changes when Save button clicked', async ({ page, mockApi }) => {
    await mockApi.mockSuccess('/api/instructors/update', { success: true });

    const instructorsEditButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Instructors' }) })
      .getByRole('button', { name: 'Edit' });
    await instructorsEditButton.click();

    const addInput = page.locator('.add-new input');
    await addInput.fill('Dr. New Instructor');
    await page.locator('.add-new .add-btn').click();

    page.on('dialog', dialog => dialog.accept());
    await page.locator('.popup-footer .save-btn').click();
    await expect(page.locator('.popup-overlay')).not.toBeVisible({ timeout: 5000 });
  });

  test('should handle save errors gracefully', async ({ page, mockApi }) => {
    await mockApi.mockError('/api/courses/update', 500, 'Server error');

    const coursesEditButton = page
      .locator('.section', { has: page.locator('h3', { hasText: 'Courses' }) })
      .getByRole('button', { name: 'Edit' });
    await coursesEditButton.click();

    page.on('dialog', dialog => {
      expect(dialog.message()).toContain('Failed');
      dialog.accept();
    });

    await page.locator('.popup-footer .save-btn').click();
  });

  test('should display footer with copyright', async ({ page }) => {
    const footer = page.locator('.footer');
    await expect(footer).toBeVisible();
    await expect(footer).toContainText('© 2025 ABET Assessment App');
    await expect(footer).toContainText('Definitions adapted from ABET documentation');
  });
});
