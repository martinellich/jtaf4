# Use Case: View dashboard

## Overview

**Use Case ID:** UC-090
**Use Case Name:** View dashboard
**Primary Actor:** Visitor
**Goal:** Browse the public list of series and competitions across all organizations and download public reports.
**Status:** Implemented

## Preconditions

- The application is reachable; no authentication is required.

## Main Success Scenario

1. Visitor opens the root URL.
2. System loads `DashboardView` (anonymous-allowed).
3. System fetches all non-hidden series ordered by the most recent competition date in descending order.
4. For each series the system displays the logo, name, and download buttons for "Series Ranking" (UC-091) and "Club Ranking" (UC-092).
5. For each competition under the series the system shows its name, date, and a "Competition Ranking" button (UC-093).
6. If the visitor is signed in, additional buttons appear: "Diploma" (UC-094), "Event Ranking" (UC-095), and "Enter Results" (deep-links to the result-entry view).

## Alternative Flows

### A1: Series hidden

**Trigger:** Step 3 — `SERIES.hidden = true`.
**Flow:**

1. The series is omitted from the dashboard.

## Postconditions

### Success Postconditions

- The visitor sees an up-to-date overview of public series and can navigate to their reports.

### Failure Postconditions

_None — read-only flow._

## Business Rules

### BR-056: Visibility

Hidden series are excluded for everyone, including signed-in users, from the public dashboard.

### BR-057: Sort order

Series are sorted by the most recent competition date so the active season appears first.

### BR-058: Conditional admin buttons

Diploma, event ranking, and result entry actions are only rendered for authenticated users.
