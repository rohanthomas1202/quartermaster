import type { RcdoAlignment } from '../api/types';

const BAR_COLORS = [
  'from-purple-600 to-purple-400',
  'from-blue-600 to-blue-400',
  'from-green-600 to-green-400',
  'from-amber-600 to-amber-400',
  'from-pink-600 to-pink-400',
];

interface Props { alignment: RcdoAlignment; }

export function RcdoAlignmentChart({ alignment }: Props) {
  return (
    <div aria-label="RCDO Alignment" className="bg-white/[0.04] border border-white/[0.08] rounded-xl p-4">
      <h3 className="text-sm font-semibold text-slate-200 mb-3">RCDO Alignment</h3>
      {alignment.rallyCries.map((rc, i) => (
        <div key={rc.rallyCryId} className="mb-2.5 last:mb-0">
          <div className="flex justify-between text-xs mb-1">
            <span className="text-slate-200">{rc.rallyCryTitle}</span>
            <span className="text-slate-400">{rc.commitCount} commits ({rc.percentage}%)</span>
          </div>
          <div className="h-1.5 bg-white/[0.08] rounded-full overflow-hidden">
            <div
              className={`h-full bg-gradient-to-r ${BAR_COLORS[i % BAR_COLORS.length]} rounded-full`}
              style={{ width: `${rc.percentage}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}
