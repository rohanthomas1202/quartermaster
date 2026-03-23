import { useEffect, type ReactNode } from 'react';

interface Props {
  open: boolean;
  onClose: () => void;
  title: string;
  children: ReactNode;
}

export function SlideOver({ open, onClose, title, children }: Props) {
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (open) document.addEventListener('keydown', handleEsc);
    return () => document.removeEventListener('keydown', handleEsc);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <>
      <div className="fixed inset-0 bg-black/70 z-40" onClick={onClose} />
      <div className="fixed right-0 top-0 bottom-0 w-1/2 min-w-[400px] bg-gradient-to-b from-slate-800 to-slate-900 border-l border-white/[0.15] z-50 shadow-[-8px_0_24px_rgba(0,0,0,0.5)] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-semibold text-slate-200">{title}</h2>
            <button onClick={onClose} className="text-slate-500 hover:text-slate-300 text-xl">{'\u2715'}</button>
          </div>
          {children}
        </div>
      </div>
    </>
  );
}
