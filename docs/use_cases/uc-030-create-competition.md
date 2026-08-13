# Use Case: Create competition

## Overview

**Use Case ID:** UC-030
**Use Case Name:** Create competition
**Primary Actor:** Registered User
**Goal:** Add a new competition (a single event date) to a series.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The user has opened a series in `SeriesView`.
- The "Competitions" tab is active.

## Main Success Scenario

1. User clicks the "+" / Add action in the competitions grid.
2. System opens `CompetitionDialog` with `medal_percentage = 0` and `series_id` set to the current series.
3. User enters a name, a date, and a medal percentage (0–100) and (optionally) checks "Always first three medals".
4. User saves.
5. System persists the competition and refreshes the grid.

## Alternative Flows

### A1: Required fields missing

**Trigger:** Step 4 — name, date, or medal percentage is missing, or the percentage is outside 0–100.
**Flow:**

1. Validation marks the affected fields and blocks the save; the dialog stays open.

## Postconditions

### Success Postconditions

- A new `COMPETITION` row exists, linked to the series.
- The competition is listed in the "Competitions" grid and offered with sheet/number download links.

### Failure Postconditions

- No competition is created.

## Business Rules

### BR-026: Default medal percentage

A newly created competition starts with `medal_percentage = 0`. Medals (and therefore diplomas) are awarded only when `medal_percentage > 0`; within that, `always_first_three_medals` raises the medal count to at least three.

### BR-027: Series binding

A competition always belongs to exactly one series.
