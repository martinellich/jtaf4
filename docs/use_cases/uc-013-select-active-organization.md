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
4. System stores the chosen organization in the session-scoped `OrganizationProvider`.
5. System navigates to the series list of that organization.
6. MainLayout updates the drawer to show the organization key as the label of the series link and reveals Events / Clubs / Athletes navigation.

## Alternative Flows

### A1: No active organization yet

**Trigger:** A user signs in for the first time and visits a protected view directly.
**Flow:**

1. The view is rendered with `OrganizationProvider.getOrganization() == null`.
2. Listing falls back to `false condition`, so no rows are shown.
3. User must select an organization first.

## Postconditions

### Success Postconditions

- All subsequent grid views filter their data by the selected organization.
- New records created in the dialogs are stamped with that organization id.

### Failure Postconditions

- No active organization is set; protected grids stay empty.

## Business Rules

### BR-015: Single active organization

A user works in exactly one organization at a time within a session; switching is done by selecting another row.
