# Use Case: Mark athlete DNF

## Overview

**Use Case ID:** UC-081
**Use Case Name:** Mark athlete DNF
**Primary Actor:** Registered User
**Goal:** Flag an athlete as "Did Not Finish" so the series ranking excludes their results from cumulative scoring.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The athlete is selected in the result-entry view of a competition.

## Main Success Scenario

1. User toggles the DNF checkbox in the result-entry form.
2. System updates `CATEGORY_ATHLETE.dnf` for that (athlete, category) pair.
3. The change is persisted.

## Alternative Flows

### A1: Update fails

**Trigger:** Step 2 — `CATEGORY_ATHLETE` row is missing or update returns 0 rows.
**Flow:**

1. `CategoryAthleteDAO.setDnf` throws `IllegalStateException`.
2. System shows the localized message "DNF could not be set".
3. The checkbox state is left as the user toggled.

## Postconditions

### Success Postconditions

- The athlete is marked as DNF for the category.
- Series ranking (UC-091) excludes the athlete (`CATEGORY_ATHLETE.DNF.eq(false)` filter).
- Competition ranking (UC-093) still lists the athlete but flagged as DNF.

### Failure Postconditions

- The DNF state remains as before.

## Business Rules

### BR-050: DNF scope

DNF is recorded per (athlete, category); it is series-wide, not per-event nor per-competition.
