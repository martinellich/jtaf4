# Use Case: Manage club

## Overview

**Use Case ID:** UC-060
**Use Case Name:** Manage club
**Primary Actor:** Registered User
**Goal:** Maintain the list of clubs that athletes can be assigned to within the organization.
**Status:** Implemented

## Preconditions

- The user is signed in and an active organization is selected.

## Main Success Scenario

1. User opens the "Clubs" view.
2. System lists clubs of the active organization, sorted by abbreviation.
3. User clicks "Add" or selects a row to edit.
4. System opens `ClubDialog` with abbreviation and name.
5. User enters or edits values and saves.
6. System persists the `CLUB` row stamped with `organization_id` and refreshes the grid.

## Alternative Flows

### A1: Delete a club

**Trigger:** User triggers the row's delete action.
**Flow:**

1. After confirmation the `CLUB` row is deleted.
2. The deletion fails if athletes still reference the club via `ATHLETE.club_id`.

### A2: Validation failure

**Trigger:** Step 5 — abbreviation or name empty.
**Flow:**

1. `NotEmptyValidator` marks the field invalid and the save is blocked until corrected; the dialog stays open.

## Postconditions

### Success Postconditions

- The club is added or modified inside the active organization.

### Failure Postconditions

- The grid and database are unchanged.

## Business Rules

### BR-039: Club scope

Clubs belong to one organization.

### BR-040: Athlete reference

A club cannot be deleted while athletes still reference it; reassign or delete those athletes first.
