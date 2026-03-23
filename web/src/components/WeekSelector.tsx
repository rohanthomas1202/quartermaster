interface WeekSelectorProps {
  weekStartDate: string;
  onPrev: () => void;
  onNext: () => void;
  onCurrent: () => void;
}

function formatWeekRange(startDate: string): string {
  const start = new Date(startDate + 'T00:00:00');
  const end = new Date(start);
  end.setDate(start.getDate() + 6);
  const fmt = (d: Date) => d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  return `${fmt(start)} – ${fmt(end)}`;
}

export function WeekSelector({ weekStartDate, onPrev, onNext, onCurrent }: WeekSelectorProps) {
  return (
    <div className="flex items-center gap-2 bg-white/[0.06] border border-white/10 rounded-lg px-3 py-1.5">
      <button type="button" onClick={onPrev} aria-label="Previous week"
        className="text-slate-500 hover:text-slate-300 transition-colors">←</button>
      <span className="text-sm text-slate-200 font-medium min-w-[120px] text-center">
        {formatWeekRange(weekStartDate)}
      </span>
      <button type="button" onClick={onNext} aria-label="Next week"
        className="text-slate-500 hover:text-slate-300 transition-colors">→</button>
      <button type="button" onClick={onCurrent}
        className="text-xs text-slate-400 hover:text-slate-200 ml-1 transition-colors">Current</button>
    </div>
  );
}
