# Use Case Coverage — Gap Analysis

**Date:** 2026-08-29
**Scope:** all 45 use cases in [`use_cases/`](use_cases/README.md), mode *both* (implementation and tests)
**Method:** triage pre-pass over every specification (status, id markers in `src/main` and `src/test`), followed by full specification-coverage audits of five use cases. The audit maps every precondition, main-scenario step, alternative flow, postcondition, and business rule onto the code and tests that realize it. This is specification coverage, not line coverage. No build or test run was performed as part of this analysis.

---

## 1. Summary

| Finding | Detail |
|---|---|
| Status of all 45 specs | `Implemented` |
| Implementation markers (`UC-XXX` in `src/main`) | none, for any use case |
| Test markers (`UC-XXX` in `src/test`) | present for all 45 — one `UCxxx…Test.java` per use case under `src/test/java/ch/jtaf/ui/usecase/` |
| Playwright tests (`src/it`) | exist for dashboard and organizations views; carry no use case markers |
| Full audits performed | UC-003, UC-072, UC-080, UC-083, UC-091 |
| Implementation coverage of audited use cases | 100 % — every unit has enforcing code with a `file:line` |
| Test coverage of audited use cases | 33–54 % of units; alternative flows, failure postconditions and business-rule "rejected" cases are largely untested |
| Suggested status change | none — `Implemented` is accurate for all five; `Tested` is not justified for any |

### Audited use cases at a glance

| Use case | Implementation | Tests | Suggested status |
|---|---|---|---|
| UC-003 Sign in | 15/15 | 5/15 | keep `Implemented` |
| UC-072 Assign athlete to series | 16/16 (2 partial) | 7/16 | keep `Implemented` |
| UC-080 Enter result | 20/20 (1 n/a) | 11/21 | keep `Implemented` |
| UC-083 Filter athletes for result entry | 11/11 | 4/11 | keep `Implemented` |
| UC-091 Download series ranking | 13/13 | 7/13 | keep `Implemented` |

### Cross-cutting findings

These recur in all five audits and are almost certainly project-wide:

1. **Implementation is complete, traceability is not.** No production class references its use case id. The auditor located code via domain vocabulary. This is a documentation gap, not a behaviour gap, but it makes every future audit slower and less certain.
2. **Tests are happy-path smoke tests.** Each `UCxxx…Test` exercises the main success scenario once. Alternative flows (`A1`, `A2`, …), failure postconditions (`Post-F-*`) and the "rejected" side of business rules are mostly absent.
3. **Outcomes are asserted indirectly.** Where a unit is *Partial*, the test typically proves the outcome by grid size or "no exception" rather than reading the persisted row, asserting the resolved category, or checking the specific UI state (read-only, focus, dialog closed, notification text).
4. **Browserless `login()` bypasses Spring Security.** `AbstractViewTest.login(...)` places a hand-built token into `SecurityContextHolder`. Nothing in the suite authenticates with real credentials, so the JWT cookie, password verification and redirect behaviour are never exercised.
5. **No `@UseCase` annotation is used anywhere.** Scenario and business-rule claims per test method are not recorded; the id lives only in the test class name and Javadoc.

### Notable individual findings

- **`UC003SignInTest.login_with_unknown_user` asserts the opposite of the specification.** It asserts a bare `<vaadin-login-overlay>` — i.e. *no* error state — after an unknown-user submit, because the browserless submit never reaches Spring Security (`UC003SignInTest.java:37-38`).
- **`UC091DownloadSeriesRankingTest` cannot detect a broken PDF.** `SeriesRankingReport.java:47-50` swallows generation errors and returns `new byte[0]`; the test discards the stream and only asserts no exception.
- **BR-045 (one category per series) is enforced only by a code guard.** The `CATEGORY_ATHLETE` primary key `(athlete_id, category_id)` does not prevent a second category of the same series; `CategoryAthleteDAO.isAssignedToSeries` does — and has no test.
- `src/test/resources/application.properties:5` contains the test `jwt.auth.secret` in plain text; `V9999__Data.sql` contains values that the Sonar secrets hook flags as credentials.

---

## 2. Triage table — all 45 use cases

