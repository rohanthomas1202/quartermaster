import { useState, useEffect, useCallback } from 'react';
import { api } from '../api/client';
import type { TeamSummary, RcdoAlignment } from '../api/types';
import { WeekSelector } from './WeekSelector';
import { TeamSummaryCards } from './TeamSummaryCards';
import { RcdoAlignmentChart } from './RcdoAlignmentChart';

function getCurrentMonday(): string {
  const now = new Date();
  const day = now.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  const monday = new Date(now);
  monday.setDate(now.getDate() + diff);
  return monday.toISOString().split('T')[0];
}

function shiftWeek(dateStr: string, days: number): string {
  const d = new Date(dateStr + 'T00:00:00');
  d.setDate(d.getDate() + days);
  return d.toISOString().split('T')[0];
}

export function ManagerDashboard() {
  const [weekStart, setWeekStart] = useState(getCurrentMonday);
  const [summary, setSummary] = useState<TeamSummary | null>(null);
  const [alignment, setAlignment] = useState<RcdoAlignment | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setError(null);

    Promise.all([
      api.getTeamSummary(weekStart),
      api.getRcdoAlignment(weekStart),
    ])
      .then(([s, a]) => {
        if (!cancelled) {
          setSummary(s);
          setAlignment(a);
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Failed to load dashboard');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [weekStart]);

  const handlePrev = useCallback(() => {
    setWeekStart((w) => shiftWeek(w, -7));
  }, []);

  const handleNext = useCallback(() => {
    setWeekStart((w) => shiftWeek(w, 7));
  }, []);

  const handleCurrent = useCallback(() => {
    setWeekStart(getCurrentMonday());
  }, []);

  if (error) {
    return <div role="alert">Error: {error}</div>;
  }

  if (!summary || !alignment) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <WeekSelector
        weekStartDate={weekStart}
        onPrev={handlePrev}
        onNext={handleNext}
        onCurrent={handleCurrent}
      />

      <TeamSummaryCards summary={summary} />

      <table>
        <thead>
          <tr>
            <th>Member</th>
            <th>Status</th>
            <th>Completed</th>
            <th>Partial</th>
            <th>Not Done</th>
          </tr>
        </thead>
        <tbody>
          {summary.members.map((m) => (
            <tr key={m.userId}>
              <td>{m.userId}</td>
              <td>{m.status}</td>
              <td>{m.completedCount}</td>
              <td>{m.partialCount}</td>
              <td>{m.notDoneCount}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <RcdoAlignmentChart alignment={alignment} />
    </div>
  );
}
