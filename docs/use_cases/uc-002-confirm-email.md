# Use Case: Confirm e-mail

## Overview

**Use Case ID:** UC-002
**Use Case Name:** Confirm e-mail
**Primary Actor:** Visitor
**Goal:** Activate a registered account by clicking the confirmation link sent by e-mail.
**Status:** Implemented

## Preconditions

- The visitor has registered an account (UC-001) and received the confirmation e-mail.
- The confirmation token still matches the unconfirmed user.

## Main Success Scenario

1. Visitor clicks the confirmation link `/<host>/confirm?cf=<token>` from the e-mail.
2. System reads the `cf` query parameter.
3. System looks up the user by confirmation token.
4. System sets `confirmed = true` on the user record.
5. System displays a success message and a link to sign in.

## Alternative Flows

### A1: Token unknown

**Trigger:** Step 3 — no user matches the token (already confirmed, mistyped, or revoked).
**Flow:**

1. System displays the failure message and hides the success block.

### A2: Confirmation parameter missing

**Trigger:** Step 2 — the URL has no `cf` parameter.
**Flow:**

1. System shows the failure heading without attempting a database lookup.

## Postconditions

### Success Postconditions

- The user's `confirmed` flag is `true`.
- The user can sign in (UC-003).

### Failure Postconditions

- The user remains unconfirmed.
- The visitor stays on the confirmation page with the failure message.

## Business Rules

### BR-004: Single-use confirmation

A confirmation token is consumed once; subsequent visits with the same token find the user already confirmed and lead to the failure flow.