| ID | Title | Status | Impl marker | Test marker | Test file (`@Test` count) | Audited |
|---|---|---|---|---|---|---|
| UC-001 | Register account | Implemented | ✗ | ✓ | UC001RegisterAccountTest (3) | |
| UC-002 | Confirm e-mail | Implemented | ✗ | ✓ | UC002ConfirmEmailTest (4) | |
| UC-003 | Sign in | Implemented | ✗ | ✓ | UC003SignInTest (2) | ✓ |
| UC-004 | Sign out | Implemented | ✗ | ✓ | UC004SignOutTest (1) | |
| UC-005 | Switch language | Implemented | ✗ | ✓ | UC005SwitchLanguageTest (1) | |
| UC-010 | Create organization | Implemented | ✗ | ✓ | UC010CreateOrganizationTest (1) | |
| UC-011 | Edit organization | Implemented | ✗ | ✓ | UC011EditOrganizationTest (1) | |
| UC-012 | Delete organization | Implemented | ✗ | ✓ | UC012DeleteOrganizationTest (1) | |
| UC-013 | Select active organization | Implemented | ✗ | ✓ | UC013SelectActiveOrganizationTest (1) | |
| UC-020 | List series | Implemented | ✗ | ✓ | UC020ListSeriesTest (1) | |
| UC-021 | Create series | Implemented | ✗ | ✓ | UC021CreateSeriesTest (1) | |
| UC-022 | Edit series | Implemented | ✗ | ✓ | UC022EditSeriesTest (1) | |
| UC-023 | Upload series logo | Implemented | ✗ | ✓ | UC023UploadSeriesLogoTest (1) | |
| UC-024 | Delete series | Implemented | ✗ | ✓ | UC024DeleteSeriesTest (1) | |
| UC-025 | Copy categories from another series | Implemented | ✗ | ✓ | UC025CopyCategoriesTest (2) | |
| UC-030 | Create competition | Implemented | ✗ | ✓ | UC030CreateCompetitionTest (1) | |
| UC-031 | Edit competition | Implemented | ✗ | ✓ | UC031EditCompetitionTest (1) | |
| UC-032 | Delete competition | Implemented | ✗ | ✓ | UC032DeleteCompetitionTest (1) | |
| UC-040 | Manage category | Implemented | ✗ | ✓ | UC040ManageCategoryTest (1) | |
| UC-041 | Assign event to category | Implemented | ✗ | ✓ | UC041AssignEventToCategoryTest (1) | |
| UC-042 | Remove event from category | Implemented | ✗ | ✓ | UC042RemoveEventFromCategoryTest (1) | |
| UC-050 | Manage event (IAAF coefficients) | Implemented | ✗ | ✓ | UC050ManageEventTest (1) | |
| UC-060 | Manage club | Implemented | ✗ | ✓ | UC060ManageClubTest (1) | |
| UC-070 | Manage athlete | Implemented | ✗ | ✓ | UC070ManageAthleteTest (1) | |
| UC-071 | Search athletes | Implemented | ✗ | ✓ | UC071SearchAthletesTest (1) | |
| UC-072 | Assign athlete to series | Implemented | ✗ | ✓ | UC072AssignAthleteToSeriesTest (1) | ✓ |
| UC-073 | Remove athlete from series | Implemented | ✗ | ✓ | UC073RemoveAthleteFromSeriesTest (1) | |
| UC-074 | Import athletes from Excel | Implemented | ✗ | ✓ | UC074ImportAthletesFromExcelTest (2) + AthleteImportServiceTest | |
| UC-080 | Enter result | Implemented | ✗ | ✓ | UC080EnterResultTest (3) | ✓ |
| UC-081 | Mark athlete DNF | Implemented | ✗ | ✓ | UC081MarkAthleteDnfTest (1) | |
| UC-082 | Remove athlete results | Implemented | ✗ | ✓ | UC082RemoveAthleteResultsTest (1) | |
| UC-083 | Filter athletes for result entry | Implemented | ✗ | ✓ | UC083FilterAthletesForResultEntryTest (1) | ✓ |
| UC-084 | Calculate IAAF points | Implemented | ✗ | ✓ | UC084CalculateIaafPointsTest (4) | |
| UC-085 | Assign athlete during result entry | Implemented | ✗ | ✓ | UC085AssignAthleteDuringResultEntryTest (3) | |
| UC-086 | Correct athlete during result entry | Implemented | ✗ | ✓ | UC086CorrectAthleteDuringResultEntryTest (3) | |
| UC-090 | View dashboard | Implemented | ✗ | ✓ | UC090ViewDashboardTest (3) | |
| UC-091 | Download series ranking | Implemented | ✗ | ✓ | UC091DownloadSeriesRankingTest (1) | ✓ |
| UC-092 | Download club ranking | Implemented | ✗ | ✓ | UC092DownloadClubRankingTest (1) | |
| UC-093 | Download competition ranking | Implemented | ✗ | ✓ | UC093DownloadCompetitionRankingTest (1) | |
| UC-094 | Download diplomas | Implemented | ✗ | ✓ | UC094DownloadDiplomasTest (1) | |
| UC-095 | Download event ranking | Implemented | ✗ | ✓ | UC095DownloadEventRankingTest (1) | |
| UC-096 | Generate athlete numbers | Implemented | ✗ | ✓ | UC096GenerateAthleteNumbersTest (1) | |
| UC-097 | Generate result sheets | Implemented | ✗ | ✓ | UC097GenerateResultSheetsTest (1) | |
| UC-098 | Generate empty result sheets per category | Implemented | ✗ | ✓ | UC098GenerateEmptyResultSheetsTest (1) | |
| UC-099 | Generate categories sheet | Implemented | ✗ | ✓ | UC099GenerateCategoriesSheetTest (1) | |

