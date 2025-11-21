# Helmut

An open-source Android time-boxed task management app inspired by LlamaLife. Built with Kotlin, Jetpack Compose, and modern Android architecture.

## Features

- ✅ Create tasks with time estimates
- ⏱️ Focus mode with visual countdown timer
- ⏸️ Pause/resume timers
- ✔️ Complete or skip tasks
- 📱 Clean Material Design 3 UI
- 💾 Local database persistence with Room

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room
- **Asynchronous**: Kotlin Coroutines & Flow
- **Build**: Gradle (Kotlin DSL)

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android SDK 26+ (Android 8.0 Oreo or higher)
- JDK 11 or higher

### Building the App

1. Clone the repository:
```bash
git clone https://github.com/yourusername/helmut.git
cd helmut
```

2. Open in Android Studio or build from command line:

**Using Android Studio's JDK:**
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew clean build
```

**Or just:**
```bash
./gradlew assembleDebug
```
(Requires Java/JDK in your PATH)

3. The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

### Running the App

#### Using Android Studio:
1. Open project in Android Studio
2. Create an Android Virtual Device (AVD) or connect a physical device
3. Click the Run button (▶️)

#### Using Command Line:
```bash
./gradlew installDebug
```

## Project Structure

```
app/src/main/java/com/helmut/
├── data/
│   ├── local/          # Room database
│   │   ├── AppDatabase.kt     # Database configuration
│   │   └── TaskDao.kt         # Data access object
│   ├── model/          # Data models
│   │   └── Task.kt            # Task entity
│   └── repository/     # Data layer
│       └── TaskRepository.kt  # Repository pattern
├── di/                 # Dependency injection
│   └── DatabaseModule.kt      # Hilt modules
├── ui/
│   ├── screens/       # Compose screens
│   │   └── TaskListScreen.kt # Main screen
│   └── theme/         # Material theming
├── utils/             # Utilities
│   ├── TimerManager.kt       # Timer logic
│   └── NotificationHelper.kt # Notifications
├── viewmodel/         # ViewModels
│   └── TaskViewModel.kt      # Task business logic
├── HelmutApplication.kt      # Application class
└── MainActivity.kt           # Entry point
```

## Code Architecture Explained

### For Kotlin Beginners

If you're new to Kotlin or Android development, here's what each component does:

#### 1. **Data Layer** (`data/`)

**Task.kt** - The data model
```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,           // Unique ID (auto-generated)
    val title: String,          // Task name
    val estimatedMinutes: Int,  // Time estimate
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val order: Int = 0
)
```
- `@Entity`: Marks this as a database table
- `data class`: Kotlin's way of creating simple data containers (like POJOs in Java)
- `val`: Read-only property (like `final` in Java)
- `= 0`: Default value if not specified

**TaskDao.kt** - Database operations
```kotlin
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY `order` ASC")
    fun getActiveTasks(): Flow<List<Task>>
    
    @Insert
    suspend fun insertTask(task: Task): Long
}
```
- `@Dao`: Data Access Object - defines database operations
- `Flow<List<Task>>`: Observable stream of data (updates automatically when DB changes)
- `suspend`: Marks function as asynchronous (runs in background, doesn't block UI)

**AppDatabase.kt** - Database setup
```kotlin
@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
```
- Room generates all the boilerplate database code for you

**TaskRepository.kt** - Abstraction layer
```kotlin
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    
    suspend fun addTask(task: Task): Long = taskDao.insertTask(task)
}
```
- `@Inject constructor`: Hilt will automatically provide dependencies
- Repository pattern: Separates data source from business logic
- Makes it easy to swap database for API calls later

#### 2. **ViewModel** (`viewmodel/`)

**TaskViewModel.kt** - Business logic and UI state
```kotlin
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    fun addTask(title: String, estimatedMinutes: Int) {
        viewModelScope.launch {
            val task = Task(title = title, estimatedMinutes = estimatedMinutes)
            repository.addTask(task)
        }
    }
}
```
- `ViewModel`: Survives configuration changes (like screen rotation)
- `StateFlow`: Like LiveData but for Kotlin coroutines
- `viewModelScope.launch`: Runs code in background, automatically cancelled when ViewModel dies
- **Key concept**: UI observes `uiState`, ViewModel updates `_uiState`

#### 3. **UI Layer** (`ui/`)

**TaskListScreen.kt** - Compose UI
```kotlin
@Composable
fun TaskListScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn {
        items(uiState.activeTasks) { task ->
            TaskItem(task = task)
        }
    }
}
```
- `@Composable`: Function that describes UI (declarative, like React)
- `by`: Kotlin delegation - unwraps StateFlow automatically
- `collectAsState()`: Converts Flow to Compose State (recomposes UI when changed)
- `LazyColumn`: RecyclerView equivalent (only renders visible items)

**Key Compose Concepts:**
- UI is a function of state: `UI = f(state)`
- When state changes, UI automatically recomposes
- No more `findViewById()` or `findViewById()`!

#### 4. **Dependency Injection** (`di/`)

**DatabaseModule.kt** - Tells Hilt how to create dependencies
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "helmut_database"
        ).build()
    }
}
```
- Hilt automatically creates and injects dependencies
- `@Singleton`: Only one instance app-wide
- `@ApplicationContext`: Hilt provides Android Context

