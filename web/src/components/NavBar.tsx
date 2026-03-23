import type { ReactNode } from 'react';

interface Props {
  breadcrumb?: string;
  children?: ReactNode;
}

export function NavBar({ breadcrumb, children }: Props) {
  return (
    <div className="flex justify-between items-center px-6 py-3 border-b border-white/[0.08]">
      <div className="flex items-center gap-2.5">
        <div className="w-7 h-7 bg-gradient-to-br from-purple-600 to-purple-700 rounded-md flex items-center justify-center text-sm font-extrabold text-white">
          Q
        </div>
        <span className="text-base font-bold bg-gradient-to-r from-purple-300 to-indigo-400 bg-clip-text text-transparent">
          Quartermaster
        </span>
        {breadcrumb && (
          <span className="text-sm text-slate-500 ml-1">/ {breadcrumb}</span>
        )}
      </div>
      <div className="flex items-center gap-4">
        {children}
      </div>
    </div>
  );
}
