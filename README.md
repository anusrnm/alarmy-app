# Alarmy App - Advanced Android Alarm Application

A comprehensive Android alarm application built with **Kotlin** and **Jetpack Compose**, featuring advanced alarm management, customizable sounds, vibration patterns, and flexible scheduling options.

## 🚀 Features

### Core Alarm Functionality
- **Multiple Alarms**: Set unlimited number of alarms
- **Flexible Scheduling**: One-time or recurring alarms
- **Custom Repeat Patterns**: Daily, weekdays, weekends, or select specific days
- **Smart Time Management**: Automatic calculation of next alarm trigger time

### Audio & Vibration Control
- **Volume Control**: Adjustable alarm volume (0-100%)
- **Sound Selection**: Default alarm sound or custom sound URIs
- **Vibration Patterns**: 4 levels (None, Light, Medium, Strong)
- **Duration Control**: Set how long alarms ring (1-60 minutes)

### User Experience
- **Snooze Functionality**: Configurable snooze intervals
- **Quick Actions**: Dismiss or snooze from notification
- **Enable/Disable**: Toggle alarms without deleting
- **Clean UI**: Material Design 3 with light/dark theme support

### System Integration
- **Boot Persistence**: Alarms restored after device restart
- **Background Operation**: Reliable alarm triggering even when app is closed
- **Notification Management**: Full-screen alarms with action buttons
- **Permission Handling**: Automatic request for required permissions

## 🏗️ Architecture

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room with SQLite and Coroutines
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Background Processing**: AlarmManager with exact scheduling
- **Async Operations**: Kotlin Coroutines and Flow

### Project Structure
```
app/src/main/java/com/example/alarmyapp/
├── data/
│   ├── model/           # Alarm entity and data models
│   ├── dao/             # Database access objects
│   ├── database/        # Room database configuration
│   ├── repository/      # Data repository layer
│   └── converter/       # Type converters for Room
├── alarm/
│   ├── AlarmScheduler   # Alarm scheduling logic
│   ├── AlarmReceiver    # Broadcast receiver for alarm triggers
│   ├── AlarmService     # Foreground service for alarm playback
│   ├── BootReceiver     # Handles device boot events
│   └── AlarmActionReceiver # Handles snooze/dismiss actions
├── viewmodel/           # ViewModels for UI state management
├── ui/
│   ├── screen/          # Compose screen components
│   └── theme/           # Material Design theme
└── MainActivity         # Main application entry point
```

## 📱 User Interface

### Main Screen
- **Alarm List**: Display all configured alarms with status
- **Quick Toggle**: Enable/disable alarms with switch controls
- **Add Button**: Floating action button to create new alarms
- **Time Display**: Large, clear time format (HH:MM)
- **Status Indicators**: Visual feedback for enabled/disabled state

### Alarm Configuration Dialog
- **Time Picker**: Hour and minute selection
- **Label**: Custom alarm name/description
- **Repeat Settings**: Checkbox and day selection chips
- **Audio Controls**: Volume slider and sound selection
- **Vibration**: Pattern selection (None/Light/Medium/Strong)
- **Duration**: How long alarm should ring
- **Snooze Settings**: Enable/disable and interval configuration

## 🔧 Setup and Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK API 24 (Android 7.0) or higher
- Kotlin 1.9.10 or higher
- Java 17 compatibility

### Building the Project
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build and run on device or emulator

### Required Permissions
The app automatically requests these permissions:
- `WAKE_LOCK` - Keep device awake for alarms
- `VIBRATE` - Vibration functionality
- `RECEIVE_BOOT_COMPLETED` - Restore alarms after reboot
- `SCHEDULE_EXACT_ALARM` - Precise alarm scheduling
- `POST_NOTIFICATIONS` - Display alarm notifications
- `FOREGROUND_SERVICE` - Background alarm service

## 🎯 Key Features Implementation

### Alarm Duration Control
- Configure alarm duration from 1 to 60 minutes
- Automatic stop after specified duration
- Visual duration indicator in alarm configuration

### Multiple Alarm Support
- Unlimited number of concurrent alarms
- Each alarm maintains independent settings
- Bulk enable/disable operations
- Individual alarm management

### Advanced Repeat Patterns
- **Once**: Single-time alarm
- **Daily**: Every day repeat
- **Weekdays**: Monday through Friday
- **Weekends**: Saturday and Sunday
- **Custom**: Select specific days of the week

### Sound and Vibration Customization
- Volume control with live preview
- Multiple vibration intensity levels
- Default system alarm sound integration
- Support for custom sound URIs

### Snooze and Dismiss Options
- Configurable snooze intervals (1-60 minutes)
- Quick action buttons in notifications
- Full-screen alarm interface
- Persistent snooze scheduling

## 🔒 Data Persistence

### Room Database Schema
- **Alarms Table**: Stores all alarm configurations
- **Type Converters**: Handle complex data types (Set<Integer>)
- **Migration Support**: Database version management
- **Backup Integration**: Automatic backup rules

### Alarm Scheduling Persistence
- Alarms survive app closure and device restart
- Automatic rescheduling after boot
- Failure recovery mechanisms
- Exact alarm scheduling with fallbacks

## 🚨 System Integration

### Background Operation
- **Foreground Service**: Ensures alarm playback continues
- **Notification Channels**: Proper notification categorization
- **Wake Lock Management**: Reliable device wake-up
- **Battery Optimization**: Handles Doze mode and app standby

### Notification System
- **High Priority**: Ensures alarm visibility
- **Full-Screen Intent**: Alarm activity over lock screen
- **Action Buttons**: Direct snooze and dismiss options
- **Ongoing Notification**: Persistent during alarm playback

## 🎨 UI/UX Design

### Material Design 3
- Modern Material You color system
- Dynamic theming support
- Consistent iconography
- Accessible design patterns

### Responsive Layout
- Adaptive layouts for different screen sizes
- Touch-friendly controls
- Clear visual hierarchy
- Intuitive navigation patterns

## 🔧 Configuration Options

### Default Settings
- Default alarm time: 07:00
- Default label: "Alarm"
- Default volume: 80%
- Default vibration: Medium
- Default duration: 5 minutes
- Default snooze: 5 minutes

### Customization
All settings are configurable per alarm:
- Time (24-hour format)
- Custom labels
- Volume levels (0-100%)
- Vibration patterns
- Ring duration
- Snooze intervals
- Repeat patterns

## 📋 Future Enhancements

### Planned Features
- Weather-based alarm adjustments
- Smart snooze based on sleep patterns
- Alarm sound fade-in/fade-out
- Location-based alarms
- Alarm challenges (math problems, puzzles)
- Multiple timezone support
- Alarm analytics and statistics
- Custom alarm sounds from device storage

### Technical Improvements
- Enhanced accessibility features
- Widget support for home screen
- Wear OS integration
- Cloud backup and sync
- Alarm history and statistics
- Dark theme improvements

## 🐛 Troubleshooting

### Common Issues
1. **Alarms not triggering**: Check permissions and battery optimization settings
2. **Sound not playing**: Verify volume settings and Do Not Disturb mode
3. **Alarms lost after reboot**: Ensure RECEIVE_BOOT_COMPLETED permission is granted
4. **Notifications not showing**: Check notification permissions and channel settings

### Debug Features
- Comprehensive logging throughout alarm lifecycle
- Error handling with user-friendly messages
- Fallback mechanisms for critical functions
- Debug builds with additional logging

## 📄 License

This project is open source and available under the MIT License.

---

**Alarmy App** - Your reliable companion for never missing important moments! 🕐⏰