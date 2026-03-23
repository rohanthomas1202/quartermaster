import { useState, useEffect, useCallback } from 'react';
import { api } from '../api/client';
import type { TeamSummary, RcdoAlignment } from '../api/types';
import { NavBar } from './NavBar';
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
      <NavBar breadcrumb="Manager">
        <WeekSelector
          weekStartDate={weekStart}
          onPrev={handlePrev}
          onNext={handleNext}
          onCurrent={handleCurrent}
        />
      </NavBar>

      <div className="px-6 py-4 space-y-4">
        <TeamSummaryCards summary={summary} />

        {/* Team table */}
        <div>
          <h3 className="text-sm font-semibold text-slate-200 mb-3">Team Members</h3>
          <div className="bg-white/[0.04] border border-white/[0.08] rounded-xl overflow-hidden">
            <div className="grid grid-cols-[2fr_1fr_1fr_1fr_1fr] px-4 py-2.5 border-b border-white/[0.08] text-[10px] uppercase tracking-widest text-slate-500">
              <div>Member</div><div>Status</div><div>Done</div><div>Partial</div><div>Not Done</div>
            </div>
            {summary.members.map(m => (
              <div key={m.userId} className="grid grid-cols-[2fr_1fr_1fr_1fr_1fr] px-4 py-3 border-b border-white/[0.06] text-sm items-center last:border-b-0">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 bg-gradient-to-br from-purple-600 to-indigo-500 rounded-full flex items-center justify-center text-[11px] font-semibold text-white">
                    {m.userId.slice(0, 2).toUpperCase()}
                  </div>
                  <span className="text-slate-200">{m.userId}</span>
                </div>
                <div>
                  <span className={`text-xs px-2 py-0.5 rounded ${
                    m.status === 'RECONCILED' ? 'bg-green-900/20 text-green-400' :
                    m.status === 'RECONCILING' ? 'bg-purple-900/20 text-purple-400' :
                    m.status === 'LOCKED' ? 'bg-amber-900/20 text-amber-400' :
                    'bg-white/[0.08] text-slate-400'
                  }`}>
                    {m.status}
                  </span>
                </div>
                <div className="text-green-400 font-semibold">{m.completedCount}</div>
                <div className="text-amber-400">{m.partialCount}</div>
                <div className="text-red-400">{m.notDoneCount}</div>
              </div>
            ))}
          </div>
        </div>

        <RcdoAlignmentChart alignment={alignment} />
      </div>
    </div>
  );
}
