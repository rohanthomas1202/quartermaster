import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import WeeklyCommitsApp from "./WeeklyCommitsApp";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter basename="/weekly-commits">
      <WeeklyCommitsApp />
    </BrowserRouter>
  </StrictMode>,
);
