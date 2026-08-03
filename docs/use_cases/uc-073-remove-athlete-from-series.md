# Use Case: Remove athlete from series

## Overview

**Use Case ID:** UC-073
**Use Case Name:** Remove athlete from series
**Primary Actor:** Registered User
**Goal:** Drop an athlete from all categories of a given series.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.
- The athlete is currently enrolled in at least one category of the series.

## Main Success Scenario

1. User clicks "Remove" on the athlete row in the series athletes grid.
2. System opens the "Are you sure?" confirm dialog.
3. User confirms.
4. System deletes every `CATEGORY_ATHLETE` row of the athlete whose category belongs to the series.
5. System refreshes the series athletes grid.

## Alternative Flows

### A1: User cancels

**Trigger:** Step 3 — user clicks "Cancel".
**Flow:**

1. Dialog closes; the enrolment remains.

## Postconditions

### Success Postconditions

- The athlete is no longer enrolled in any category of the series.
- The athlete row in the global athletes view is unaffected; results from prior competitions of the series remain in the database.

### Failure Postconditions

- The athlete remains enrolled.

## Business Rules

### BR-046: Series-level removal

Removing an athlete from the series only deletes the `CATEGORY_ATHLETE` rows; the underlying athlete record and any captured results are preserved.
