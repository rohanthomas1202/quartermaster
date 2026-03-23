import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { LockedView } from '../components/LockedView';
import type { WeeklyCommitResponse } from '../api/types';

const mockWeek: WeeklyCommitResponse = {
  id: 'wc-1',
  userId: 'user-1',
  orgId: 'org-1',
  weekStartDate: '2026-03-23',
  weekEndDate: '2026-03-29',
  status: 'LOCKED',
  version: 1,
  items: [
    {
      id: 'item-1',
      weeklyCommitId: 'wc-1',
      title: 'Locked task',
      description: null,
      outcomeId: 'out-1',
      urgency: 'HIGH',
      importance: 'HIGH',
      sortOrder: 0,
      completionStatus: null,
      completionNotes: null,
      carriedFromId: null,
      version: 1,
      createdAt: null,
      updatedAt: null,
    },
  ],
};

describe('LockedView', () => {
  beforeEach(() => {
    useWeeklyCommitStore.setState({
      currentWeek: mockWeek,
      loading: false,
      error: null,
      fetchCurrentWeek: vi.fn(),
      fetchWeek: vi.fn(),
      addItem: vi.fn(),
      updateItem: vi.fn(),
      deleteItem: vi.fn(),
      reconcileItem: vi.fn(),
      submitReconciliation: vi.fn(),
    });
  });

  it('shows locked banner text', () => {
    render(<LockedView />);

    expect(
      screen.getByText(
        'Week is in progress. Commits are locked. Reconciliation opens Friday 5:00 PM.',
      ),
    ).toBeInTheDocument();
  });
});
