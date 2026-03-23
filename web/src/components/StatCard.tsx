interface Props {
  value: number;
  label: string;
  sublabel: string;
  accentColor: string;
  bgColor: string;
  borderColor: string;
}

export function StatCard({ value, label, sublabel, accentColor, bgColor, borderColor }: Props) {
  return (
    <div className="flex items-center gap-3">
      <div className={`w-9 h-9 bg-gradient-to-br ${bgColor} border ${borderColor} rounded-lg flex items-center justify-center text-lg font-bold ${accentColor}`}>
        {value}
      </div>
      <div>
        <div className="text-sm text-slate-200 font-medium">{label}</div>
        <div className="text-xs text-slate-500">{sublabel}</div>
      </div>
    </div>
  );
}
