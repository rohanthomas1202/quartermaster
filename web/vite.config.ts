import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { federation } from "@module-federation/vite";

const isTest = process.env.VITEST === "true" || process.env.NODE_ENV === "test";

export default defineConfig({
  plugins: [
    react(),
    ...(!isTest
      ? [
          federation({
            name: "weekly-commits",
            filename: "remoteEntry.js",
            exposes: {
              "./App": "./src/WeeklyCommitsApp.tsx",
            },
            shared: {
              react: { singleton: true },
              "react-dom": { singleton: true },
              "react-router-dom": { singleton: true },
            },
          }),
        ]
      : []),
  ],
  build: {
    target: "esnext",
  },
  server: {
    port: 3001,
    cors: true,
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/mocks/server.ts"],
  },
});
