# Use Case: Manage athlete

## Overview

**Use Case ID:** UC-070
**Use Case Name:** Manage athlete
**Primary Actor:** Registered User
**Goal:** Maintain the central list of athletes registered in the active organization.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.

## Main Success Scenario

1. User opens the "Athletes" view.
2. System lists athletes of the active organization, sorted by gender, year of birth, last name, first name.
3. User clicks "Add" or selects a row to edit.
4. System opens `AthleteDialog` with last name, first name, gender, year of birth, and an optional club selector.
5. User fills in the fields and saves.
6. System persists the `ATHLETE` row stamped with `organization_id` and refreshes the grid.

## Alternative Flows

### A1: Delete an athlete

**Trigger:** User triggers the row's delete action.
**Flow:**

1. After confirmation the `ATHLETE` row is deleted.
2. The deletion fails if `RESULT` rows or `CATEGORY_ATHLETE` rows still reference the athlete.

### A2: Year of birth not numeric

**Trigger:** Step 5 — `JtafStringToIntegerConverter` rejects the value.
**Flow:**

1. The dialog shows "Must be a number", the save is blocked, and the dialog stays open.

## Postconditions

### Success Postconditions

- The athlete exists in the organization and is searchable for series enrolment (UC-071, UC-072).

### Failure Postconditions

- No row is created or modified.

## Business Rules

### BR-041: Organization scope

Athletes belong to exactly one organization.

### BR-042: Optional club

Club assignment is optional; an athlete without a club still appears in rankings but no club ranking row is contributed.
