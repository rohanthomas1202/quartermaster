import { describe, it, expect } from 'vitest';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { EisenhowerMatrix } from '../components/EisenhowerMatrix';
import type { CommitItem, Priority, CompletionStatus } from '../api/types';

function makeItem(overrides: Partial<CommitItem> = {}): CommitItem {
  return {
    id: overrides.id ?? 'item-1',
    weeklyCommitId: 'wc-1',
    title: overrides.title ?? 'Test Item',
    description: overrides.description ?? null,
    outcomeId: 'out-1',
    urgency: overrides.urgency ?? 'LOW',
    importance: overrides.importance ?? 'LOW',
    sortOrder: overrides.sortOrder ?? 0,
    completionStatus: overrides.completionStatus ?? null,
    completionNotes: overrides.completionNotes ?? null,
    carriedFromId: overrides.carriedFromId ?? null,
    version: overrides.version ?? 1,
    createdAt: null,
    updatedAt: null,
  };
}

describe('EisenhowerMatrix', () => {
  it('places items in correct quadrant', () => {
    const items = [
      makeItem({ id: 'a', title: 'Urgent Important', urgency: 'HIGH', importance: 'HIGH' }),
      makeItem({ id: 'b', title: 'Not Urgent Important', urgency: 'LOW', importance: 'HIGH' }),
    ];

    render(<EisenhowerMatrix items={items} />);

    const doFirst = screen.getByLabelText('Do First');
    const schedule = screen.getByLabelText('Schedule');

    expect(doFirst).toHaveTextContent('Urgent Important');
    expect(schedule).toHaveTextContent('Not Urgent Important');
  });

  it('shows carried-forward indicator', () => {
    const items = [
      makeItem({ id: 'c', title: 'Carried Task', urgency: 'HIGH', importance: 'HIGH', carriedFromId: 'old-1' }),
    ];

    render(<EisenhowerMatrix items={items} />);

    expect(screen.getByText(/↻ carried/)).toBeInTheDocument();
  });
});
