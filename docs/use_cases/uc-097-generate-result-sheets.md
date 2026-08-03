# Use Case: Generate result sheets

## Overview

**Use Case ID:** UC-097
**Use Case Name:** Generate result sheets
**Primary Actor:** Registered User
**Goal:** Print one prefilled result sheet per athlete enrolled in the series, used by judges at the field.
**Status:** Implemented

## Preconditions

- The user is signed in and is editing a series in `SeriesView`.
- The competition exists.
- Athletes are enrolled in the series.

## Main Success Scenario

1. User clicks one of the "Sheets" links on a competition row:
   * "Sheets" — ordered by category, last name, first name.
   * "Ordered by club" — ordered by club, category, last name, first name.
2. System triggers a download.
3. `NumberAndSheetsService.createSheets` joins athletes with their categories, gathers each category's events ordered by `position`, and pulls the series logo.
4. System renders `SheetsReport` in the user's locale.
5. Browser downloads `sheets_orderby_(athlete|club)<competitionId>.pdf`.

## Alternative Flows

### A1: Competition not found

**Trigger:** Step 3 — the competition id is unknown.
**Flow:**

1. `getCompetition().orElseThrow()` raises an exception and the browser shows an error.

## Postconditions

### Success Postconditions

- A PDF with prefilled sheets (athlete identity, category, events) is delivered.

### Failure Postconditions

- No file is downloaded; the user sees an error.

## Business Rules

### BR-070: Events come from the category

Each sheet enumerates exactly the events of the athlete's category, in `position` order.

### BR-071: Logo from series

The sheet header uses the series logo when available.
