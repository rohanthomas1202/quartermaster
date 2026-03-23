import { describe, it, expect, beforeAll, afterEach, afterAll } from 'vitest';
import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { ManagerDashboard } from '../components/ManagerDashboard';

const server = setupServer(
  http.get('*/api/wc/manager/team-summary*', () => {
    return HttpResponse.json({
      totalMembers: 4,
      reconciledCount: 2,
      reconcilingCount: 1,
      lockedCount: 1,
      draftCount: 0,
      avgCompletionPercent: 75,
      members: [
        {
          userId: 'alice',
          status: 'RECONCILED',
          completedCount: 3,
          partialCount: 1,
          notDoneCount: 0,
          totalItems: 4,
        },
        {
          userId: 'bob',
          status: 'RECONCILING',
          completedCount: 2,
          partialCount: 0,
          notDoneCount: 1,
          totalItems: 3,
        },
      ],
    });
  }),
  http.get('*/api/wc/manager/rcdo-alignment*', () => {
    return HttpResponse.json({
      rallyCries: [
        {
          rallyCryId: 'rc-1',
          rallyCryTitle: 'Retention',
          commitCount: 5,
          percentage: 50,
        },
      ],
      totalCommits: 10,
    });
  }),
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('ManagerDashboard', () => {
  it('renders team summary and alignment', async () => {
    render(<ManagerDashboard />);

    await waitFor(() => {
      expect(screen.getByText('75% Avg Completion')).toBeInTheDocument();
    });

    expect(screen.getByText('alice')).toBeInTheDocument();
    expect(screen.getByText('bob')).toBeInTheDocument();
    expect(screen.getByText(/Retention/)).toBeInTheDocument();
  });
});
