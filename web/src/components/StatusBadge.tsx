import type { WeeklyCommitStatus } from '../api/types';

const STATUS_STYLES: Record<WeeklyCommitStatus, string> = {
  DRAFT: 'bg-gradient-to-r from-green-500 to-green-600',
  LOCKED: 'bg-gradient-to-r from-amber-500 to-amber-600',
  RECONCILING: 'bg-gradient-to-r from-purple-400 to-purple-600',
  RECONCILED: 'bg-slate-500',
};

interface Props {
  status: WeeklyCommitStatus;
}

export function StatusBadge({ status }: Props) {
  return (
    <span className={`${STATUS_STYLES[status]} text-white px-3 py-1 rounded-full text-xs font-semibold tracking-wide`}>
      {status}
    </span>
  );
}
