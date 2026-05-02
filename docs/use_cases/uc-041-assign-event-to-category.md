# Use Case: Assign event to category

## Overview

**Use Case ID:** UC-041
**Use Case Name:** Assign event to category
**Primary Actor:** Registered User
**Goal:** Add a discipline (event) to a category so athletes can be scored on it.
**Status:** Implemented

## Preconditions

- The user is signed in and editing a category in `CategoryDialog`.
- At least one event exists in the active organization (UC-050).

## Main Success Scenario

1. User clicks "Add Event" in the category dialog.
2. System opens `SearchEventDialog` and lists the organization's events not yet assigned to this category.
3. User selects an event.
4. System computes the next position by taking the current maximum + 1 (starting at 0 for an empty list).
5. System inserts a new `CATEGORY_EVENT` row linking the category, event, and position.
6. System refreshes the events grid in the category dialog.

## Alternative Flows

### A1: No events available

**Trigger:** Step 2 — the organization has no events yet.
**Flow:**

1. The search dialog shows an empty grid.
2. User cancels and creates events first via UC-050.

## Postconditions

### Success Postconditions

- The category includes the new event at the next position.
- Result entry (UC-080) for athletes in the category will offer this event.

### Failure Postconditions

- The category's event list is unchanged.

## Business Rules

### BR-033: Maximum events per category

A category may schedule at most ten events; the UI is expected to enforce this limit.

### BR-034: Position determines order

Event order in result entry and reports follows the `position` of `CATEGORY_EVENT`.
