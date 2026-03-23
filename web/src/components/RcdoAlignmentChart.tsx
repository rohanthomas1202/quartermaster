import type { RcdoAlignment } from '../api/types';

interface RcdoAlignmentChartProps {
  alignment: RcdoAlignment;
}

export function RcdoAlignmentChart({ alignment }: RcdoAlignmentChartProps) {
  return (
    <div aria-label="RCDO Alignment">
      <h3>RCDO Alignment</h3>
      {alignment.rallyCries.map((rc) => (
        <div key={rc.rallyCryId} style={{ marginBottom: '8px' }}>
          <div>
            {rc.rallyCryTitle} — {rc.commitCount} commits ({rc.percentage}%)
          </div>
          <div
            style={{
              background: '#e0e0e0',
              borderRadius: '4px',
              height: '12px',
              overflow: 'hidden',
            }}
          >
            <div
              style={{
                width: `${rc.percentage}%`,
                background: '#4caf50',
                height: '100%',
              }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}
