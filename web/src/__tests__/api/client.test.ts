// @vitest-environment node
import { describe, it, expect } from 'vitest';
import { api } from '../../api/client';

describe('api client', () => {
  it('fetches current week', async () => {
    const week = await api.getCurrentWeek();
    expect(week.status).toBe('DRAFT');
    expect(week.weekStartDate).toBe('2026-03-23');
  });

  it('fetches rally cries', async () => {
    const cries = await api.listRallyCries();
    expect(cries).toHaveLength(1);
    expect(cries[0]!.title).toBe('Retention');
  });
});
