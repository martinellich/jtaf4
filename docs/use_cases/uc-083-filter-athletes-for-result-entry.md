# Use Case: Filter athletes for result entry

## Overview

**Use Case ID:** UC-083
**Use Case Name:** Filter athletes for result entry
**Primary Actor:** Registered User
**Goal:** Locate the next athlete quickly during result entry by ID or by name.
**Status:** Implemented

## Preconditions

- The user is in the result-entry view (UC-080).

## Main Success Scenario

1. User focuses the filter field at the top of the view.
2. User types either an athlete number or a name.
3. System recomputes the data provider on every keystroke.
4. If the input is numeric, system filters by `ATHLETE.ID = <number>`.
5. Otherwise, system filters by case-insensitive prefix on last name OR first name.
6. If exactly one athlete remains, system auto-selects the athlete and focuses the first result field.

## Alternative Flows

### A1: Empty filter

**Trigger:** Step 2 — filter cleared.
**Flow:**

1. The grid is empty (`1 = 2` condition) only before any filter has been typed.
2. Clearing the filter back to an empty string lists **all** athletes of the competition; the form below the grid is not cleared.

## Postconditions

### Success Postconditions

- The grid shows only matching athletes; the form may auto-focus when a single athlete is matched.

### Failure Postconditions

_None — the operation is read-only._

## Business Rules

### BR-052: Number-first matching

Numeric input always queries by ID, never by name; this matches printed athlete numbers used at the field.

### BR-053: Auto-selection

When a single athlete matches, the form is opened and the first result field receives focus to allow entering data without further clicks.