Rows without a tick in *Audited* are marker checks only — they are **not** coverage verdicts. Selection for the full audits favoured thin tests behind large or high-risk specifications (single `@Test`, small test file, core or security-relevant flow). UC-091 stands in for the whole report group; UC-092…095 have identically sized tests (35 lines, one `@Test`) and are expected to show the same shape.

---

## 3. Detailed audits

### 3.1 UC-003 Sign in

Implementation 15/15 · Tests 5/15 · Spec: [`use_cases/uc-003-sign-in.md`](use_cases/uc-003-sign-in.md)

Context for the test column: all UI tests extend `AbstractViewTest`; `login(...)` at `AbstractViewTest.java:59-70` places a hand-built `UsernamePasswordAuthenticationToken` into `SecurityContextHolder`. Nothing goes through the Spring Security filter chain, the `UserDetailsService`, the password encoder, or the JWT cookie. The `?error` failure URL and the success redirect are Vaadin `VaadinSecurityConfigurer.loginView(...)` defaults at `SecurityConfiguration.java:46`.

| Unit | Description | Implementation | Test | Verdict |
|---|---|---|---|---|
| Pre-1 | Visitor has a confirmed account | UserDetailsServiceImpl.java:42 | V9999__Data.sql:2283, UserDetailsServiceImplTest.java:35 | Covered |
| Step 1 | Visitor opens the login overlay | LoginView.java:11-13, :54; MainLayout.java:104 | UC003SignInTest.java:26-28 | Covered |
| Step 2 | System displays e-mail and password fields | LoginView.java:30-34 | UC003SignInTest.java:28 | Partial (fields/labels never asserted) |
| Step 3 | Visitor enters credentials and submits | LoginView.java:43 | UC003SignInTest.java:31 | Partial (submit only with unknown user; never valid credentials) |
| Step 4 | Credentials verified against `SECURITY_USER` via Spring Security | UserDetailsServiceImpl.java:29-45; SecurityConfiguration.java:35, :46 | UserDetailsServiceImplTest.java:32-35 | Partial (password verification via `AuthenticationManager`/BCrypt never exercised) |
| Step 5 | JWT cookie issued, forward to requested page, fallback dashboard | SecurityConfiguration.java:46-51; DashboardView.java:29 | — | Test missing |
| Step 6 | Drawer reveals protected links for any signed-in user | MainLayout.java:246, :258-264, :283-296 | — | Test missing |
| A1 | Invalid credentials: `?error`, localized error, retry | LoginView.java:36-37, :60; messages_*.properties:10-11 | UC003SignInTest.java:23-39 | Partial (see gap 1) |
| A2 | Account not confirmed → disabled, rejected | UserDetailsServiceImpl.java:42 | UserDetailsServiceImplTest.java:39-46 | Partial (rejection at the overlay not exercised) |
| A3 | Already signed in → forwarded to dashboard | LoginView.java:50-52 | UC003SignInTest.java:42-49 | Covered |
| Post-S-1 | Authenticated via JWT cookie, can navigate to protected views | SecurityConfiguration.java:48-51; ProtectedView.java:15 | ProtectedViewTest.java:19-25 | Partial (synthetic token; JWT cookie never asserted) |
| Post-S-2 | MainLayout shows username, logout, org-bound menu items | MainLayout.java:253-264 | UC004SignOutTest.java:29-31; UC013SelectActiveOrganizationTest.java:40-41 | Partial (Events/Clubs/Athletes links not asserted; no UC-003 marker) |
| Post-F-1 | No authentication established after failure | SecurityConfiguration.java:46 | — | Test missing |
| Post-F-2 | Visitor remains on the login overlay after failure | SecurityConfiguration.java:46; LoginView.java:54 | UC003SignInTest.java:37 | Partial (assertion is tautological browserless) |
| BR-005 | Unconfirmed users cannot authenticate | UserDetailsServiceImpl.java:42 | UserDetailsServiceImplTest.java:35, :45 | Covered |
| BR-006 | Protected views require `USER` or `ADMIN` | ProtectedView.java:15; OrganizationsView.java:29; ResultCapturingView.java:53; ProtectedGridView.java:17 | ProtectedViewTest.java:19-25 (ADMIN allowed) | Partial (no `USER`, no rejected case) |

#### Gaps

