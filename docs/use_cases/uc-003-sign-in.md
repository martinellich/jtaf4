# Use Case: Sign in

## Overview

**Use Case ID:** UC-003
**Use Case Name:** Sign in
**Primary Actor:** Visitor
**Goal:** Authenticate against the system in order to access organization-scoped features.
**Status:** Implemented

## Preconditions

- The visitor has a confirmed account.

## Main Success Scenario

1. Visitor opens the login overlay.
2. System displays e-mail and password fields.
3. Visitor enters credentials and submits.
4. System verifies the credentials against `SECURITY_USER` via Spring Security.
5. System issues a stateless authentication cookie (JWT, `VaadinStatelessSecurityConfigurer`) and forwards the visitor to the originally requested page, falling back to the dashboard.
6. Application drawer reveals the protected navigation links (shown for any signed-in user, independent of organization selection).

## Alternative Flows

### A1: Invalid credentials

**Trigger:** Step 4 — e-mail unknown or password mismatch.
**Flow:**

1. System reloads the login overlay with `?error` in the URL.
2. System displays the localized authentication error.
3. Visitor may retry from step 3.

### A2: Account not confirmed

**Trigger:** Step 4 — user exists but `confirmed = false`.
**Flow:**

1. Spring Security treats the account as disabled and rejects the login (same UI as A1).
2. Visitor must complete UC-002 before retrying.

### A3: Already signed in

**Trigger:** Step 1 — `SecurityContext.isUserLoggedIn()` returns true.
**Flow:**

1. System forwards the visitor to the dashboard without re-prompting.

## Postconditions

### Success Postconditions

- The visitor is authenticated via the stateless JWT cookie and can navigate to protected views.
- The MainLayout shows the username, the logout button, and the organization-bound menu items.

### Failure Postconditions

- No authentication is established.
- The visitor remains on the login overlay.

## Business Rules

### BR-005: Disabled accounts

Unconfirmed users cannot authenticate.

### BR-006: USER or ADMIN role required

Protected views require either the `USER` or `ADMIN` role.
