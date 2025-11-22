# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Timer completion alerts with customizable notifications, sounds, and vibration
- Settings screen for alert preferences
- Task templates for reusable task sets
- History screen with completed tasks and statistics
- Bottom navigation with 4 tabs (Today/Templates/History/Settings)
- DataStore for persistent user preferences
- Material Design 3 UI with Jetpack Compose
- Room database for local data persistence
- Focus mode with pause/resume functionality
- Task ordering and management

### Technical
- MVVM architecture with Hilt dependency injection
- Kotlin Coroutines and Flow for async operations
- Comprehensive README with beginner-friendly architecture guide
- CI/CD pipeline with GitHub Actions
- Automated builds for multiple Android API levels

## [1.0.0] - 2025-11-22

### Added
- Initial release
- Core time-boxed task management functionality
- Visual countdown timer
- Task creation and completion
- Local database persistence

[Unreleased]: https://github.com/florianow/helmut/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/florianow/helmut/releases/tag/v1.0.0
