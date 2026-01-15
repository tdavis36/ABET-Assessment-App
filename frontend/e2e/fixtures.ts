// fixtures.ts
import { test as base, expect, Page } from '@playwright/test';
import { createMockApi, MockApiHelpers } from './test-helpers.js';

export type TestFixtures = {
  mockApi: MockApiHelpers;
  page: Page;
};

export const test = base.extend<TestFixtures>({
  mockApi: async ({ page }, use) => {
    const mockApi = createMockApi(page); // ✅ no await here
    await use(mockApi);
  },
});

export { expect };
