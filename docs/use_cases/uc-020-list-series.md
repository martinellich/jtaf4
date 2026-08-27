# Use Case: List series

## Overview

**Use Case ID:** UC-020
**Use Case Name:** List series
**Primary Actor:** Registered User
**Goal:** Browse all series of the active organization, including how many athletes are registered.
**Status:** Implemented

## Preconditions

- The user is signed in.
- An active organization is selected (UC-013).

## Main Success Scenario

1. User clicks the organization-keyed link in the drawer.
2. System loads `SeriesListView` and queries series where `organization_id` matches the active organization, sorted by name (descending).
3. For each series the system displays the logo, name, athlete count (`countAthletesBySeriesId`), `hidden` flag, and `locked` flag.
4. User can sort by name, jump into a series row to manage it, or use the "Add" / "Delete" actions.

## Alternative Flows

### A1: No active organization

**Trigger:** Step 2 — `OrganizationProvider.getOrganization()` is null.
**Flow:**

1. `ProtectedView.beforeEnter` reroutes to the "My Organizations" view; the series list is not rendered.
2. User selects an organization (UC-013) and returns.

## Postconditions

### Success Postconditions

- The grid renders the series of the active organization.

### Failure Postconditions

- Without an active organization the user is rerouted to the organization selection; the grid is empty only when the organization owns no series yet.

## Business Rules

### BR-016: Sort order

Series are listed by name in descending order by default.
