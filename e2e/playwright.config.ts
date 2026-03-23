import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  timeout: 30000,
  use: {
    baseURL: 'http://localhost:5173',
    headless: true,
  },
  webServer: [
    {
      command: 'cd ../service && ./gradlew bootRun',
      port: 8080,
      timeout: 60000,
      reuseExistingServer: true,
    },
    {
      command: 'cd ../web && npm run dev',
      port: 3001,
      timeout: 30000,
      reuseExistingServer: true,
    },
  ],
});