1. **A1 test asserts the opposite of the specification.** `login_with_unknown_user` (`UC003SignInTest.java:37-38`) asserts a bare `<vaadin-login-overlay>` — no `error` attribute — after an unknown-user submit, because the browserless submit never reaches Spring Security. A1 needs a test navigating to `LoginView` with `?error` and asserting `isError()` plus the `Auth.ErrorTitle`/`Auth.ErrorMessage` texts.
2. **Step 5 has no test.** Nothing asserts the JWT cookie or the redirect. Needs a `MockMvc`/`@SpringBootTest` form-POST against `/login` with the seeded user, or a Playwright test.
3. **Step 6 has no test.** No assertion that `series-list-link`, Events, Clubs, Athletes become visible after sign-in and are hidden for a visitor.
4. **Post-F-1 has no test.** After a failed login nothing asserts `securityContext.isUserLoggedIn()` is false.
5. **Steps 3/4, Post-S-1 only partially tested.** No test authenticates with a real e-mail and password. Same fix as gap 2.
6. **A2 rejection at the overlay not exercised.** Same fix as gap 2.
7. **BR-006 tested one-sided.** No anonymous-visitor bounce test, none for the `USER` role.
8. **Step 2 not asserted.** The test locates the overlay but never checks the e-mail/password fields or their labels.
9. **Traceability.** `UserDetailsServiceImplTest`, `ProtectedViewTest`, and the Post-S-2 assertions in the UC-004/UC-013 tests carry no UC-003 / BR-005 / BR-006 marker.

#### Drift

1. `UC003SignInTest.java:30-35` swallows an `IllegalStateException` from `GoogleAnalyticsTracker` around the submit (`MainLayout.java:76-79`); the A1 test cannot tell a real failure from the swallowed one.
2. `MainLayout.java:104`: the header "Login" button navigates to `OrganizationsView` and relies on the security redirect; Step 1 is realized indirectly.
3. `SecurityContext.logout()` (`SecurityContext.java:87-92`) clears a `remember-me` cookie; UC-003 describes a stateless JWT and no remember-me flow (belongs to UC-004).

#### Suggested status

`Implemented` remains correct. `Tested` is not justified: 5 of 15 units covered, and the main success path (Steps 3–6) has no test that authenticates with real credentials.

---

### 3.2 UC-072 Assign athlete to series

Implementation 16/16 (2 partial) · Tests 7/16 · Spec: [`use_cases/uc-072-assign-athlete-to-series.md`](use_cases/uc-072-assign-athlete-to-series.md)

Series "CIS 2019" is id 3; seeded athlete Zimmermann (id 1, F, 2011) is enrolled only in series 1 and matches category 35 "L" of series 3. `UC072…Test` = `UC072AssignAthleteToSeriesTest.java`; `UC085…Test` evidence is cited but does not count as UC-072 coverage since `SeriesView.onAthleteSelect` is not on that path.

| Unit | Description | Implementation | Test | Verdict |
|---|---|---|---|---|
| Pre-1 | Signed in, active organization selected | ProtectedView.java:32-40 | UC072…Test.java:34-40 via AbstractViewTest.java:95-121 | Covered |
| Pre-2 | Series opened in `SeriesView`, "Athletes" tab | SeriesView.java:130-148 | UC072…Test.java:36-47 | Covered |
| Pre-3 | Category for gender/birth year exists in the series | n/a (data precondition) | V9999__Data.sql:270 + :3 | Covered |
| Step 1 | Click "Assign Athlete" in athletes-grid header | SeriesView.java:465-473, :501 | UC072…Test.java:55 | Covered |
| Step 2 | Dialog opens; grid empty until filter; lists org athletes not yet enrolled | SearchAthleteDialog.java:83-94, :203-204; AthleteDAO.java:52-65, :67-80 | UC072…Test.java:57, :64-69 | Partial (empty-before-filter and exclusion of enrolled athlete never asserted) |
| Step 3 | Filter, click per-row "Assign Athlete" | SearchAthleteDialog.java:127-131; GridBuilder.java:34-67 | UC072…Test.java:64, :71-76 | Covered |
| Step 4 | Resolve category by series, gender, year range | CategoryDAO.java:30-38; CategoryAthleteDAO.java:38-39 | — | Partial (resolved category never asserted) |
| Step 5 | Insert `CATEGORY_ATHLETE`, `dnf` defaults false | CategoryAthleteDAO.java:41-42, :48-54; V0002__Add_DNF.sql:2 | UC072…Test.java:79 | Partial (`dnf` default not asserted) |
| Step 6 | Close dialog, refresh athletes grid | SearchAthleteDialog.java:165-168; SeriesView.java:511, :235-245 | UC072…Test.java:79-80 | Partial (dialog closure not asserted) |
| A1 | No matching category → nothing inserted, notification | CategoryAthleteDAO.java:41-44; SeriesView.java:506-509; messages_en.properties:99 | — | Test missing (UC085…Test.java:134-153 covers the DAO from the other view) |
| A2 | Already enrolled athlete excluded from search | AthleteDAO.java:56-60, :72-77; CategoryAthleteDAO.java:41 | — | Test missing |
| A3 | Overlapping categories → `TooManyRowsException`, unhandled | CategoryDAO.java:38 | — | Test missing |
| A4 | "Add" → new athlete stored and assigned; edit only refreshes | SearchAthleteDialog.java:127-131, :155-163; AthleteDialog.java:66-119; EditDialog.java:79-89 | UC085…Test.java:101-132 (other view) | Partial (SeriesView continuation and "edit only refreshes" untested) |
| Post-S-1 | `CATEGORY_ATHLETE` row links athlete to matching category | CategoryAthleteDAO.java:48-54 | UC072…Test.java:79-80 | Covered |
| Post-S-2 | Athlete shown in grid and counted in `countAthletesBySeriesId` | SeriesView.java:243-244; CategoryAthleteDAO.java:73-78 | UC072…Test.java:79-80 (grid only) | Partial (`countAthletesBySeriesId` never asserted) |
| Post-F-1 | No enrolment created on failure | CategoryAthleteDAO.java:41 | — | Test missing (UC085…Test.java:150 from other view) |
| BR-044 | Category auto-selected; no manual pick | CategoryDAO.java:30-38; no category input in dialog | UC072…Test.java:71-79 (indirect) | Partial (rejected case untested) |
| BR-045 | Exactly one category per series | AthleteDAO.java:56-60 + CategoryAthleteDAO.java:41 | — | Test missing |

