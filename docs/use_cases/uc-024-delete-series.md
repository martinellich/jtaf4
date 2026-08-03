# Use Case: Delete series

## Overview

**Use Case ID:** UC-024
**Use Case Name:** Delete series
**Primary Actor:** Registered User
**Goal:** Remove a series and its category structure from the active organization.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.
- The series belongs to the active organization.

## Main Success Scenario

1. User clicks the red "Delete" button on a series row.
2. System opens the "Are you sure?" confirmation dialog.
3. User confirms.
4. Within a single transaction the system deletes:
   1. all `CATEGORY_EVENT` rows of categories belonging to the series,
   2. all `CATEGORY` rows of the series,
   3. the `SERIES` row itself.
5. System refreshes the grid.

## Alternative Flows

### A1: Foreign-key violation

**Trigger:** Step 4 — competitions, results, or `CATEGORY_ATHLETE` rows still reference the series.
**Flow:**

1. The transaction rolls back.
2. System shows the database error in a Notification.
3. User must remove dependent competitions / results before retrying.

### A2: User cancels

**Trigger:** Step 3 — user clicks "Cancel".
**Flow:**

1. Dialog closes; nothing is deleted.

## Postconditions

### Success Postconditions

- The series and all of its categories (with their event assignments) are removed.

### Failure Postconditions

- The series and its data remain.

## Business Rules

### BR-023: Confirmation required

Series deletion is destructive and requires explicit user confirmation.
