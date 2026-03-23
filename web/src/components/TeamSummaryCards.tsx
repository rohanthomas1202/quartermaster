import type { TeamSummary } from '../api/types';

interface TeamSummaryCardsProps {
  summary: TeamSummary;
}

export function TeamSummaryCards({ summary }: TeamSummaryCardsProps) {
  return (
    <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
      <div aria-label="Reconciled count">
        {summary.reconciledCount}/{summary.totalMembers} Reconciled
      </div>
      <div aria-label="Reconciling count">
        {summary.reconcilingCount}/{summary.totalMembers} Reconciling
      </div>
      <div aria-label="Locked count">
        {summary.lockedCount}/{summary.totalMembers} Locked
      </div>
      <div aria-label="Average completion">
        {summary.avgCompletionPercent}% Avg Completion
      </div>
    </div>
  );
}
