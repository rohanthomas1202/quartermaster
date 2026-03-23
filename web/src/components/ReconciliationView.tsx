import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { NavBar } from './NavBar';
import { WeekSelector } from './WeekSelector';
import { StatusBadge } from './StatusBadge';
import { ReconciliationItem } from './ReconciliationItem';
import type { CompletionStatus } from '../api/types';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate + 'T00:00:00');
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function ReconciliationView() {
  const { currentWeek, fetchCurrentWeek, fetchWeek, reconcileItem, submitReconciliation } =
    useWeeklyCommitStore();
  if (!currentWeek) return null;

  const items = currentWeek.items;
  const completedCount = items.filter((i) => i.completionStatus === 'COMPLETED').length;
  const partialCount = items.filter((i) => i.completionStatus === 'PARTIAL').length;
  const notDoneCount = items.filter((i) => i.completionStatus === 'NOT_DONE').length;

  const handleReconcile = (itemId: string, status: CompletionStatus, notes: string) => {
    reconcileItem(currentWeek.weekStartDate, itemId, {
      completionStatus: status,
      completionNotes: notes || undefined,
    });
  };

  return (
    <div>
      <NavBar>
        <WeekSelector
          weekStartDate={currentWeek.weekStartDate}
          onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
          onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
          onCurrent={() => fetchCurrentWeek()}
        />
        <StatusBadge status="RECONCILING" />
      </NavBar>

      <div className="px-6 py-4">
        {items.map((item) => (
          <ReconciliationItem key={item.id} item={item} onReconcile={handleReconcile} />
        ))}

        {/* Summary bar */}
        <div className="flex gap-4 items-center p-4 bg-white/[0.04] border border-white/[0.08] rounded-xl my-4">
          <div className="flex items-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-green-500" />
            <span className="text-sm text-slate-200 font-medium">{completedCount}</span>
            <span className="text-xs text-slate-500">Completed</span>
          </div>
          <div className="flex items-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-amber-500" />
            <span className="text-sm text-slate-200 font-medium">{partialCount}</span>
            <span className="text-xs text-slate-500">Partial</span>
          </div>
          <div className="flex items-center gap-1.5">
            <div className="w-2.5 h-2.5 rounded-full bg-red-500" />
            <span className="text-sm text-slate-200 font-medium">{notDoneCount}</span>
            <span className="text-xs text-slate-500">Not Done</span>
          </div>
          <div className="flex-1" />
          <span className="text-xs text-slate-500">{items.length} total</span>
        </div>

        {/* Submit */}
        <div className="text-center">
          <button
            type="button"
            onClick={() => submitReconciliation(currentWeek.weekStartDate)}
            className="bg-gradient-to-r from-purple-600 to-purple-700 text-white px-7 py-2.5 rounded-lg text-sm font-semibold shadow-[0_0_24px_rgba(124,58,237,0.3)] hover:shadow-[0_0_30px_rgba(124,58,237,0.4)] transition-shadow"
          >
            Submit Reconciliation
          </button>
          <p className="text-xs text-slate-500 mt-2">Partial and Not Done items will carry forward to next week</p>
        </div>
      </div>
    </div>
  );
}
