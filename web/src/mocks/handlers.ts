import { http, HttpResponse } from 'msw';

export const handlers = [
  http.get('*/api/wc/weekly-commits/current', () => {
    return HttpResponse.json({
      id: 'wc-1',
      userId: 'user-1',
      orgId: 'org-1',
      weekStartDate: '2026-03-23',
      weekEndDate: '2026-03-29',
      status: 'DRAFT',
      version: 0,
      items: [],
    });
  }),

  http.get('*/api/wc/rally-cries', () => {
    return HttpResponse.json([
      {
        id: 'rc-1',
        title: 'Retention',
        description: null,
        orgId: 'org-1',
        active: true,
      },
    ]);
  }),
];
