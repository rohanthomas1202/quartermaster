import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { NavBar } from './NavBar';
import { WeekSelector } from './WeekSelector';
import { StatusBadge } from './StatusBadge';
import { CommitItemRow } from './CommitItemRow';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate + 'T00:00:00');
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function LockedView() {
  const { currentWeek, fetchCurrentWeek, fetchWeek } = useWeeklyCommitStore();
  if (!currentWeek) return null;

  return (
    <div>
      <NavBar>
        <WeekSelector
          weekStartDate={currentWeek.weekStartDate}
          onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
          onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
          onCurrent={() => fetchCurrentWeek()}
        />
        <StatusBadge status="LOCKED" />
      </NavBar>

      <div className="px-6 py-4">
        {/* Alert banner */}
        <div className="flex items-center gap-3 p-4 bg-gradient-to-r from-amber-900/20 to-amber-900/10 border border-amber-500/20 rounded-xl mb-4" role="alert">
          <span className="text-xl">🔒</span>
          <div>
            <div className="text-sm text-amber-200 font-semibold">Week is in progress</div>
            <div className="text-xs text-amber-700">Commits are locked. Reconciliation opens Friday 5:00 PM.</div>
          </div>
        </div>

        {/* Read-only items */}
        {currentWeek.items.map(item => (
          <CommitItemRow key={item.id} item={item} readOnly dimmed />
        ))}
      </div>
    </div>
  );
}