BR-045 note: the primary key `(athlete_id, category_id)` (`V0001__Initial.sql:174-175`) only prevents the *same* pair twice; a second category of the same series is blocked solely by the `isAssignedToSeries` guard — enforcement in code, but exactly the guard that needs a test.

#### Gaps

1. **A1 has no UC-072 test.** Add a scenario assigning an athlete with a birth year outside every category of series 3 and assert the "No.matching.category" text and unchanged grid size.
2. **A2 has no test.** Filter for an already-enrolled athlete and assert the search grid stays empty.
3. **BR-045 has no test.** Attempt a second enrolment and assert the count stays at one.
4. **Post-F-1 has no UC-072 test.** Falls out of gap 1 — assert `isAssignedToSeries(...)` is false after the A1 path.
5. **A3 has no test.** A DAO-level test with two overlapping categories would pin the `TooManyRowsException`; alternatively the spec should state it is deliberately untested.
6. **A4 untested from `SeriesView`.** The "Add" → save → assign path is proven only from `ResultCapturingView`.
7. **Steps 2, 4, 5, 6, Post-S-2, BR-044 only indirectly asserted.** The main-path test proves "108 → 109 and contains Zimmermann" only; it should assert the empty search grid, the resolved category (35/"L"), `dnf = false`, the dialog closed, and `countAthletesBySeriesId(3) == 109`.

#### Drift

1. `SearchAthleteDialog.java:195-197` — a purely numeric filter searches by athlete id; not described by UC-072 Step 2 (UC-085 relies on it).
2. `UC072…Test.java:60-62` tests the dialog maximize/restore toggle; the spec does not mention it.
3. Spec A3 names `fetchOneInto`; code uses `fetchOptionalInto` (`CategoryDAO.java:38`). Same behaviour — wording drift in the specification.
4. `UC072…Test.java:81-96` removes the athlete as inline cleanup; a failure in the assign path leaves seeded state mutated (`UC085…Test` uses `@AfterEach`).

#### Suggested status

`Implemented` remains accurate. `Tested` is not yet justified — A1, A2, A3, BR-045 and Post-F-1 have no test against `SeriesView`.

---

### 3.3 UC-080 Enter result

Implementation 20/20 (1 n/a) · Tests 11/21 · Spec: [`use_cases/uc-080-enter-result.md`](use_cases/uc-080-enter-result.md)

`View` = `src/main/java/ch/jtaf/ui/ResultCapturingView.java`, `Test` = `src/test/java/ch/jtaf/ui/usecase/result/UC080EnterResultTest.java`, `Seed` = `src/test/resources/db/migration/V9999__Data.sql`.

