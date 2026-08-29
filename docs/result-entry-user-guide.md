# JTAF Result Entry User Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [Accessing Result Entry](#accessing-result-entry)
4. [Understanding the Interface](#understanding-the-interface)
5. [Entering Results](#entering-results)
6. [Managing Athletes](#managing-athletes)
7. [Result Formats and Validation](#result-formats-and-validation)
8. [DNF (Did Not Finish) Management](#dnf-did-not-finish-management)
9. [Bulk Operations](#bulk-operations)
10. [Real-time Point Calculation](#real-time-point-calculation)
11. [Best Practices](#best-practices)
12. [Troubleshooting](#troubleshooting)
13. [Keyboard Shortcuts](#keyboard-shortcuts)

## Introduction

### What is Result Entry?

The JTAF Result Entry system allows competition officials to quickly and accurately record athlete performances during track and field competitions. The system automatically calculates points using official IAAF formulas and provides real-time feedback on athlete rankings.

### Key Features
- **Real-time point calculation**: Points are calculated instantly as you enter results
- **Athlete filtering**: Quickly find athletes by ID, name, or category
- **Dynamic forms**: Entry forms adapt to each category's assigned events
- **DNF tracking**: Mark athletes who did not finish specific events
- **Bulk operations**: Remove all results for an athlete with one click
- **Auto-save**: Results are saved automatically as you type

### Who Should Use This Guide?

This guide is designed for:
- Competition officials entering results during events
- Volunteers helping with result recording
- Event organizers managing result entry teams
- Anyone responsible for recording athlete performances

## Getting Started

### Prerequisites

Before you can enter results, ensure:
1. **User Account**: You have a registered JTAF account with appropriate permissions
2. **Organization Access**: You belong to the organization running the competition
3. **Competition Setup**: The competition, categories, and events are properly configured
4. **Athlete Registration**: Athletes are registered and assigned to categories

### Required Permissions

To enter results, you need:
- **USER** or **ADMIN** role in the system
- **Organization membership** for the competition's organization
- **Competition access** (competitions must not be locked)

### System Requirements

- **Modern web browser**: Chrome, Firefox, Safari, or Edge
- **Stable internet connection**: For real-time saving and point calculation
- **Keyboard access**: For efficient data entry

## Accessing Result Entry

### Navigation Path

1. **Login to JTAF**
   - Enter your email and password
   - Select the appropriate organization if you belong to multiple

2. **Navigate to Series**
   - From the dashboard, click on the relevant series
   - Or use the "Series" menu item

3. **Select Competition**
   - In the series view, go to the "Competitions" tab
   - Find the competition you want to enter results for
   - Click the "Enter Results" button

### Competition Selection

The "Enter Results" button is only available for:
- **Unlocked competitions**: Locked competitions prevent result modifications
- **Current competitions**: Based on competition date and status
- **Authorized users**: Users with appropriate permissions

### URL Structure

Result entry URLs follow this pattern:
```
https://your-jtaf-instance.com/resultcapturing/[competition-id]
```

You can bookmark specific competition result entry pages for quick access.

## Understanding the Interface

### Main Components

The result entry interface consists of three main sections:

#### 1. Athlete Filter (Top)
- **Search field**: Filter athletes by ID, first name, or last name
- **Auto-focus**: Cursor automatically starts in this field
- **Auto-select**: Text is selected for quick replacement

#### 2. Athlete Grid (Middle)
- **ID Column**: Athlete identification number
- **Last Name**: Athlete's surname
- **First Name**: Athlete's given name
- **Category**: Category abbreviation the athlete is competing in
- **Selection**: Click any row to load that athlete's result form

#### 3. Result Form (Bottom)
- **Event Fields**: One field per event assigned to the athlete's category
- **Points Display**: Calculated points shown next to each result field
- **DNF Checkbox**: Mark athlete as "Did Not Finish" for the category
- **Remove Results Button**: Clear all results for the selected athlete

### Interface Behavior

#### Auto-Selection
- When your filter returns exactly one athlete, that athlete is automatically selected
- The first result field automatically receives focus for immediate data entry
- This speeds up entry when you know athlete IDs or unique name portions

#### Real-Time Updates
- Points are calculated immediately when you enter a result
- Results are saved automatically when you move to the next field
- No manual save button is required

#### Visual Feedback
- **Points fields**: Read-only, showing calculated values
- **Result fields**: Editable, with format validation
- **Grid highlighting**: Selected athlete is highlighted in the grid

## Entering Results

### Basic Result Entry Process

1. **Filter for Athlete**
   ```
   Type in filter field:
   - Athlete ID (e.g., "140")
   - Last name (e.g., "Smith")
   - First name (e.g., "John")
   - Partial name (e.g., "Smi")
   ```

2. **Select Athlete**
   - Click on the athlete row in the grid
   - Or use auto-selection by filtering to exactly one athlete

3. **Enter Results**
   - Type result in the appropriate event field
   - Press Tab or Enter to move to next field
   - Points are calculated and displayed automatically

4. **Continue to Next Athlete**
   - Clear the filter field
   - Type new search criteria
   - Repeat the process

### Event-Specific Entry

#### Sprint Events (RUN)
**Examples**: 60m, 100m, 200m, 400m

**Format**: `seconds.centiseconds`
```
Examples:
- 12.34 (12.34 seconds)
- 9.87 (9.87 seconds)
- 23.45 (23.45 seconds)
```

**Entry Tips**:
- Always use decimal point (not comma)
- Include centiseconds for accuracy
- Typical range: 8.00-20.00 for youth competitions

#### Distance Events (RUN_LONG)
**Examples**: 800m, 1500m, 3000m

**Format**: `minutes:seconds.centiseconds`
```
Examples:
- 2:15.67 (2 minutes, 15.67 seconds)
- 4:32.10 (4 minutes, 32.10 seconds)
- 12:45.00 (12 minutes, 45.00 seconds)
```

**Entry Tips**:
- Use colon (:) to separate minutes and seconds
- Use decimal point for centiseconds
- For times under 1 minute, still include minutes: 0:45.67

#### Field Events (JUMP_THROW)
**Examples**: Long jump, high jump, shot put, discus, javelin

**Format**: `distance in centimeters`
```
Examples:
- 450 (4.50 meters)
- 520 (5.20 meters)
- 1250 (12.50 meters)
```

**Entry Tips**:
- Always enter in centimeters, not meters
- No decimal points needed
- Convert measurements: 4.50m = 450cm

### Measurement Conversion

#### Length Conversions
```
Meters to Centimeters:
- 4.50m = 450cm
- 5.20m = 520cm
- 12.50m = 1250cm

Feet/Inches to Centimeters:
- 15'6" = 472cm
- 17'2" = 523cm
```

#### Time Conversions
```
Minutes:Seconds to Total Seconds:
- 2:15.67 = 135.67 seconds
- 4:32.10 = 272.10 seconds

For RUN_LONG events, always use MM:SS.CC format
```

## Managing Athletes

### Athlete Search Strategies

#### Search by ID
- **Most Efficient**: If athletes have numbered bibs
- **Example**: Type "140" to find athlete #140
- **Auto-selection**: Immediately loads athlete if ID is unique

#### Search by Last Name
- **Common Method**: Type surname or partial surname
- **Example**: "Smith" finds all athletes with surname starting with "Smith"
- **Case Insensitive**: "smith", "SMITH", "Smith" all work the same

#### Search by First Name
- **Alternative Method**: When multiple athletes share surnames
- **Example**: "John" finds all athletes with first name starting with "John"
- **Combination**: Can help narrow down results

#### Partial Name Search
- **Flexible Approach**: Type any portion of first or last name
- **Example**: "Mar" might find "Martinez", "Marshall", "Mary"
- **Progressive Filtering**: Add more letters to narrow results

### Multi-Category Athletes

Some athletes may compete in multiple categories within the same competition:

#### Category Selection
- **Grid Display**: Shows category abbreviation for each athlete entry
- **Separate Entries**: Same athlete appears multiple times (once per category)
- **Independent Results**: Results for each category are tracked separately

#### Managing Multiple Entries
1. **Filter for athlete name**: Shows all category entries for that athlete
2. **Select specific category**: Click the row for the desired category
3. **Enter results**: Results apply only to the selected category
4. **Repeat for other categories**: Select different category row for same athlete

### Athlete Information Display

The athlete grid shows:
- **ID**: Unique athlete identifier (often bib number)
- **Last Name**: Surname for identification
- **First Name**: Given name for identification  
- **Category**: Category abbreviation (e.g., "M12", "F14")

This information helps ensure you're entering results for the correct athlete in the correct category.

### Correcting Athlete Details

If the name on the result sheet does not match the grid (e.g. a typo from registration), you can fix it without leaving result entry:

1. **Select the athlete**: Filter by ID or name so the athlete is selected
2. **Click "Edit Athlete"**: The button next to the filter is enabled as soon as an athlete is selected
3. **Correct the name**: Change last name and/or first name and click "Save"
4. **Continue entering results**: The grid shows the corrected name and the athlete stays selected

Note: Gender, year of birth and club cannot be changed here because they determine the athlete's category. Use the athlete list of the organization for such changes.

## Result Formats and Validation

### Format Requirements

#### Time Events
The system expects specific time formats based on event type:

**RUN Events (Sprints)**:
- **Required Format**: `SS.CC` (seconds.centiseconds)
- **Valid Examples**: 
  - `12.34` ✓
  - `9.87` ✓
  - `23.45` ✓
- **Invalid Examples**:
  - `12,34` ✗ (comma instead of period)
  - `12:34` ✗ (colon instead of period)
  - `12.3` ✗ (missing centisecond digit)

**RUN_LONG Events (Distance)**:
- **Required Format**: `MM:SS.CC` (minutes:seconds.centiseconds)
- **Valid Examples**:
  - `2:15.67` ✓
  - `4:32.10` ✓
  - `0:45.23` ✓ (for times under 1 minute)
- **Invalid Examples**:
  - `135.67` ✗ (total seconds instead of MM:SS format)
  - `2.15.67` ✗ (periods instead of colon)
  - `2:15` ✗ (missing centiseconds)

#### Distance Events
**JUMP_THROW Events**:
- **Required Format**: Whole number in centimeters
- **Valid Examples**:
  - `450` ✓ (represents 4.50m)
  - `520` ✓ (represents 5.20m)
  - `1250` ✓ (represents 12.50m)
- **Invalid Examples**:
  - `4.50` ✗ (meters instead of centimeters)
  - `450.0` ✗ (decimal not needed)
  - `4m50cm` ✗ (text format not accepted)

### Validation Feedback

#### Real-Time Validation
- **Format Checking**: System validates format as you type
- **Error Indicators**: Invalid formats are highlighted
- **Point Calculation**: Only valid results generate points

#### Common Validation Errors
1. **"Invalid result format"**: Check that format matches event type
2. **"Result out of range"**: Unusually high or low performance values
3. **"Missing required format"**: Empty or incomplete result entries

### Handling Invalid Results

#### Correction Process
1. **Identify Error**: Look for validation messages or missing points
2. **Clear Field**: Select all text and delete
3. **Re-enter Correctly**: Use proper format for event type
4. **Verify Points**: Confirm points appear after correction

#### Prevention Tips
- **Know Event Types**: Understand which events use which formats
- **Use Consistent Format**: Stick to required patterns
- **Double-Check Entries**: Verify format before moving to next field

## DNF (Did Not Finish) Management

### Understanding DNF

**DNF (Did Not Finish)** indicates an athlete who:
- Started an event but couldn't complete it
- Was disqualified during the event
- Chose not to participate in specific events within their category
- Had equipment failure or injury during the event

### DNF vs. No Result

**DNF (Checkbox Checked)**:
- Athlete attempted the event but didn't finish
- Receives 0 points for affected events
- Still eligible for other events in the category
- Appears in results but marked as DNF

**No Result (Empty Fields)**:
- Athlete hasn't attempted the event yet
- No points calculated
- May still compete later
- Doesn't appear in event rankings

### Setting DNF Status

#### Individual Event DNF
Currently, DNF applies to the entire category, not individual events. If an athlete DNFs in one event but completes others:

1. **Enter completed results**: Record results for events the athlete finished
2. **Leave DNF events empty**: Don't enter results for events not completed
3. **Use DNF checkbox only**: If athlete DNFs from entire category

#### Category-Wide DNF
For athletes who cannot continue in any events within a category:

1. **Select athlete**: Find athlete in the grid
2. **Check DNF box**: Located below the result entry fields
3. **Confirm action**: DNF status is saved immediately
4. **Result clearing**: Existing results may be cleared (system dependent)

### DNF Effects on Scoring

#### Point Calculation
- **DNF Events**: Receive 0 points
- **Completed Events**: Normal point calculation applies
- **Category Total**: Sum of completed event points only

#### Ranking Impact
- **Event Rankings**: DNF athletes excluded from individual event rankings
- **Category Rankings**: Athletes with some results still appear in category rankings
- **Medal Eligibility**: Depends on competition rules and remaining points

### Managing DNF Changes

#### Removing DNF Status
1. **Select athlete**: Find athlete in grid
2. **Uncheck DNF box**: Click to remove DNF status
3. **Re-enter results**: Add results for events as needed
4. **Verify points**: Confirm point calculations are correct

#### Bulk DNF Operations
For multiple athletes with DNF status:
- Handle each athlete individually
- Use consistent approach for similar situations
- Document reasons for DNF (external to system)

## Bulk Operations

### Remove Results Function

The "Remove Results" button allows you to clear all results for a selected athlete in the current competition.

#### When to Use Remove Results
- **Incorrect entries**: When multiple results need correction
- **Athlete withdrawal**: When athlete withdraws from competition
- **Data reset**: When starting fresh with an athlete's results
- **Error correction**: When systematic errors affect all events

#### Remove Results Process
1. **Select athlete**: Choose athlete from grid
2. **Click "Remove Results"**: Button appears below result form
3. **Confirm action**: System asks for confirmation
4. **Results cleared**: All results and points are removed
5. **DNF reset**: DNF status is also cleared

#### Confirmation Dialog
The system shows a confirmation dialog:
- **Action**: "Remove Results"
- **Athlete**: Shows which athlete will be affected
- **Confirm**: Click to proceed with removal
- **Cancel**: Click to abort the operation

### Safety Features

#### Confirmation Required
- **No accidental deletion**: All bulk operations require confirmation
- **Clear messaging**: Dialog explains what will be removed
- **Cancel option**: Always available to abort operation

#### Immediate Effect
- **Real-time removal**: Results disappear immediately after confirmation
- **Point recalculation**: Points are cleared instantly
- **Ranking updates**: Rankings update automatically

### Recovery from Bulk Operations

#### No Undo Function
- **Permanent action**: Removed results cannot be automatically restored
- **Manual re-entry**: Results must be entered again manually
- **Backup strategy**: Keep paper records as backup

#### Prevention Strategies
- **Double-check selection**: Verify correct athlete before removal
- **Partial corrections**: Consider editing individual fields instead
- **External backup**: Maintain paper or digital backup of results

## Real-time Point Calculation

### IAAF Scoring System

The JTAF system uses official IAAF (International Association of Athletics Federations) formulas to calculate points for each event, allowing fair comparison across different disciplines.

#### Scoring Formulas

**RUN Events (Sprints)**:
```
Points = A × ((B - time_in_centiseconds) / 100)^C
```
- Lower times produce higher points
- Formula rewards faster performances exponentially

**RUN_LONG Events (Distance)**:
```
1. Convert MM:SS.CC to total centiseconds
2. Apply RUN formula: Points = A × ((B - centiseconds) / 100)^C
```
- Time conversion happens automatically
- Same exponential reward system as sprints

**JUMP_THROW Events (Field)**:
```
Points = A × ((distance_in_centimeters - B) / 100)^C
```
- Greater distances produce higher points
- Formula rewards better performances exponentially

#### Coefficient Values
Each event has three coefficients (A, B, C) that determine the scoring curve:
- **A**: Scaling factor
- **B**: Performance baseline
- **C**: Exponential factor

These values are set by athletics federations and configured in the system by administrators.

### Point Display

#### Real-Time Calculation
- **Immediate feedback**: Points appear as soon as you enter a valid result
- **Format validation**: Points only calculate for properly formatted results
- **Automatic rounding**: Points are rounded to nearest whole number

#### Points Field Characteristics
- **Read-only**: You cannot directly edit point values
- **Auto-updating**: Changes automatically when result is modified
- **Validation indicator**: Empty points field indicates invalid result format

### Understanding Point Values

#### Typical Point Ranges
Point values vary significantly by event and performance level:

**Youth Competitions**:
- **Excellent performance**: 800-1000+ points
- **Good performance**: 400-800 points
- **Average performance**: 200-400 points
- **Beginning performance**: 50-200 points

#### Comparative Analysis
- **Cross-event comparison**: Points allow comparison between different events
- **Balanced scoring**: IAAF formulas ensure no single event dominates
- **Performance tracking**: Points show improvement over time

### Troubleshooting Point Calculation

#### Missing Points
If points don't appear after entering a result:

1. **Check result format**: Ensure format matches event type requirements
2. **Verify event configuration**: Event must have valid IAAF coefficients
3. **Clear and re-enter**: Try clearing the field and entering again
4. **Check for errors**: Look for validation messages

#### Unexpected Point Values
If points seem too high or low:

1. **Verify result entry**: Check that result was entered correctly
2. **Confirm event type**: Ensure event is configured with correct type
3. **Check coefficients**: Verify IAAF coefficients are correct for the event
4. **Compare with standards**: Check against known performance standards

## Best Practices

### Efficient Data Entry

#### Preparation
1. **Organize materials**: Have result sheets, athlete lists, and timing equipment ready
2. **Test system**: Verify internet connection and system access before competition
3. **Backup plan**: Keep paper records as backup during entry
4. **Team coordination**: Assign specific roles if multiple people are entering results

#### Entry Strategy
1. **Systematic approach**: Enter results in consistent order (by event or by athlete)
2. **Use athlete IDs**: Fastest method when athletes have numbered bibs
3. **Batch similar events**: Complete all results for one event before moving to next
4. **Regular verification**: Periodically check entered results for accuracy

#### Speed Techniques
1. **Learn keyboard shortcuts**: Use Tab and Enter for navigation
2. **Master search patterns**: Develop efficient athlete filtering techniques
3. **Use auto-selection**: Filter to exactly one athlete for automatic selection
4. **Minimize mouse use**: Keyboard navigation is faster than clicking

### Quality Control

#### Accuracy Checks
1. **Double-entry verification**: Have second person verify critical results
2. **Range validation**: Question unusually high or low performances
3. **Format consistency**: Ensure all results use correct format for event type
4. **Point validation**: Verify point calculations make sense

#### Error Prevention
1. **Clear communication**: Ensure result readers speak clearly and distinctly
2. **Confirmation protocol**: Repeat back results before entering
3. **Standard procedures**: Use consistent methods for all entry personnel
4. **Regular breaks**: Prevent fatigue-related errors with scheduled breaks

### Competition Day Workflow

#### Pre-Competition Setup
1. **System access**: Verify all entry personnel can log in
2. **Competition selection**: Navigate to correct competition for result entry
3. **Test entries**: Enter a few test results to verify system functionality
4. **Backup preparation**: Prepare paper forms as backup

#### During Competition
1. **Real-time entry**: Enter results as events complete when possible
2. **Batch processing**: For large events, consider batch entry between events
3. **Progress tracking**: Monitor which events/athletes still need results
4. **Communication**: Coordinate with timing officials and event judges

#### Post-Competition
1. **Completion verification**: Ensure all results are entered
2. **Final review**: Check for missing or obviously incorrect results
3. **Report generation**: Generate preliminary results for announcements
4. **Data backup**: Save or export results for external backup

### Multi-User Coordination

#### Role Assignment
1. **Lead coordinator**: Oversees entire result entry process
2. **Event specialists**: Assign specific events to specific people
3. **Quality checker**: Dedicated person for verification and error correction
4. **Technical support**: Person familiar with system troubleshooting

#### Communication Protocols
1. **Status updates**: Regular communication about entry progress
2. **Error reporting**: Clear process for reporting and resolving issues
3. **Completion confirmation**: Verify when each event's results are complete
4. **Final verification**: Coordinated final check before results publication

## Troubleshooting

### Common Issues and Solutions

#### Login and Access Problems

**Problem**: Cannot access result entry for competition
**Possible Causes**:
- User not logged in
- Insufficient permissions
- Competition is locked
- Wrong organization selected

**Solutions**:
1. **Verify login**: Ensure you're logged in with correct credentials
2. **Check organization**: Confirm you've selected the right organization
3. **Verify permissions**: Contact administrator to confirm USER/ADMIN role
4. **Competition status**: Check if competition is locked (contact organizer)

#### Athlete Search Issues

**Problem**: Cannot find athlete in search
**Possible Causes**:
- Athlete not registered for competition
- Athlete not assigned to any categories
- Spelling errors in search
- Case sensitivity issues

**Solutions**:
1. **Check registration**: Verify athlete is registered for this competition
2. **Category assignment**: Confirm athlete is assigned to at least one category
3. **Try different search**: Use athlete ID, different name spelling, or partial names
4. **Contact organizer**: Verify athlete should be in this competition

#### Result Entry Problems

**Problem**: Cannot enter results for athlete
**Possible Causes**:
- Invalid result format
- Competition locked
- Network connectivity issues
- Browser compatibility problems

**Solutions**:
1. **Check format**: Ensure result format matches event type requirements
2. **Verify competition status**: Confirm competition is not locked
3. **Test connection**: Try refreshing page or checking internet connection
4. **Try different browser**: Switch to Chrome, Firefox, or Edge

**Problem**: Points not calculating
**Possible Causes**:
- Invalid result format
- Missing IAAF coefficients
- Event configuration errors
- System calculation errors

**Solutions**:
1. **Verify format**: Double-check result format for event type
2. **Clear and re-enter**: Delete result and enter again
3. **Contact administrator**: Report missing or incorrect IAAF coefficients
4. **Try different event**: Test if problem affects all events or just one

#### Performance Issues

**Problem**: System running slowly
**Possible Causes**:
- Network congestion
- Large competition size
- Multiple simultaneous users
- Browser performance issues

**Solutions**:
1. **Check connection**: Verify internet speed and stability
2. **Close other applications**: Free up browser resources
3. **Coordinate timing**: Stagger entry times with other users
4. **Use wired connection**: Switch from WiFi to ethernet if possible

**Problem**: Results not saving
**Possible Causes**:
- Network interruption
- Session timeout
- Browser issues
- System overload

**Solutions**:
1. **Refresh page**: Reload and check if results were saved
2. **Re-login**: Log out and back in to refresh session
3. **Re-enter results**: Enter results again if not saved
4. **Contact support**: Report persistent saving issues

### Error Messages

#### Common Error Messages and Meanings

**"Invalid result format"**
- **Meaning**: Result doesn't match expected format for event type
- **Solution**: Check event type and use correct format (time vs distance)

**"DNF could not be set"**
- **Meaning**: System error preventing DNF status change
- **Solution**: Refresh page and try again, or contact administrator

**"Athlete not found"**
- **Meaning**: Search didn't return any matching athletes
- **Solution**: Check spelling, try different search terms, or verify athlete registration

**"Competition is locked"**
- **Meaning**: Results cannot be modified for this competition
- **Solution**: Contact competition organizer to unlock if changes are needed

#### Browser-Specific Issues

**Chrome/Edge**:
- Generally most compatible
- Clear cache if experiencing issues
- Disable extensions if problems persist

**Firefox**:
- Usually works well
- Check privacy settings if login issues occur
- Ensure JavaScript is enabled

**Safari**:
- May have compatibility issues
- Try Chrome or Firefox if problems persist
- Check security settings

### Getting Help

#### Self-Service Steps
1. **Check this guide**: Review relevant sections for your issue
2. **Try basic troubleshooting**: Refresh page, clear cache, try different browser
3. **Test with different data**: Try entering results for different athlete/event
4. **Document the issue**: Note exact error messages and steps to reproduce

#### Contacting Support
When contacting technical support, include:

**System Information**:
- Browser type and version
- Operating system
- Internet connection type

**Problem Details**:
- Exact error messages
- Steps to reproduce the issue
- Which competition/athlete/event affected
- Screenshots if helpful

**User Information**:
- Your email address
- Organization name
- Competition name and date

#### Escalation Process
1. **Competition organizer**: First contact for competition-specific issues
2. **Organization administrator**: For user permission and access issues
3. **Technical support**: For system bugs and technical problems
4. **JTAF support**: For critical system-wide issues

## Keyboard Shortcuts

### Navigation Shortcuts

#### Basic Navigation
- **Tab**: Move to next field
- **Shift + Tab**: Move to previous field
- **Enter**: Move to next field (same as Tab in most cases)
- **Escape**: Cancel current operation or close dialog

#### Search and Selection
- **Ctrl + F**: Focus on filter field (browser shortcut)
- **F3**: Find next (browser shortcut)
- **Ctrl + A**: Select all text in current field

### Data Entry Shortcuts

#### Text Entry
- **Ctrl + A**: Select all text in field
- **Ctrl + C**: Copy selected text
- **Ctrl + V**: Paste text
- **Ctrl + Z**: Undo last change (browser dependent)

#### Form Navigation
- **Tab**: Next field
- **Shift + Tab**: Previous field
- **Enter**: Confirm entry and move to next field
- **Escape**: Cancel current entry

### Browser Shortcuts

#### Page Management
- **F5** or **Ctrl + R**: Refresh page
- **Ctrl + Shift + R**: Hard refresh (clear cache)
- **Ctrl + T**: New tab
- **Ctrl + W**: Close current tab

#### Zoom and Display
- **Ctrl + Plus (+)**: Zoom in
- **Ctrl + Minus (-)**: Zoom out
- **Ctrl + 0**: Reset zoom to 100%
- **F11**: Full screen mode

### Efficiency Tips

#### Speed Entry Techniques
1. **Use Tab navigation**: Faster than clicking between fields
2. **Learn number pad**: Efficient for numeric entry
3. **Use auto-complete**: Browser may remember common entries
4. **Minimize mouse use**: Keyboard navigation is typically faster

#### Workflow Optimization
1. **Filter + Tab**: Type athlete search, then Tab to first result field
2. **Enter + Clear**: Enter result, then clear filter for next athlete
3. **Consistent patterns**: Develop muscle memory for common sequences
4. **Batch similar data**: Enter all results for one event before switching

---

## Quick Reference

### Result Format Summary
| Event Type | Format | Example |
|------------|--------|---------|
| RUN (Sprint) | SS.CC | 12.34 |
| RUN_LONG (Distance) | MM:SS.CC | 2:15.67 |
| JUMP_THROW (Field) | Centimeters | 450 |

### Common Conversions
| Measurement | Centimeters |
|-------------|-------------|
| 4.50m | 450cm |
| 5.20m | 520cm |
| 12.50m | 1250cm |

### Essential Shortcuts
| Action | Shortcut |
|--------|----------|
| Next field | Tab |
| Previous field | Shift + Tab |
| Refresh page | F5 |
| Select all text | Ctrl + A |

This guide provides comprehensive information for efficient and accurate result entry in the JTAF system. For additional support or questions not covered here, contact your competition organizer or system administrator.