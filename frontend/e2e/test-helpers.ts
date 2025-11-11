// test-helpers.ts
import type { Page } from '@playwright/test';

export interface MockApiHelpers {
  mockSuccess: (endpoint: string, data: any) => Promise<void>;
  mockError: (endpoint: string, status: number, message: string) => Promise<void>;
  mockNetworkFailure: (endpoint: string) => Promise<void>;
  mockCourses: (courses: any[]) => Promise<void>;
  mockInstructors: (instructors: any[]) => Promise<void>;
  mockLoginSuccess: (user?: any) => Promise<void>;
  mockLoginFailure: () => Promise<void>;
  mockLogout: () => Promise<void>;
}

export function createMockApi(page: Page): MockApiHelpers {
  return {
    async mockSuccess(endpoint, data) {
      await page.route(`**${endpoint}**`, route =>  // ✅ wildcard on both ends
        route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(data),
        })
      );
    },

    async mockError(endpoint, status, message) {
      await page.route(`**${endpoint}**`, route =>
        route.fulfill({
          status,
          contentType: 'application/json',
          body: JSON.stringify({ error: message }),
        })
      );
    },

    async mockNetworkFailure(endpoint) {
      await page.route(`**${endpoint}**`, route => route.abort('failed'));
    },

    async mockCourses(courses) {
      await this.mockSuccess('/api/courses', courses);
    },

    async mockInstructors(instructors) {
      await this.mockSuccess('/api/courses/instructor', instructors);
      await this.mockSuccess('/api/instructors', instructors); // ✅ cover both endpoints
    },

    async mockLoginSuccess(user = { id: 1, name: 'Test User', role: 'admin' }) {
      await this.mockSuccess('/api/auth/login', { token: 'fake-jwt-token', user });
      await this.mockSuccess('/api/auth/session', { authenticated: true, user });
    },

    async mockLoginFailure() {
      await this.mockError('/api/auth/login', 401, 'Invalid credentials');
      await this.mockSuccess('/api/auth/session', { authenticated: false });
    },

    async mockLogout() {
      await this.mockSuccess('/api/auth/logout', { success: true });
      await this.mockSuccess('/api/auth/session', { authenticated: false });
    },
  };
}