| Unit | Description | Implementation | Test | Verdict |
|---|---|---|---|---|
| Pre-1 | User signed in as `USER` or `ADMIN` | View:53 `@RolesAllowed` | Test:28 login ADMIN | Covered |
| Pre-2 | Reached `/resultcapturing/<id>` via dashboard "Enter Results" | DashboardView.java:179-184 | Test:34 click `enter-results-1-1` | Covered |
| Pre-3 | At least one athlete enrolled in a contested category | n/a (data precondition) | Seed:508, Seed:2106 | Covered |
| Step 1 | User opens the result-entry view | View:54-55 `@Route`, View:376-379 `setParameter` | Test:34 | Covered |
| Step 2 | Initially empty grid + filter; rows appear once filter set | View:363 (`1 = 2`), View:157-163 | Test:36 sets filter only | Partial (empty grid never asserted) |
| Step 3 | User selects athlete, or auto-select of the single filtered row | View:126-129, View:227-232 | Test:36 (auto-select only) | Partial (manual selection never exercised) |
| Step 4 | Events of the category in `position` order, result/points pair each | View:254 + EventDAO.java:57-63; View:258-272 | Test:38-39, :48-55, :64-74 | Covered |
| Step 5 | Pre-populate existing `RESULT` rows | View:274-282 | Test:38-39 against Seed:2106 | Covered |
| Step 6 | User types a value in a result field | View:293 | Test:48 | Covered |
| Step 7 | `calculatePoints` (UC-084) and render points read-only | View:298, :307-308, :270 | Test:49, :52, :55, :65-74 | Covered |
| Step 8 | Persist or update the `RESULT` row immediately | View:310 `resultDAO.save` | Test:48 fires the listener; nothing reads the row back | Partial (persistence never asserted) |
| Step 9 | Steps 6–8 repeat per event | View:258-313 | Test:48-55, :64-74 | Covered |
| A1 | No athlete selected: form area stays empty | View:248-250 | — | Test missing |
| A2 | Filter resolves to zero athletes: form cleared | View:235-241 → :248 | — | Test missing |
| A3 | Toggle "DNF" → UC-081 | View:315-328 | UC081MarkAthleteDnfTest.java:35-43 | Covered |
| A4 | "Remove results" → UC-082 | View:332-345 | UC082RemoveAthleteResultsTest.java:38-46 | Covered |
| Post-S-1 | `RESULT` row with value and points | View:284-290, :305-310 | Test:49 asserts the UI points field only | Partial (no test reads the row) |
| Post-F-1 | Invalid input: "Invalid result" notification, nothing saved | View:300-303; messages_en.properties:84 | — | Test missing |
| BR-047 | Auto-save, no separate save button | View:293-311; no save button | Test:48 | Partial (persistence never asserted) |
| BR-048 | New result inherits `position` from the iteration index | View:257, :285, :312 | — | Test missing |
| BR-049 | Points read-only, never user-editable | View:270-271 | — | Test missing |

#### Gaps

1. **Post-F-1 has no test.** No UI test enters an invalid value (e.g. `12.2.2`), asserts the "Invalid result" notification, and confirms nothing was saved. `ResultCalculatorTest.java:53` only asserts the exception at calculator level.
2. **A1 has no test.** Nothing asserts the form `Div` is empty before any athlete is selected.
3. **A2 has no test.** No test sets a filter yielding zero rows and asserts a previously built form is cleared.
4. **BR-048 has no test.** Nothing asserts a newly inserted row carries `position` equal to its index in the category's event order.
5. **BR-049 has no test.** No assertion that `points-N` is read-only.
6. **Step 8 / BR-047 / Post-S-1 only partially tested.** No test reloads the view or queries `RESULT` to assert the row exists with the captured value and points.
7. **Steps 2/3 only partially tested.** "Initially empty grid" never asserted; manual grid-row selection never exercised.
8. **Traceability.** `ResultCapturingView.java` has no `UC-080` reference.

#### Drift

1. `View:229-231` focuses the first result field on auto-select — not described by Step 3.

Note: `resultDAO.save` is inherited from the external `JooqDAO`; insert-or-update semantics were not inspected.

#### Suggested status

`Implemented` remains correct. `Tested` becomes justified once gaps 1–6 are closed and the suite is confirmed passing.

---

### 3.4 UC-083 Filter athletes for result entry

Implementation 11/11 · Tests 4/11 · Spec: [`use_cases/uc-083-filter-athletes-for-result-entry.md`](use_cases/uc-083-filter-athletes-for-result-entry.md)

`RCV` = `ResultCapturingView.java`; `UC083`/`UC080`/`UC085`/`UC086` = test classes under `src/test/java/ch/jtaf/ui/usecase/result/`. `UC083FilterAthletesForResultEntryTest` itself covers Step 4 only; the rest comes from UC080/UC086 using the filter incidentally.

