# Use Case: Sign out

## Overview

**Use Case ID:** UC-004
**Use Case Name:** Sign out
**Primary Actor:** Registered User
**Goal:** End the current authenticated session.
**Status:** Implemented

## Preconditions

- The user is authenticated.

## Main Success Scenario

1. User clicks the "Logout" button in the top navigation bar.
2. System invokes Spring Security logout, invalidates the session, and clears the security context.
3. System redirects the user to the dashboard.
4. MainLayout hides protected navigation entries and shows the "Login" / "Register" actions.

## Alternative Flows

_None._

## Postconditions

### Success Postconditions

- The user's session is invalidated.
- Protected views are no longer accessible without re-authenticating.

### Failure Postconditions

- The user remains signed in if logout fails (e.g. network error); the UI then remains on the current view.

## Business Rules

### BR-007: Session invalidation

Logout fully invalidates the HTTP session; bookmarked protected URLs require a fresh sign-in.
