# JTAF Competition Organizer Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [Organization Setup](#organization-setup)
3. [Series Management](#series-management)
4. [Competition Structure](#competition-structure)
5. [Event and Category Configuration](#event-and-category-configuration)
6. [Athlete Registration](#athlete-registration)
7. [Result Entry and Management](#result-entry-and-management)
8. [Report Generation](#report-generation)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

## Getting Started

### What is JTAF?

JTAF is a comprehensive web-based system for managing track and field competitions, specifically designed for youth athletics organizations. The system handles everything from athlete registration to result processing and report generation, using official IAAF scoring formulas for fair cross-discipline comparison.

### Key Features
- **Multi-tenant architecture**: Multiple organizations can use the system independently
- **Complete competition lifecycle**: From setup to final reports
- **IAAF-compliant scoring**: Automatic points calculation using official formulas
- **Professional reports**: PDF generation for rankings, diplomas, and administrative documents
- **Multi-language support**: German, English, and French interfaces

### System Requirements
- Modern web browser (Chrome, Firefox, Safari, Edge)
- Internet connection for cloud-based access
- Email account for registration and notifications

## Organization Setup

### Initial Registration

1. **Create Account**
   - Navigate to the JTAF registration page
   - Enter your email address and create a secure password
   - Check your email for confirmation link
   - Click the confirmation link to activate your account

2. **Organization Creation**
   - After login, navigate to "Organizations" in the main menu
   - Click "Add Organization" button
   - Fill in organization details:
     - **Name**: Full organization name (e.g., "Turnverein Erlach")
     - **Key**: Unique identifier (e.g., "tv-erlach")
     - **Description**: Optional description of your organization
   - Save the organization

3. **User Management**
   - Invite additional users to your organization
   - Assign appropriate roles (USER or ADMIN)
   - ADMIN users can manage organization settings and users
   - USER role provides access to competition management features

### Organization Settings

- **Logo Upload**: Upload your organization logo for branded reports
- **Contact Information**: Maintain current contact details
- **User Permissions**: Manage who has access to your organization's data

## Series Management

### Understanding Series

A **Series** is a collection of related competitions, typically representing an annual championship or seasonal competition cycle. For example, "2024 Youth Championships" might contain multiple competitions throughout the year.

### Creating a New Series

1. **Navigate to Series Management**
   - From the dashboard, click "Series" or use the main navigation
   - Click "Add Series" button

2. **Series Configuration**
   - **Name**: Descriptive name (e.g., "2024 Inter-Section Championships")
   - **Description**: Optional detailed description
   - **Logo**: Upload series-specific logo (optional, falls back to organization logo)
   - **Visibility Settings**:
     - **Hidden**: Check to hide from public dashboard
     - **Locked**: Check to prevent modifications (use after series completion)

3. **Series Logo Guidelines**
   - Recommended format: PNG or JPG
   - Optimal size: 200x200 pixels or larger
   - Will be automatically resized for reports
   - Used in all generated PDF reports

### Series Lifecycle

1. **Planning Phase**: Create series, define categories and events
2. **Setup Phase**: Register athletes, create competitions
3. **Active Phase**: Conduct competitions, enter results
4. **Completion Phase**: Generate final reports, lock series

## Competition Structure

### Competition Hierarchy

```
Organization
└── Series (Annual Championship)
    ├── Competition 1 (Event Date 1)
    ├── Competition 2 (Event Date 2)
    └── Competition 3 (Event Date 3)
        └── Categories (Age/Gender Groups)
            ├── Category A (e.g., Boys 2010-2011)
            └── Category B (e.g., Girls 2010-2011)
                └── Events (Track & Field Disciplines)
                    ├── 60m Sprint
                    ├── Long Jump
                    └── Shot Put (up to 10 events per category)
```

### Creating Competitions

1. **Access Series Management**
   - Select your series from the series list
   - Navigate to the "Competitions" tab

2. **Add New Competition**
   - Click "Add Competition" button
   - Configure competition details:
     - **Name**: Competition name (e.g., "Spring Championship")
     - **Date**: Competition date (used for age calculations)
     - **Medal Settings**:
       - **Always First Three**: Top 3 athletes receive medals
       - **Percentage-based**: Specify percentage (e.g., 20% of participants)

3. **Competition Settings**
   - **Locked**: Prevents result modifications after completion
   - **Medal Distribution**: Affects diploma generation and rankings

## Event and Category Configuration

### Event Management

Events represent track and field disciplines with specific scoring parameters.

#### Event Types

1. **RUN Events** (Sprint distances)
   - Examples: 60m, 100m, 200m
   - Measured in time (format: seconds.centiseconds)
   - Scoring: Lower time = higher points

2. **RUN_LONG Events** (Distance running)
   - Examples: 800m, 1500m, 3000m
   - Measured in time (format: minutes:seconds.centiseconds)
   - Scoring: Lower time = higher points

3. **JUMP_THROW Events** (Field events)
   - Examples: Long jump, high jump, shot put, discus
   - Measured in distance (centimeters)
   - Scoring: Greater distance = higher points

#### Creating Events

1. **Navigate to Events Management**
   - From main menu, select "Events"
   - Click "Add Event" button

2. **Event Configuration**
   - **Name**: Event name (e.g., "60m Sprint")
   - **Type**: Select appropriate event type
   - **Gender**: M (Male), F (Female), or create separate events
   - **IAAF Coefficients**: 
     - **A, B, C values**: Official IAAF scoring parameters
     - Contact IAAF or athletics federation for official values
     - Critical for accurate point calculations

#### IAAF Scoring Formulas

The system uses official IAAF formulas for point calculation:

- **RUN Events**: `points = A × ((B - time_in_centiseconds) / 100)^C`
- **RUN_LONG Events**: Convert time format, then apply RUN formula
- **JUMP_THROW Events**: `points = A × ((distance_in_centimeters - B) / 100)^C`

### Category Management

Categories group athletes by age and gender for fair competition.

#### Creating Categories

1. **Access Series Categories**
   - Select your series
   - Navigate to "Categories" tab
   - Click "Add Category" button

2. **Category Configuration**
   - **Name**: Descriptive name (e.g., "Boys U12")
   - **Gender**: M (Male) or F (Female)
   - **Age Range**: 
     - **Year From**: Oldest birth year eligible (e.g., 2010)
     - **Year To**: Youngest birth year eligible (e.g., 2011)
   - **Event Assignment**: Select up to 10 events for this category

#### Age Calculation Rules

- Age eligibility based on birth year, not exact birth date
- Competition date used as reference point
- Athletes must fall within the specified year range
- Example: Category "2010-2011" includes athletes born in 2010 or 2011

#### Event Assignment

- Maximum 10 events per category
- Events can be shared across multiple categories
- Position ordering determines event sequence in reports
- Consider athlete capabilities and competition duration

### Copying Categories

For recurring series, you can copy categories from previous years:

1. **Use Copy Categories Feature**
   - In Categories tab, click "Copy Categories"
   - Select source series
   - Choose which categories to copy
   - Adjust age ranges for new year

2. **Benefits**
   - Saves setup time
   - Maintains consistency across years
   - Preserves event assignments and ordering

## Athlete Registration

### Club Management

Before registering athletes, set up clubs:

1. **Navigate to Clubs**
   - From main menu, select "Clubs"
   - Click "Add Club" button

2. **Club Configuration**
   - **Name**: Full club name
   - **Abbreviation**: Short form for reports (e.g., "TVE" for "Turnverein Erlach")

### Athlete Registration Process

1. **Access Athlete Management**
   - From series view, navigate to "Athletes" tab
   - Click "Add Athlete" or "Search Athletes" for existing athletes

2. **Athlete Information**
   - **Personal Details**:
     - First Name and Last Name
     - Gender (M/F)
     - Year of Birth
   - **Club Assignment**: Select from registered clubs
   - **Category Assignment**: Assign to appropriate categories

3. **Category Assignment Rules**
   - Athletes can participate in multiple categories
   - Must meet age eligibility requirements
   - Gender must match category requirements
   - System validates eligibility automatically

### Bulk Athlete Operations

- **Search Existing Athletes**: Find athletes from other series in your organization
- **Bulk Assignment**: Assign multiple athletes to categories simultaneously
- **Import/Export**: Use for large athlete databases (contact support for assistance)

## Result Entry and Management

### Accessing Result Entry

1. **Navigate to Result Capturing**
   - From series view, select a competition
   - Click "Enter Results" button
   - System displays result entry interface

### Result Entry Interface

#### Athlete Filtering
- **Filter by ID**: Enter athlete number for quick access
- **Filter by Name**: Search by first name or last name
- **Category View**: Results organized by category

#### Entering Results

1. **Select Athlete**: Use filters to find specific athlete
2. **Enter Performance**: 
   - **Time Events**: Enter in appropriate format
     - RUN: seconds.centiseconds (e.g., "12.34")
     - RUN_LONG: minutes:seconds.centiseconds (e.g., "2:15.67")
   - **Distance Events**: Enter in centimeters (e.g., "450" for 4.50m)
3. **Automatic Calculation**: Points calculated immediately using IAAF formulas
4. **Save Results**: Results saved automatically

#### Result Formats

**Time Events:**
- Sprint (RUN): "12.34" (12.34 seconds)
- Distance (RUN_LONG): "2:15.67" (2 minutes, 15.67 seconds)

**Distance Events:**
- Enter in centimeters: "450" = 4.50 meters
- Long Jump: "520" = 5.20 meters
- Shot Put: "1250" = 12.50 meters

### Managing DNF (Did Not Finish)

1. **Mark DNF**: Check DNF box for athletes who didn't complete event
2. **DNF Effects**:
   - Athlete receives 0 points for that event
   - Excluded from event rankings
   - Can still compete in other events
   - Shown separately in reports

### Result Validation

- **Format Validation**: System checks result format matches event type
- **Range Validation**: Warns about unusual performances
- **Duplicate Prevention**: Prevents multiple results for same athlete/event
- **Point Calculation**: Automatic validation of calculated points

### Bulk Operations

- **Remove All Results**: Clear all results for a competition (with confirmation)
- **Recalculate Points**: Refresh point calculations if formulas change
- **Export Results**: Download results for external analysis

## Report Generation

### Available Report Types

#### Competition Reports
1. **Competition Rankings**: Athletes ranked by total points within each category
2. **Event Rankings**: Best performances per event across all categories
3. **Diplomas**: Certificates for medal winners based on competition settings

#### Series Reports
1. **Series Rankings**: Cumulative points across all competitions in series
2. **Club Rankings**: Total points by club across entire series

#### Administrative Reports
1. **Result Sheets**: Blank forms for manual result recording
2. **Athlete Numbers**: List of athlete numbers for identification
3. **Numbers and Sheets**: Combined report for competition preparation

### Generating Reports

1. **Access Reports**
   - From dashboard or series view
   - Click on desired report type
   - Reports available for public download or authenticated access

2. **Report Features**
   - **PDF Format**: Professional, printable documents
   - **Series Branding**: Includes organization and series logos
   - **Multi-language**: Reports in selected language
   - **Medal Integration**: Reflects competition medal settings

### Report Content

#### Competition Rankings
- Athletes listed by total points within category
- Individual event results and points
- Medal indicators based on competition settings
- DNF athletes listed separately

#### Series Rankings
- **Eligibility**: Only athletes who competed in ALL competitions
- **Cumulative Points**: Total points across all competitions
- **Consistency Reward**: Favors athletes with consistent participation

#### Diplomas
- **Medal Winners**: Based on competition medal settings
- **Professional Format**: Suitable for printing and presentation
- **Personalized**: Individual certificates with athlete names and achievements

### Report Customization

- **Logo Placement**: Series and organization logos automatically included
- **Language Selection**: Choose report language
- **Medal Settings**: Reflected in diploma generation
- **Date Formatting**: Locale-appropriate date formats

## Best Practices

### Series Planning

1. **Annual Setup**
   - Plan series structure at beginning of season
   - Define categories based on expected participation
   - Set consistent event selection across competitions

2. **Event Selection**
   - Balance between variety and competition duration
   - Consider athlete capabilities and age appropriateness
   - Maintain consistency with previous years for comparison

3. **Category Design**
   - Create fair age groupings
   - Consider gender-specific events where appropriate
   - Plan for expected athlete numbers

### Competition Management

1. **Pre-Competition**
   - Verify athlete registrations and category assignments
   - Generate and print result sheets
   - Prepare athlete numbers
   - Test result entry system

2. **During Competition**
   - Enter results promptly for real-time rankings
   - Use DNF marking for incomplete performances
   - Verify unusual results before saving
   - Generate preliminary reports for announcements

3. **Post-Competition**
   - Complete all result entry
   - Generate final reports
   - Distribute diplomas and rankings
   - Lock competition to prevent accidental changes

### Data Management

1. **Regular Backups**
   - System automatically backs up data
   - Download important reports for local storage
   - Maintain athlete database for future series

2. **Quality Control**
   - Review results for accuracy before finalizing
   - Verify IAAF coefficients with official sources
   - Cross-check medal distributions with competition rules

3. **User Management**
   - Regularly review user access permissions
   - Remove access for inactive users
   - Train new users on system procedures

### Performance Optimization

1. **Result Entry Efficiency**
   - Use athlete filtering for quick access
   - Enter results in systematic order (by event or athlete)
   - Utilize bulk operations where appropriate

2. **Report Generation**
   - Generate reports during off-peak times for large competitions
   - Use appropriate report types for different audiences
   - Plan report distribution in advance

## Troubleshooting

### Common Issues

#### Login and Access Problems

**Problem**: Cannot log in to system
**Solutions**:
- Verify email address and password
- Check for email confirmation if new account
- Clear browser cache and cookies
- Try different browser or incognito mode
- Contact system administrator for password reset

**Problem**: Cannot access organization data
**Solutions**:
- Verify organization selection in user interface
- Confirm user has been added to organization
- Check user role permissions (USER vs ADMIN)
- Contact organization administrator

#### Result Entry Issues

**Problem**: Cannot enter results for athlete
**Solutions**:
- Verify athlete is registered in series
- Check athlete is assigned to correct category
- Confirm competition is not locked
- Verify result format matches event type

**Problem**: Points calculation seems incorrect
**Solutions**:
- Verify IAAF coefficients for event
- Check result format (time vs distance)
- Confirm event type setting (RUN vs RUN_LONG vs JUMP_THROW)
- Contact technical support with specific examples

#### Report Generation Problems

**Problem**: Reports not generating or displaying incorrectly
**Solutions**:
- Check browser PDF viewer settings
- Try downloading report instead of viewing in browser
- Verify all required data is entered (athletes, results)
- Clear browser cache and retry

**Problem**: Missing logos in reports
**Solutions**:
- Verify logo upload was successful
- Check logo file format (PNG/JPG recommended)
- Ensure logo file size is reasonable (<5MB)
- Re-upload logo if necessary

### Data Validation Errors

#### Age Eligibility Issues
- **Error**: "Athlete not eligible for category"
- **Solution**: Check athlete birth year against category year range
- **Prevention**: Verify category age ranges during setup

#### Event Assignment Problems
- **Error**: "Too many events in category"
- **Solution**: Categories limited to 10 events maximum
- **Prevention**: Plan event selection carefully

#### Result Format Errors
- **Error**: "Invalid result format"
- **Solution**: Check result format matches event type requirements
- **Examples**: 
  - RUN events: "12.34" (not "12,34" or "12:34")
  - RUN_LONG events: "2:15.67" (not "135.67")
  - JUMP_THROW events: "450" (not "4.50")

### Performance Issues

#### Slow System Response
**Causes and Solutions**:
- **Large competitions**: Break result entry into smaller sessions
- **Network issues**: Check internet connection stability
- **Browser performance**: Close unnecessary tabs, restart browser
- **Peak usage**: Try accessing during off-peak hours

#### Report Generation Delays
**Causes and Solutions**:
- **Large datasets**: Allow extra time for complex reports
- **Multiple users**: Coordinate report generation timing
- **Browser limitations**: Try different browser or download instead of viewing

### Getting Help

#### Self-Service Resources
1. **System Help**: Built-in help tooltips and guidance
2. **Documentation**: This guide and other system documentation
3. **FAQ**: Common questions and answers (if available)

#### Support Contacts
1. **Technical Issues**: Contact system administrator or technical support
2. **IAAF Coefficients**: Consult athletics federation or IAAF documentation
3. **Training**: Request additional user training sessions

#### Reporting Issues
When contacting support, include:
- **User Information**: Your email and organization
- **Error Details**: Exact error messages and steps to reproduce
- **Browser Information**: Browser type and version
- **Screenshots**: Visual evidence of issues when helpful

---

## Appendix

### IAAF Coefficient Resources

For official IAAF scoring coefficients:
- Contact your national athletics federation
- Refer to IAAF Combined Events Scoring Tables
- Verify coefficients match your competition level (youth vs senior)

### System Limits

- **Events per Category**: Maximum 10
- **Athletes per Series**: No practical limit
- **Competitions per Series**: No practical limit
- **File Upload Size**: 5MB maximum for logos
- **Concurrent Users**: System supports multiple simultaneous users

### Keyboard Shortcuts

- **Tab**: Navigate between form fields
- **Enter**: Save current field and move to next
- **Escape**: Cancel current dialog or operation
- **Ctrl+S**: Save current form (where applicable)

This guide provides comprehensive information for competition organizers using the JTAF system. For additional support or specific questions not covered here, please contact your system administrator or technical support team.