| Unit | Description | Implementation | Test | Verdict |
|---|---|---|---|---|
| Pre-1 | User is in the result-entry view (UC-080) | RCV:54-55, :376-379 | UC083:24-30 | Covered |
| Step 1 | User focuses the filter field (system: autofocus) | RCV:160 | — | Test missing |
| Step 2 | User types athlete number or name | RCV:82, :158 | UC083:32 | Covered |
| Step 3 | Data provider recomputed on every keystroke | RCV:161-162 (`ValueChangeMode.EAGER`) | UC083:32 | Partial (EAGER never asserted) |
| Step 4 | Numeric input filters by `ATHLETE.ID` | RCV:354-355, AthleteDAO.java:94 | UC083:32-35 | Covered |
| Step 5 | Otherwise case-insensitive prefix on last OR first name | RCV:358-359 | UC080:36, UC086:63 | Partial (first-name branch and case-insensitivity untested) |
| Step 6 | Exactly one match: auto-select and focus first result field | RCV:227-231 | UC083:34-35, UC086:65-66 | Partial (focus never asserted) |
| A1 | Empty filter: `1 = 2` before input; clearing to `""` lists all, form not cleared | RCV:362-364; :358-359; :126-129, :236-240 | — | Test missing |
| Post-S-1 | Grid shows only matching athletes | RCV:224-233 | UC086:65, :70 | Covered |
| Post-F-1 | _None — read-only_ | — | — | n/a |
| BR-052 | Numeric input always queries by ID, never by name | RCV:354-355 | UC083:32-35 | Partial (rejected case untested) |
| BR-053 | Single match opens the form and focuses the first result field | RCV:227-231 | UC083:34-35, UC086:66-67 | Partial (multi-match "no auto-select" and focus untested) |

#### Gaps

1. **Step 1 has no test.** No assertion of `filter.isAutofocus()`.
2. **A1 has no test.** No test clears the filter back to `""` in the result view and asserts all athletes are listed and the form is retained.
3. **Step 5 only partly tested.** No first-name prefix or lower-case value.
4. **Step 3 only partly tested.** A one-line `getValueChangeMode() == EAGER` assertion closes it.
5. **Step 6 / BR-053 only partly tested.** Focus never asserted; no multi-match "no auto-select" test. If focus cannot be asserted browserless, cover with Playwright.
6. **BR-052 rejected case untested.** A non-existent number should yield zero rows rather than falling back to name search.
7. **Traceability.** `ResultCapturingView.java` carries no `UC-083` reference.

#### Drift

1. `RCV:159` sets `filter.setAutoselect(true)` (select-all-on-focus) — not in the spec.

Outside the coverage units: `Long.valueOf` at `RCV:355` throws on a digit string longer than a `long`; `%`/`_` in the name filter reach `LIKE` unescaped (`RCV:358-359`). Neither contradicts the spec as written.

#### Suggested status

`Implemented` remains correct. `Tested` is not justified: A1 and Step 1 have no test, five further units are partial, mostly outside the UC-083 test class.

---

### 3.5 UC-091 Download series ranking

Implementation 13/13 · Tests 7/13 · Spec: [`use_cases/uc-091-download-series-ranking.md`](use_cases/uc-091-download-series-ranking.md)

| Unit | Description | Implementation | Test | Verdict |
|---|---|---|---|---|
| Pre-1 | Series exists and is not hidden | SeriesDAO.java:93 | V9999__Data.sql:2288, :2290 | Covered |
| Pre-2 | Series contains competitions and results | SeriesRankingService.java:74-77 | V9999__Data.sql:783-787 + result rows | Covered |
| Step 1 | Visitor clicks "Series Ranking" on the dashboard | DashboardView.java:75 inside anchor :67 | UC091DownloadSeriesRankingTest.java:32 | Covered |
| Step 2 | Anchor download targeted to a new tab | DashboardView.java:67-73 (`_blank`) | UC091DownloadSeriesRankingTest.java:32 | Partial (`_blank` never asserted) |
| Step 3 | `getSeriesRankingAsPdf` multiset query summing points | SeriesRankingService.java:35-78 | SeriesRankingServiceTest.java:42-47 | Covered (presence and name only, not aggregation) |
| Step 4 | Rendered with `SeriesRankingReport` in the visitor's locale | DashboardView.java:70; SeriesRankingService.java:32; AbstractReport.java:38 | SeriesRankingServiceTest.java:50-54 | Covered |
| Step 5 | Browser downloads `series_ranking<id>.pdf` | DashboardView.java:68-69 | UC091DownloadSeriesRankingTest.java:31-32 | Partial (only `assertThatNoException`; bytes discarded) |
| A1.1 | No competitions: empty, report throws, browser error | SeriesRankingService.java:32; DashboardView.java:70 | — | Test missing |
| A1.2 | Competitions but no results: PDF with empty tables | SeriesRankingService.java:74-77; SeriesRankingData.java:16; SeriesRankingReport.java:54-74 | — | Test missing |
| Post-S-1 | A PDF is delivered | DashboardView.java:68-70 | SeriesRankingServiceTest.java:53 | Covered (service level only) |
| Post-F-1 | Unknown series → error; PDF-generation errors → 0-byte download | SeriesRankingService.java:32; SeriesRankingReport.java:47-50 | — | Test missing |
| BR-059 | Only `DNF = false` athletes with a result in every competition | SeriesRankingService.java:66; SeriesRankingData.java:14-19; SeriesRankingReport.java:63 | — | Test missing |
| BR-060 | Downloadable without authentication | DashboardView.java:28 `@AnonymousAllowed` | DashboardViewIT.java:14-20 (page load only); UC091 test logs in as ADMIN | Partial (no anonymous download) |

