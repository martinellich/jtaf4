# Use Case: Select active organization

## Overview

**Use Case ID:** UC-013
**Use Case Name:** Select active organization
**Primary Actor:** Registered User
**Goal:** Pick the organization whose data the user wants to work on next.
**Status:** Implemented

## Preconditions

- The user is signed in.
- The user belongs to at least one organization.

## Main Success Scenario

1. User opens the "My Organizations" view.
2. System lists organizations the user belongs to.
3. User clicks "Select" on a row.
4. System stores the chosen organization in the session-scoped `OrganizationProvider` and persists it in the `jtaf-organization-id` cookie (max-age 24 h).
5. System navigates to the series list of that organization.
6. MainLayout updates the drawer to show the organization key as the label of the series link. (The Events / Clubs / Athletes links are visible for any signed-in user, independent of the selection.)

## Alternative Flows

### A1: No active organization yet

**Trigger:** A user visits a protected view with no active organization (and no valid `jtaf-organization-id` cookie).
**Flow:**

1. `ProtectedView.beforeEnter` reroutes to the "My Organizations" view.
2. User selects an organization and navigates on from there.

## Postconditions

### Success Postconditions

- All subsequent grid views filter their data by the selected organization.
- New records created in the dialogs are stamped with that organization id.

### Failure Postconditions

- No active organization is set; protected views reroute to the organization selection.

## Business Rules

### BR-015: Single active organization

A user works in exactly one organization at a time; switching is done by selecting another row. On a later visit the selection is restored from the `jtaf-organization-id` cookie, guarded by a membership check.
