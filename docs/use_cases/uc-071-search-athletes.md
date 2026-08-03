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
4. System matches the substring against the athletes' names (case-insensitive prefix on first/last name) within the active organization.
5. The grid updates with the filtered results.

## Alternative Flows

### A1: Numeric filter (in result entry)

**Trigger:** Result-entry filter is numeric (UC-083).
**Flow:**

1. The condition becomes `ATHLETE.ID = <number>`, returning a single athlete row.

### A2: Empty filter

**Trigger:** Step 2 — filter cleared.
**Flow:**

1. In `SearchAthleteDialog` the data provider returns no rows (empty filter); in `AthletesView` it returns the full organization list.

## Postconditions

### Success Postconditions

- The grid shows only athletes that match the filter.

### Failure Postconditions

_None — the operation is read-only._

## Business Rules

### BR-043: Case-insensitive prefix matching

Filter text is upper-cased and compared with `LIKE 'X%'` against last name and first name.
