# Use Case: Remove athlete results

## Overview

**Use Case ID:** UC-082
**Use Case Name:** Remove athlete results
**Primary Actor:** Registered User
**Goal:** Wipe all results an athlete has captured for a single competition (e.g. typing in a wrong number, restarting the entry).
**Status:** Implemented

## Preconditions

- The user is signed in.
- The athlete is selected in the result-entry view of a competition.

## Main Success Scenario

1. User clicks "Remove results" below the result form.
2. System opens a confirm dialog.
3. User confirms.
4. System resets the DNF flag to `false` (UC-081).
5. System deletes every `RESULT` row where `athlete_id` matches the selected athlete and `competition_id` matches the current competition.
6. System rebuilds the result form from scratch (cleared values).

## Alternative Flows

### A1: User cancels

**Trigger:** Step 3 — user clicks "Cancel".
**Flow:**

1. Dialog closes; nothing changes.

## Postconditions

### Success Postconditions

- No `RESULT` rows remain for the athlete in the current competition.
- The athlete's DNF flag for the corresponding category is `false`.

### Failure Postconditions

- Existing results stay untouched.

## Business Rules

### BR-051: Competition-scoped wipe

Removing results only affects the current competition; other competitions of the series keep their data.
