# Use Case: Edit organization

## Overview

**Use Case ID:** UC-011
**Use Case Name:** Edit organization
**Primary Actor:** Registered User
**Goal:** Update the key or name of an organization the user belongs to.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The user is a member of the organization to edit.

## Main Success Scenario

1. User opens the "My Organizations" view.
2. User clicks a row in the grid.
3. System opens the organization dialog populated with the current values.
4. User edits the key and/or name.
5. User saves the dialog.
6. System persists the change and refreshes the grid.

## Alternative Flows

### A1: Validation failure

**Trigger:** Step 5 — required fields missing or unique key conflict.
**Flow:**

1. System reports the error in the dialog or via Notification.
2. User adjusts the values and resubmits.

## Postconditions

### Success Postconditions

- The organization's `name` and/or `organization_key` are updated.

### Failure Postconditions

- No persisted changes.

## Business Rules

### BR-012: Visibility of organizations

The grid only lists organizations where the signed-in user appears in `ORGANIZATION_USER`.
