interface WeekSelectorProps {
  weekStartDate: string;
  onPrev: () => void;
  onNext: () => void;
  onCurrent: () => void;
}

export function WeekSelector({ weekStartDate, onPrev, onNext, onCurrent }: WeekSelectorProps) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
      <button type="button" onClick={onPrev} aria-label="Previous week">
        ←
      </button>
      <span>Week of {weekStartDate}</span>
      <button type="button" onClick={onNext} aria-label="Next week">
        →
      </button>
      <button type="button" onClick={onCurrent}>
        Current
      </button>
    </div>
  );
}
