# AI Weekly Report - Frontend Specification

## Purpose
Define the frontend requirements, user flows, and state management for displaying and manually generating AI-powered Weekly Habit Reports. The backend infrastructure, cron jobs, and AI integrations (Ollama) are already in place.

## Core Rules & Constraints (From Backend)
1. **Automatic Generation**: The backend cron job generates reports automatically every Sunday at 23:59.
2. **Manual Generation**: Users can request a report manually via the API (`POST /api/reports/weekly/generate`).
3. **Data Constraint**: A minimum of **3 logged days** (Daily Entries) within the current week is strictly required to generate a report. If a user has fewer than 3 days, the backend throws an `InsufficientDataException`.

---

## 1. Data Models (Contracts)

The frontend MUST implement the following models to communicate with the backend.

```typescript
export interface WeeklyReportResponse {
  id: number;
  userId: number;
  weekStart: string; // ISO Date (YYYY-MM-DD)
  weekEnd: string;   // ISO Date (YYYY-MM-DD)
  aiContent: string; // Markdown formatted text from the AI
  generatedAt: string; // ISO DateTime
}

// Needed to calculate if the user has enough days to manually generate a report
export interface WeeklyEntriesSummary {
  totalEntriesThisWeek: number; // Count of distinct DailyEntries for the current week
}

export interface WeeklyReportSummary {
  id: number;
  weekStart: string;
  weekEnd: string;
  generatedAt: string;
}
```

---

## 2. Requirements & Scenarios

### Requirement 1: Report Dashboard Rendering
The system MUST provide a dedicated view or dashboard widget to display the most recent weekly report. The report content is delivered in Markdown format and MUST be parsed/rendered correctly into HTML.

#### Scenario 1.1: Viewing an existing report
- **GIVEN** the user navigates to the Weekly Report view
- **AND** a generated report exists for the previous or current week
- **WHEN** the frontend fetches the report data
- **THEN** the UI MUST display the `weekStart` and `weekEnd` formatted elegantly
- **AND** the UI MUST render the `aiContent` Markdown into styled HTML paragraphs, lists, and headers
- **AND** the UI MUST display the timestamp (`generatedAt`) of when the AI processed the data.

#### Scenario 1.2: No report exists yet
- **GIVEN** the user is new or no report has been generated yet
- **WHEN** the frontend fetches the report and receives a 404 Not Found or empty response
- **THEN** the UI MUST display an empty state illustration/message (e.g., "Your AI insights are brewing...")
- **AND** the UI MUST offer the Manual Generation option if conditions are met (See Req 2).

### Requirement 2: Report History
The system MUST allow users to view a list of previously generated reports and select one to view its details.

#### Scenario 2.1: Listing past reports
- **GIVEN** the user is in the Weekly Report view
- **WHEN** the component loads
- **THEN** it MUST fetch a list of all available reports for the user
- **AND** display them in a "Past Insights" sidebar or dropdown, sorted by date (newest first).

#### Scenario 2.2: Switching between reports
- **GIVEN** the user is viewing the current report
- **WHEN** the user selects a different report from the history list
- **THEN** the UI MUST update to display the details and AI content of the selected report.

### Requirement 3: Manual Report Generation
The system MUST allow users to trigger a manual generation of the weekly report, adhering to the 3-day minimum constraint enforced by the backend.

#### Scenario 2.1: Sufficient data for manual generation
- **GIVEN** the user is viewing the report section
- **AND** the user has 3 or more logged `DailyEntry` records for the current week
- **WHEN** the user clicks the "Generate Insight" button
- **THEN** the frontend MUST send a `POST` request to `/api/reports/weekly/generate`
- **AND** the UI MUST enter a "Generating" loading state (AI processing can take 10-30 seconds)
- **AND** upon success, the UI MUST immediately display the newly generated report.

#### Scenario 2.2: Insufficient data for manual generation (Proactive UI)
- **GIVEN** the user is viewing the report section
- **AND** the user has fewer than 3 logged `DailyEntry` records for the current week
- **WHEN** the dashboard renders
- **THEN** the "Generate Insight" button SHOULD be disabled or hidden
- **AND** the UI MUST display a clear, encouraging message indicating how many more days need to be logged (e.g., "Log 2 more days this week to unlock your AI analysis!").

#### Scenario 2.3: Graceful Error Handling (Backend Rejection)
- **GIVEN** the user attempts to generate a report
- **WHEN** the backend responds with a `400 Bad Request` containing an `InsufficientDataException` message
- **THEN** the UI MUST exit the loading state
- **AND** display a non-intrusive toast or inline error explaining the 3-day minimum requirement
- **AND** gracefully recover without crashing.

---

### Requirement 3: State Management & Reactivity
The frontend MUST manage the loading states robustly, as LLM generation requests are long-polling by nature.

#### Scenario 3.1: Long-polling UX
- **GIVEN** the user clicked "Generate Insight"
- **WHEN** the request is pending
- **THEN** the UI MUST show a contextual loading indicator (e.g., "Analyzing your habits...", "Generating insights...", skeleton loaders)
- **AND** the user MUST NOT be able to click the "Generate" button a second time to prevent duplicate API calls.

---

## 3. Technical Constraints (Design Guidance)

1. **Markdown Parsing**: Use a lightweight, secure Markdown parser (e.g., `marked` or Angular's built-in tools if available) to render the `aiContent`. Sanitize HTML output to prevent XSS.
2. **Reactivity**: Use Angular Signals to manage the `report` state, `isGenerating` loading flag, and `loggedDaysCount`.
3. **Service Layer**: Create a `WeeklyReportService` responsible for the `GET` and `POST` calls to the `/api/reports` endpoints. It should abstract the HTTP logic away from the UI components.
4. **Resilience**: The HTTP interceptor or the service itself MUST have an extended timeout configuration for the `POST /weekly/generate` endpoint, as local AI inference (Ollama) can be slow.