import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { CommitEntryView } from '../components/CommitEntryView';
import type { WeeklyCommitResponse } from '../api/types';

const mockWeek: WeeklyCommitResponse = {
  id: 'wc-1',
  userId: 'user-1',
  orgId: 'org-1',
  weekStartDate: '2026-03-23',
  weekEndDate: '2026-03-29',
  status: 'DRAFT',
  version: 1,
  items: [
    {
      id: 'item-1',
      weeklyCommitId: 'wc-1',
      title: 'Build feature',
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

describe('CommitEntryView', () => {
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

  it('renders week selector and matrix for DRAFT status', () => {
    render(<CommitEntryView />);

    expect(screen.getByText(/Week of 2026-03-23/)).toBeInTheDocument();
    expect(screen.getByLabelText('Do First')).toBeInTheDocument();
    expect(screen.getByText('Build feature')).toBeInTheDocument();
  });

  it('shows loading state', () => {
    useWeeklyCommitStore.setState({ currentWeek: null, loading: true });
    render(<CommitEntryView />);

    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  it('shows error state', () => {
    useWeeklyCommitStore.setState({ currentWeek: null, loading: false, error: 'Network error' });
    render(<CommitEntryView />);

    expect(screen.getByRole('alert')).toHaveTextContent('Error: Network error');
  });
});
