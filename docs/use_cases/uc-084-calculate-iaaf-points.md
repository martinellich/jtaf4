# Use Case: Calculate IAAF points

## Overview

**Use Case ID:** UC-084
**Use Case Name:** Calculate IAAF points
**Primary Actor:** System
**Goal:** Convert a raw performance into a comparable point value using the event's IAAF coefficients.
**Status:** Implemented

## Preconditions

- An `EVENT` exists with valid coefficients A, B, C and a known `event_type` (`RUN`, `RUN_LONG`, or `JUMP_THROW`).
- A user is entering a result value for that event (UC-080).

## Main Success Scenario

1. UC-080 invokes `ResultCalculator.calculatePoints(eventRecord, resultValue)`.
2. System parses the raw result value according to the event type:
   * `RUN` — seconds with optional centiseconds, e.g. `12.34`.
   * `RUN_LONG` — minutes and seconds separated by a **dot**, e.g. `2.15` = 2 min 15 s (no centiseconds; a `:` is rejected).
   * `JUMP_THROW` — distance in **metres**, e.g. `3.11`; converted to centimetres internally.
3. System applies the IAAF formula:
   * Time events: `points = A * ((B - timeInCentiseconds) / 100)^C`.
   * Distance events: `points = A * ((distanceInCentimeters - B) / 100)^C`.
4. System returns the rounded integer point value.
5. UC-080 stores the points alongside the result.

## Alternative Flows

### A1: Invalid format

**Trigger:** Step 2 — the value cannot be parsed.
**Flow:**

1. The calculator throws a `NumberFormatException`.
2. The result-entry view catches it, shows an "Invalid result" notification, and saves nothing; `RESULT.points` is never null.

### A2: Performance below scoring threshold

**Trigger:** Step 3 — the formula yields a negative value (e.g. time slower than `B`, distance below `B`).
**Flow:**

1. The calculator explicitly clamps the points to zero (`NaN` or negative results of the formula return 0).

## Postconditions

### Success Postconditions

- The points field reflects the calculated value.
- The persisted `RESULT.points` column contains that value.

### Failure Postconditions

- Invalid input leaves the stored results unchanged; the user sees an "Invalid result" notification.

## Business Rules

### BR-054: IAAF formula choice by event type

`event_type` selects the parsing logic and the formula variant; coefficients A, B, C come from the event itself.

### BR-055: No negative points

Points cannot be negative; underperforming athletes receive zero points for that event.
