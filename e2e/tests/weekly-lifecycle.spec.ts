import { test, expect } from '@playwright/test';

test.describe('Weekly Commits Lifecycle', () => {
  test('shows current week in DRAFT state', async ({ page }) => {
    await page.goto('/weekly-commits');
    await expect(page.getByText('DRAFT')).toBeVisible();
    await expect(page.getByText('New Commit')).toBeVisible();
  });

  test('Eisenhower matrix renders four quadrants', async ({ page }) => {
    await page.goto('/weekly-commits');
    await expect(page.getByLabel('Do First')).toBeVisible();
    await expect(page.getByLabel('Schedule')).toBeVisible();
    await expect(page.getByLabel('Delegate')).toBeVisible();
    await expect(page.getByLabel('Eliminate')).toBeVisible();
  });
});
