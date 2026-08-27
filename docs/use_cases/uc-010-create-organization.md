# Use Case: Create organization

## Overview

**Use Case ID:** UC-010
**Use Case Name:** Create organization
**Primary Actor:** Registered User
**Goal:** Create a new tenant organization that the user owns and immediately belongs to.
**Status:** Implemented

## Preconditions

- The user is signed in (UC-003).
- The user is on the "My Organizations" view.

## Main Success Scenario

1. User clicks "Add" above the organizations grid.
2. System opens the organization dialog with `owner` pre-filled to the user's e-mail.
3. User enters a unique organization key and a display name.
4. User saves the dialog.
5. System persists the new `ORGANIZATION` row.
6. System creates an `ORGANIZATION_USER` link between the new organization and the current user.
7. System reloads the grid and the new organization is listed.

## Alternative Flows

### A1: Organization key already taken

**Trigger:** Step 5 — the unique constraint on `organization_key` is violated.
**Flow:**

1. System surfaces the database error in a Notification; the dialog stays open.
2. User edits the key in the dialog and resubmits.

### A2: Required field missing

**Trigger:** Step 4 — name or key is empty.
**Flow:**

1. System highlights the missing fields and blocks the save; the dialog stays open.

## Postconditions

### Success Postconditions

- A new `ORGANIZATION` exists.
- The current user is a member of the new organization.

### Failure Postconditions

- No organization is created.
- No membership row is added.

## Business Rules

### BR-010: Tenant isolation

All series, competitions, events, clubs, and athletes are scoped to one organization; only members of that organization can manage them.

### BR-011: Membership upon creation

The user who creates an organization becomes both its `owner` (e-mail) and a member through `ORGANIZATION_USER`.
