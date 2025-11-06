// connection-test.spec.ts
import { test, expect } from './fixtures.js';

test.describe('Connection and API Health', () => {
  test('should verify mock API is working', async ({ page, mockApi }) => {
    await mockApi.mockSuccess('/api/ping', { status: 'ok' });

    await page.goto('/');
    const response = await page.evaluate(async () => {
      const res = await fetch('/api/ping');
      return await res.json();
    });

    expect(response.status).toBe('ok');
  });

  test('should handle server error response', async ({ page, mockApi }) => {
    await mockApi.mockError('/api/status', 500, 'Internal Server Error');

    await page.goto('/');
    const response = await page.evaluate(async () => {
      try {
        const res = await fetch('/api/status');
        return await res.json();
      } catch {
        return { error: 'failed' };
      }
    });

    expect(response.error).toBe('Internal Server Error');
  });

  test('should simulate network failure', async ({ page, mockApi }) => {
    await mockApi.mockNetworkFailure('/api/check');

    const result = await page.evaluate(async () => {
      try {
        await fetch('/api/check');
        return 'ok';
      } catch {
        return 'network-failed';
      }
    });

    expect(result).toBe('network-failed');
  });
});
