# Contributing to Helmut

Thank you for considering contributing to Helmut! 🎉

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Description**: Clear description of the issue
- **Steps to Reproduce**: How to trigger the bug
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Device Info**: Android version, device model
- **Screenshots**: If applicable

### Suggesting Features

Feature suggestions are welcome! Please:

- Check if the feature has already been suggested
- Clearly describe the feature and its use case
- Explain why this feature would be useful

### Pull Requests

1. Fork the repo and create your branch from `main`
2. If you've added code, add tests
3. Ensure the test suite passes (`./gradlew test`)
4. Make sure your code follows the existing style
5. Write a clear commit message

## Development Setup

1. Clone your fork
2. Open in Android Studio
3. Sync Gradle
4. Run the app on an emulator or device

## Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

## Git Commit Messages

- Use present tense ("Add feature" not "Added feature")
- Capitalize first letter
- Be concise but descriptive
- Reference issues when applicable (#123)

Examples:
```
Add dark mode toggle to settings
Fix timer not pausing correctly
Update README with installation instructions
```

## Testing

Run tests before submitting:

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedCheck

# Lint
./gradlew lint
```

## Questions?

Feel free to open an issue with the `question` label.

Thank you! 🚀
