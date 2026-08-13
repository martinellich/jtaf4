# Use Case: Search athletes

## Overview

**Use Case ID:** UC-071
**Use Case Name:** Search athletes
**Primary Actor:** Registered User
**Goal:** Find athletes quickly by typing in the filter field.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.
- The user is on the "Athletes" view or has opened the `SearchAthleteDialog` from a series.

## Main Success Scenario

1. User focuses the "Filter" text field (focused automatically on view load).
2. User types a string.
3. On every keystroke (`ValueChangeMode.EAGER`) the data provider re-queries with the new filter.
4. System matches the filter within the active organization. In `AthletesView` the match is a case-insensitive **substring** search across all athlete columns (names, gender, year of birth, ids); in `SearchAthleteDialog` it is a lower-cased **prefix** match on last name or first name.
5. The grid updates with the filtered results.

## Alternative Flows

### A1: Numeric filter (in result entry)

**Trigger:** The filter in `SearchAthleteDialog` or in result entry (UC-083) is numeric.
**Flow:**

1. The condition becomes `ATHLETE.ID = <number>`, returning a single athlete row.

### A2: Empty filter

**Trigger:** Step 2 — filter cleared.
**Flow:**

1. In `SearchAthleteDialog` the grid is empty only before any filter has been typed; once the filter is cleared back to an empty string, all not-yet-enrolled athletes are listed. In `AthletesView` the full organization list is shown.

## Postconditions

### Success Postconditions

- The grid shows only athletes that match the filter.

### Failure Postconditions

_None — the operation is read-only._

## Business Rules

### BR-043: Case-insensitive matching

Matching is case-insensitive but differs per entry point: `AthletesView` compares `LIKE '%X%'` (substring) against every athlete column, while `SearchAthleteDialog` and result entry compare `LIKE 'X%'` (prefix) against last name and first name.
