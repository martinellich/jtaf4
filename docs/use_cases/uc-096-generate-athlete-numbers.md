# Use Case: Generate athlete numbers

## Overview

**Use Case ID:** UC-096
**Use Case Name:** Generate athlete numbers
**Primary Actor:** Registered User
**Goal:** Print the bib numbers for all athletes registered to a series, ordered the way the user prefers.
**Status:** Implemented

## Preconditions

- The user is signed in and is editing a series in `SeriesView`.
- Athletes are enrolled in the series (UC-072).

## Main Success Scenario

1. User clicks one of the "Numbers" links on a competition row inside the Competitions tab:
   * "Numbers" — ordered by category, last name, first name.
   * "Ordered by club" — ordered by club, category, last name, first name.
2. System triggers a download.
3. `NumberAndSheetsService.createNumbers` runs the jOOQ query joining `CATEGORY_ATHLETE`, `ATHLETE`, `CATEGORY`, and `CLUB`.
4. System renders `NumbersReport` in the user's locale.
5. Browser downloads `numbers_orderby_(athlete|club)<competitionId>.pdf`.

## Alternative Flows

### A1: No athletes enrolled

**Trigger:** Step 3 — `getAthletes` returns an empty list.
**Flow:**

1. The PDF is generated empty.

## Postconditions

### Success Postconditions

- A PDF with one bib number per athlete is delivered.

### Failure Postconditions

- No file is downloaded; the user sees an error.

## Business Rules

### BR-069: Sort selection at click time

The two link variants exist precisely so the same data can be printed in different orders without changing settings.
