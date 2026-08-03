# Use Case: Register account

## Overview

**Use Case ID:** UC-001
**Use Case Name:** Register account
**Primary Actor:** Visitor
**Goal:** Create a new JTAF account so the visitor can sign in and manage athletics data.
**Status:** Implemented

## Preconditions

- The visitor is on the registration page (`/register`).
- The visitor owns a valid e-mail address that can receive mail.

## Main Success Scenario

1. Visitor opens the registration form.
2. System displays fields for first name, last name, e-mail, and password.
3. Visitor fills in all required fields.
4. Visitor submits the form.
5. System hashes the password, generates a confirmation token, and stores a new unconfirmed user.
6. System assigns the user to the `USER` security group.
7. System sends a confirmation e-mail containing a link with the token.
8. System notifies the visitor that the e-mail has been sent and navigates to the public dashboard.

## Alternative Flows

### A1: E-mail already registered

**Trigger:** Step 5 — an active user with the same e-mail already exists.
**Flow:**

1. System rejects the request and rolls back the transaction.
2. System shows the message "User already exists".
3. Visitor stays on the registration form.

### A2: Required field missing or invalid

**Trigger:** Step 4 — first name, last name, e-mail, or password is empty.
**Flow:**

1. System highlights the missing fields.
2. Visitor corrects the input and resubmits at step 4.

## Postconditions

### Success Postconditions

- A new `SECURITY_USER` row exists with `confirmed = false` and a confirmation token.
- The user is linked to the `USER` group via `USER_GROUP`.
- A confirmation e-mail has been dispatched.

### Failure Postconditions

- No user is created.
- No confirmation e-mail is sent.

## Business Rules

### BR-001: Unique e-mail

E-mail addresses are unique across the whole system.

### BR-002: Password storage

Passwords are stored as BCrypt hashes; the plain text is never persisted.

### BR-003: Default role

Every newly registered user receives the `USER` role.
