# Reminders App

A simple, clean Android reminder application built with **Kotlin** and **Jetpack Compose**. Set reminders that ring a specific number of times to get your attention without the complexity of a full alarm app.

## ✨ Features

### Simple Reminders
- **Multiple Reminders**: Create as many reminders as you need
- **Custom Labels**: Name each reminder for easy identification  
- **Ring Count**: Choose how many times the reminder rings (1, 2, 3, 5, or 10 times)
- **Repeat Schedule**: One-time or weekly on selected days

### Clean Design
- **Modern UI**: Material Design 3 with sunrise orange theme
- **Dark/Light Mode**: Automatic theme based on system settings
- **Minimal Interface**: Only the settings you need, nothing more
- **Visual Feedback**: Animated empty state and smooth transitions

### Reliable
- **Boot Persistence**: Reminders restored after device restart
- **Background Operation**: Works even when app is closed
- **Notification Actions**: Dismiss directly from notification

## 📱 Screenshots

The app features:
- **Main Screen**: List of all reminders with time, label, repeat days, and ring count
- **Add/Edit Dialog**: Simple time picker with label, ring count, and repeat options

## 🏗️ Architecture

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room with SQLite
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Background**: AlarmManager with exact scheduling

### Project Structure
```
app/src/main/java/com/example/alarmyapp/
├── data/
│   ├── model/           # Alarm entity
│   ├── dao/             # Database access
│   ├── database/        # Room configuration
│   ├── repository/      # Data layer
│   └── converter/       # Type converters
├── alarm/
│   ├── AlarmScheduler   # Scheduling logic
│   ├── AlarmReceiver    # Trigger handler
│   ├── AlarmService     # Ring service
│   ├── BootReceiver     # Boot restore
│   └── AlarmActionReceiver # Dismiss handler
├── viewmodel/           # UI state
├── ui/
│   ├── screen/          # Compose screens
│   └── theme/           # App theme
└── MainActivity         # Entry point
```

## 🔧 Setup & Build

### Requirements
- Android Studio Hedgehog+ (or just JDK 17 for command line)
- Android SDK API 24+ (Android 7.0)
- Kotlin 1.9+

### Quick Build (Command Line)

**Windows:**
```bash
# Build debug APK
build.bat debug

# Build release APK
build.bat release

# Clean build files
build.bat clean

# Build and install on connected device
build.bat install
```

**Linux/macOS:**
```bash
# Make script executable (first time only)
chmod +x build.sh

# Build debug APK
./build.sh debug

# Build release APK
./build.sh release

# Clean build files
./build.sh clean

# Build and install on connected device
./build.sh install
```

### Build with Gradle Directly

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on device
./gradlew installDebug

# Run tests
./gradlew test

# Clean
./gradlew clean
```

### Output Locations
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Build with Android Studio
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Select `Build > Build Bundle(s) / APK(s) > Build APK(s)`
4. Or click the green Run button to build and install

### Permissions
- `SCHEDULE_EXACT_ALARM` - Precise scheduling
- `POST_NOTIFICATIONS` - Show reminders
- `RECEIVE_BOOT_COMPLETED` - Restore after reboot
- `VIBRATE` - Vibration on ring
- `FOREGROUND_SERVICE` - Background service

## 📋 How It Works

### Ring Behavior
When a reminder triggers:
1. Plays notification sound
2. Vibrates briefly
3. Waits 1 second
4. Repeats for the configured number of rings
5. Auto-dismisses when complete

### Repeat Options
- **Once**: Triggers one time, then disables
- **Weekly**: Select specific days (Mon, Tue, Wed, etc.)
- **Presets**: Weekdays, Weekends, Daily

## 🎨 Theme

The app uses a custom sunrise-inspired color palette:
- **Primary**: Sunrise Orange (#FF6B35)
- **Secondary**: Soft Purple (#7B68EE)
- **Tertiary**: Soft Cyan (#6FEDD6)
- **Dark Background**: Deep Night (#1A1A2E)

## 📄 License

MIT License

---

**Reminders** - Simple notifications, exactly when you need them. 🔔