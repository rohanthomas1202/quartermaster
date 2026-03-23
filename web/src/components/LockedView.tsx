import { useWeeklyCommitStore } from '../state/weeklyCommitStore';
import { WeekSelector } from './WeekSelector';
import { EisenhowerMatrix } from './EisenhowerMatrix';

function navigateWeek(currentDate: string, offsetDays: number): string {
  const date = new Date(currentDate);
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().slice(0, 10);
}

export function LockedView() {
  const { currentWeek, fetchCurrentWeek, fetchWeek } = useWeeklyCommitStore();

  if (!currentWeek) {
    return null;
  }

  return (
    <div>
      <WeekSelector
        weekStartDate={currentWeek.weekStartDate}
        onPrev={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, -7))}
        onNext={() => fetchWeek(navigateWeek(currentWeek.weekStartDate, 7))}
        onCurrent={() => fetchCurrentWeek()}
      />

      <h2>LOCKED</h2>

      <div role="alert">
        Week is in progress. Commits are locked. Reconciliation opens Friday 5:00 PM.
      </div>

      <EisenhowerMatrix items={currentWeek.items} readOnly />
    </div>
  );
}