#### 5. **Utils** (`utils/`)

**TimerManager.kt** - Countdown timer logic
```kotlin
class TimerManager(private val scope: CoroutineScope) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState
    
    fun startTimer(minutes: Int, onComplete: () -> Unit) {
        scope.launch {
            while (_timerState.value.remainingSeconds > 0) {
                delay(1000)  // Wait 1 second
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = _timerState.value.remainingSeconds - 1
                )
            }
            onComplete()
        }
    }
}
```
- Uses coroutines for async countdown
- Emits state updates every second
- UI observes `timerState` and updates automatically

### Key Kotlin Concepts

**1. Null Safety**
```kotlin
val name: String = "Helmut"      // Cannot be null
val name: String? = null         // Can be null
val length = name?.length        // Safe call: returns null if name is null
val length = name?.length ?: 0   // Elvis operator: default to 0 if null
```

**2. Data Classes**
```kotlin
data class Task(val title: String, val minutes: Int)
// Auto-generates: equals(), hashCode(), toString(), copy()

val task1 = Task("Write code", 30)
val task2 = task1.copy(minutes = 45)  // Copy with changes
```

**3. Extension Functions**
```kotlin
fun Int.formatTime(): String {
    val mins = this / 60
    val secs = this % 60
    return "$mins:$secs"
}

val time = 125.formatTime()  // "2:5"
```

**4. Coroutines**
```kotlin
suspend fun fetchData() { ... }           // Can be suspended/resumed
viewModelScope.launch { fetchData() }     // Launch coroutine
```

**5. Flow**
```kotlin
flow {
    emit(1)
    delay(1000)
    emit(2)
}.collect { value ->
    println(value)  // Prints 1, then 2 after 1 second
}
```

## How the App Works (Flow Diagram)

```
User adds task → TaskViewModel.addTask() → TaskRepository.addTask() 
                                         ↓
                                    Room Database
                                         ↓
TaskDao.getActiveTasks() → Flow<List<Task>> → TaskRepository
                                         ↓
                              TaskViewModel (uiState)
                                         ↓
                              TaskListScreen observes
                                         ↓
                                    UI recomposes
```

### Timer Flow

```
User clicks "Start" → TaskViewModel.startTask()
                              ↓
                    TimerManager.startTimer()
                              ↓
                    Coroutine loop (every 1s)
                              ↓
                    timerState updates
                              ↓
                    FocusCard observes
                              ↓
                    Timer UI updates
```

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests (requires emulator/device)
```bash
./gradlew connectedAndroidTest
```

## CI/CD

GitHub Actions automatically:
- Builds the app on every push
- Runs tests
- Generates code coverage reports
- Uploads APK artifacts

See `.github/workflows/android.yml` for details.

## Code Coverage

Generate coverage report locally:
```bash
./gradlew jacocoTestReport
```
View report at: `app/build/reports/jacoco/html/index.html`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by [LlamaLife](https://llamalife.co/)
- Built with love for the Android community

## Further Learning Resources

### For Kotlin Beginners:
- [Official Kotlin Docs](https://kotlinlang.org/docs/getting-started.html)
- [Kotlin Koans](https://play.kotlinlang.org/koans) - Interactive exercises

### For Android Developers:
- [Android Developers Guide](https://developer.android.com/guide)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

### For This Codebase:
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)

## FAQ

**Q: Why use Room instead of raw SQLite?**
A: Room provides compile-time verification of SQL queries, automatic object mapping, and Flow support for reactive UIs.

**Q: What's the difference between `val` and `var`?**
A: `val` is immutable (like `final` in Java), `var` is mutable.

**Q: Why use Hilt/Dagger?**
A: It handles dependency creation and injection automatically, making code more testable and reducing boilerplate.

**Q: What's a StateFlow vs LiveData?**
A: Both are observable data holders. StateFlow is Kotlin-first, works better with coroutines, and has a clearer API.

**Q: How does Compose differ from XML layouts?**
A: Compose is declarative (describe what UI should look like) vs XML's imperative approach (manually update views). Compose has less boilerplate and better Kotlin integration.
