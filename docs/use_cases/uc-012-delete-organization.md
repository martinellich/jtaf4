# Use Case: Delete organization

## Overview

**Use Case ID:** UC-012
**Use Case Name:** Delete organization
**Primary Actor:** Registered User
**Goal:** Remove an organization the user no longer needs, together with all its membership links.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The user is a member of the target organization.
- All series, competitions, events, clubs, and athletes belonging to the organization have already been removed (referential integrity must allow the delete).

## Main Success Scenario

1. User clicks the red "Delete" button on an organization row.
2. System opens the "Are you sure?" confirmation dialog.
3. User confirms.
4. System removes the organization-user links and the organization itself in one transaction.
5. System refreshes the grid.

## Alternative Flows

### A1: Foreign-key violation

**Trigger:** Step 4 — the organization still owns series, events, clubs, or athletes.
**Flow:**

1. System catches the database error and shows the message in a Notification.
2. The organization is not deleted.

### A2: User cancels

**Trigger:** Step 3 — user clicks "Cancel" in the confirmation dialog.
**Flow:**

1. Dialog closes; nothing is deleted.

## Postconditions

### Success Postconditions

- The `ORGANIZATION` row and all related `ORGANIZATION_USER` rows are gone.

### Failure Postconditions

- The organization remains intact and visible.

## Business Rules

### BR-013: Cascading membership removal

Deleting an organization automatically removes its membership links so other users no longer see it.

### BR-014: Manual cleanup of dependent data

Series, competitions, events, clubs, and athletes must be deleted explicitly before an organization can be removed.