#### Gaps

1. **BR-059 has no test, in either half.** The seed never sets `dnf = true` and every competition has results; `SeriesRankingData.getFilteredAndSortedAthletes` is referenced by no test.
2. **A1.1 has no test.** No seeded series lacks competitions.
3. **A1.2 has no test.** Needs a series with competitions and no `result` rows and an assertion that a non-empty PDF is still returned.
4. **Post-F-1 has no test.** Neither the unknown-series path nor the 0-byte swallow path is exercised.
5. **Step 5 / Post-S-1 weakly asserted at view level.** Because `SeriesRankingReport.java:49` returns an empty array on failure, the view test passes on a broken PDF; it should assert non-empty bytes and the `series_ranking1.pdf` name.
6. **Step 2 target and BR-060 not asserted.** An anonymous variant of the download test (skip `login`, call `setupVaadin()`) would close BR-060.
7. **Traceability (minor).** `SeriesRankingServiceTest.java` carries no UC-091 marker.

#### Drift

None found.

#### Suggested status

`Implemented` remains correct. `Tested` is not yet justified — the alternative flow, both business rules, and the failure postcondition have no tests, and the view-level test cannot detect an empty PDF.

---

## 4. Recommended actions

### 4.1 Close the audited gaps

All gaps are test-side; the implementation needs no change. Matching the Browserless stack already in use:

| Use case | Command | Covers |
|---|---|---|
| UC-003 | `/browserless-test UC-003` | A1 (`?error` state), Step 2, Step 6, Post-F-1, BR-006 rejected case |
| UC-003 | `/playwright-test TC-003` | Steps 3–5 with real credentials, JWT cookie, redirect, A2 at the overlay |
| UC-072 | `/browserless-test UC-072` | A1, A2, A3, A4 from `SeriesView`, BR-045, Post-F-1, direct assertions for Steps 2–6 |
| UC-080 | `/browserless-test UC-080` | A1, A2, Post-F-1, BR-048, BR-049, persistence read-back |
| UC-083 | `/browserless-test UC-083` | Step 1 autofocus, Step 3 EAGER, Step 5 first-name/case, A1, BR-052 rejected case |
| UC-083 | `/playwright-test` | focus of the first result field (if not assertable browserless) |
| UC-091 | `/browserless-test UC-091` | A1.1, A1.2, Post-F-1, BR-059, non-empty PDF assertion |
| UC-091 | `/playwright-test` | anonymous download (BR-060), `_blank` target |

### 4.2 Project-wide

1. **Add traceability markers to production code** — a `UC-XXX` reference in the Javadoc of each view, dialog, and DAO method that realizes a use case. Cheap, and it turns every future audit from a vocabulary hunt into a grep.
2. **Adopt a per-test scenario marker** (e.g. `@UseCase(id, scenario, businessRules)` or a Javadoc convention) so that alternative flows and business rules are claimed explicitly by the test that covers them.
3. **Add one alternative-flow and one failure-postcondition test per use case** as the minimum bar before any spec moves to `Tested`.
4. **Add at least one real-credentials authentication test** (MockMvc or Playwright) so that the Spring Security chain, BCrypt, and JWT cookie are exercised somewhere.
5. **Extend the seed data** with a DNF athlete, a series without competitions, and a competition without results — the three missing fixtures behind most of the untested report and ranking rules.
6. **Fix the inverted assertion** in `UC003SignInTest.login_with_unknown_user` before adding tests around it; as written it passes for the wrong reason.

### 4.3 Remaining audits

40 use cases were triaged but not audited: UC-001, 002, 004, 005, 010–013, 020–025, 030–032, 040–042, 050, 060, 070, 071, 073, 074, 081, 082, 084–086, 090, 092–099. Given the uniform test shape, expect the same pattern (complete implementation, happy-path-only tests). Suggested priority for the next batches:

1. **UC-092…095** (report downloads) — same 35-line test shape as UC-091; likely identical gaps.
2. **UC-074, UC-084** (Excel import, IAAF points) — the two most rule-heavy specs; their tests are larger but the rules deserve per-rule verification.
3. **UC-001, UC-002** (registration, e-mail confirmation) — security-relevant, several alternative flows.
4. **UC-012, UC-024, UC-032** (deletes) — cascading and guard behaviour typically has untested rejected cases.

### 4.4 Status lines

No `**Status:**` line should change as a result of this analysis. `Implemented` is accurate for all five audited use cases. A move to `Tested` requires the gaps above to be closed **and** a passing run of the test suite, which this analysis did not perform.
