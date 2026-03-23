import type { TeamSummary } from '../api/types';

interface Props { summary: TeamSummary; }

const CARDS = [
  { key: 'reconciled', bg: 'from-green-500/20 to-green-500/5', border: 'border-green-500/25', color: 'text-green-400', label: 'Reconciled' },
  { key: 'reconciling', bg: 'from-purple-500/15 to-purple-500/5', border: 'border-purple-500/25', color: 'text-purple-400', label: 'Reconciling' },
  { key: 'locked', bg: 'from-amber-500/15 to-amber-500/5', border: 'border-amber-500/25', color: 'text-amber-400', label: 'Locked' },
  { key: 'avg', bg: 'from-blue-500/15 to-blue-500/5', border: 'border-blue-500/25', color: 'text-blue-400', label: 'Avg Completion' },
] as const;

export function TeamSummaryCards({ summary }: Props) {
  const values = {
    reconciled: summary.reconciledCount,
    reconciling: summary.reconcilingCount,
    locked: summary.lockedCount,
    avg: summary.avgCompletionPercent,
  };

  return (
    <div className="grid grid-cols-4 gap-3">
      {CARDS.map(c => (
        <div key={c.key} className={`bg-gradient-to-br ${c.bg} border ${c.border} rounded-xl p-4`}
             aria-label={c.key === 'avg' ? 'Average completion' : `${c.label} count`}>
          <div className={`text-2xl font-bold ${c.color}`}>
            {values[c.key]}{c.key === 'avg' ? '%' : ''}
          </div>
          <div className="text-xs text-slate-400 mt-0.5">{c.label}</div>
        </div>
      ))}
    </div>
  );
}
