<div align="center">
  <img src="logo.png" alt="Helmut Logo" width="120" height="120">
  <h1>Helmut</h1>
  <p><strong>Time-boxed task management for focused productivity</strong></p>
  <p>
    <a href="#features">Features</a> •
    <a href="#getting-started">Getting Started</a> •
    <a href="#project-structure">Project Structure</a> •
    <a href="#contributing">Contributing</a> •
    <a href="#license">License</a>
  </p>
  <p>
    <img src="https://img.shields.io/badge/Android-8.0%2B-green?logo=android" alt="Android 8.0+">
    <img src="https://img.shields.io/badge/Kotlin-1.9-blue?logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License">
    <img src="https://github.com/florianow/helmut/workflows/Android%20CI/badge.svg" alt="CI Status">
  </p>
  
  <p>
    <strong>Tested on:</strong> Pixel 9, Pixel 9 Pro, and all Android 8.0+ devices
  </p>
</div>

---

## About

An open-source Android time-boxed task management app inspired by LlamaLife. Built with Kotlin, Jetpack Compose, and modern Android architecture.

Helmut helps you focus on one task at a time with visual countdown timers, customizable alerts, and reusable task templates.

## Features

- ✅ **Time-boxed Tasks** - Create tasks with time estimates
- ⏱️ **Focus Mode** - Visual countdown timer with pause/resume
- 🔔 **Smart Alerts** - Customizable notifications, sounds, and vibration when tasks complete
- 📋 **Task Templates** - Create reusable task sets (e.g., "Morning Routine", "Deep Work Session")
- 📊 **History & Stats** - View completed tasks, track focus time, and maintain streaks
- ⚙️ **Settings** - Configure notification sound, vibration, and alert preferences
- 🧭 **Multi-screen Navigation** - Bottom nav bar for Today/Templates/History/Settings
- 📱 **Material Design 3** - Clean, modern UI built with Jetpack Compose
- 💾 **Offline-first** - Local database persistence with Room

## Screenshots

> 📸 *Screenshots coming soon! Install the app to see it in action.*

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room
- **Data Storage**: DataStore (for user preferences)
- **Navigation**: Jetpack Navigation Compose
- **Notifications**: Android Notification API with custom sounds & vibration
- **Asynchronous**: Kotlin Coroutines & Flow
- **Build**: Gradle (Kotlin DSL)

## Getting Started

### Download Pre-built APK

The easiest way to try Helmut:

1. Go to [Releases](https://github.com/florianow/helmut/releases)
2. Download the latest `helmut-*.apk`
3. Transfer to your Android device
4. Enable "Install from Unknown Sources" in Settings
5. Open the APK and install

### Prerequisites (for building from source)

- Android Studio (latest version recommended)
- Android SDK 26+ (Android 8.0 Oreo or higher)
- JDK 11 or higher

### Building the App

#### Option 1: Using Android Studio (Recommended)

1. Clone the repository:
```bash
git clone https://github.com/florianow/helmut.git
cd helmut
```

2. Open the project in Android Studio:
   - File → Open → Select the `eaddie` folder
   - Wait for Gradle sync to complete
   - Click Run (▶️) button or press `Shift+F10`

#### Option 2: Using Command Line

**On macOS/Linux:**

```bash
# Clone the repository
git clone https://github.com/florianow/helmut.git
cd helmut

# Build the debug APK (using Android Studio's bundled JDK)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug -x lint

# Or if you have JDK 11+ in PATH, simply run:
./gradlew assembleDebug -x lint
```

**On Windows:**

```cmd
# Clone the repository
git clone https://github.com/florianow/helmut.git
cd helmut

# Build the debug APK
gradlew.bat assembleDebug -x lint
```

**Build Output:**
- The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

#### Option 3: Install to Connected Device/Emulator

```bash
# Install the app directly to connected device
./gradlew installDebug

# Or build and install in one step
./gradlew assembleDebug installDebug
```

### Running the App

#### Using Android Studio:
1. Open project in Android Studio
2. Create an Android Virtual Device (AVD) or connect a physical device via USB (with USB debugging enabled)
3. Click the Run button (▶️) or press `Shift+F10`

#### Using Command Line:
```bash
# Make sure a device/emulator is connected
adb devices

# Install and run
./gradlew installDebug
adb shell am start -n com.helmut/.MainActivity
```

## Project Structure

```
app/src/main/java/com/helmut/
├── data/
│   ├── local/          # Room database
│   │   ├── AppDatabase.kt     # Database configuration (v2 with templates)
│   │   ├── TaskDao.kt         # Task data access
│   │   └── TemplateDao.kt     # Template data access
│   ├── model/          # Data models
│   │   ├── Task.kt            # Task entity
│   │   ├── Template.kt        # Template entity
│   │   ├── TemplateTask.kt    # Template task entity
│   │   └── TemplateWithTasks.kt # Relation model
│   └── repository/     # Data layer
│       ├── TaskRepository.kt      # Task data operations
│       ├── TemplateRepository.kt  # Template data operations
│       └── SettingsRepository.kt  # User preferences (DataStore)
├── di/                 # Dependency injection
│   └── DatabaseModule.kt      # Hilt modules
├── ui/
│   ├── screens/       # Compose screens
│   │   ├── TaskListScreen.kt         # Today screen (main task list)
│   │   ├── TemplatesScreen.kt        # Templates browser
│   │   ├── CreateTemplateDialog.kt   # Template creation dialog
│   │   ├── HistoryScreen.kt          # Completed tasks & stats
│   │   └── SettingsScreen.kt         # Alert & notification settings
│   └── theme/         # Material theming
├── utils/             # Utilities
│   ├── TimerManager.kt       # Timer logic
│   └── NotificationHelper.kt # Notifications, sounds, vibration
├── viewmodel/         # ViewModels
│   ├── TaskViewModel.kt      # Task business logic
│   ├── TemplateViewModel.kt  # Template business logic
│   └── SettingsViewModel.kt  # Settings business logic
├── HelmutApplication.kt      # Application class
└── MainActivity.kt           # Entry point with navigation
```

## Code Architecture Explained

### For Complete Beginners

If you're new to Kotlin, Android, or programming in general, here's a **step-by-step** guide to understanding this codebase.

---

### 🏗️ **The Big Picture: How the App Works**

Think of the app like a restaurant:

1. **UI (Screens)** = The dining room where customers (users) interact
2. **ViewModel** = The waiter who takes orders and brings food
3. **Repository** = The kitchen manager who coordinates everything
4. **Database (Room)** = The pantry where ingredients (data) are stored
5. **Models** = The recipes (data structures)

**Flow:** User taps button → UI tells ViewModel → ViewModel asks Repository → Repository reads/writes Database → Data flows back → UI updates

---

### 📁 **Layer 1: Data Models** (`data/model/`)

These are like blueprints that define what data looks like.

#### **Task.kt** - What is a task?

```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,                      // Unique ID (like a barcode)
    val title: String,                     // "Write report"
    val estimatedMinutes: Int,             // 30
    val isCompleted: Boolean = false,      // Done or not?
    val createdAt: Long = System.currentTimeMillis(),  // When created (timestamp)
    val completedAt: Long? = null,         // When finished (null if not done)
    val order: Int = 0                     // Position in list
)
```

**What this means:**
- `@Entity` → This becomes a database table called "tasks"
- `data class` → Kotlin's way to create a simple data container (automatically gives you `.copy()`, `.equals()`, etc.)
- `val` → Read-only property (can't change after creation)
- `= 0` → Default value if you don't provide one
- `Long?` → The `?` means it can be `null` (no value yet)

**Example:**
```kotlin
val task = Task(
    title = "Meditation", 
    estimatedMinutes = 10
)
// id will auto-generate, isCompleted defaults to false, etc.
```

#### **Template.kt** - Reusable task groups

```kotlin
@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,              // "Morning Routine"
    val description: String,       // "Start your day right"
    val icon: String              // "☀️"
)
```

#### **TemplateTask.kt** - A task within a template

```kotlin
@Entity(
    tableName = "template_tasks",
    foreignKeys = [ForeignKey(
        entity = Template::class,
        parentColumns = ["id"],
        childColumns = ["templateId"],
        onDelete = ForeignKey.CASCADE    // Delete tasks when template is deleted
    )]
)
data class TemplateTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: Long,          // Which template this belongs to
    val title: String,             // "Exercise"
    val estimatedMinutes: Int,     // 30
    val order: Int                 // Position in template
)
```

**foreignKeys** → Links this to a Template (like a parent-child relationship)

#### **TemplateWithTasks.kt** - Template + its tasks together

```kotlin
data class TemplateWithTasks(
    @Embedded val template: Template,
    @Relation(
        parentColumn = "id",
        entityColumn = "templateId"
    )
    val tasks: List<TemplateTask>
)
```

This is like saying: "Give me a template AND all its tasks in one object"

---

### 🗄️ **Layer 2: Database Access** (`data/local/`)

These define **how to read/write data** from the database.

#### **TaskDao.kt** - Database operations for tasks

```kotlin
@Dao
interface TaskDao {
    // Get all active (not completed) tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY `order` ASC")
    fun getActiveTasks(): Flow<List<Task>>
    
    // Get completed tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>
    
    // Add a new task
    @Insert
    suspend fun insertTask(task: Task): Long
    
    // Update existing task
    @Update
    suspend fun updateTask(task: Task)
    
    // Delete a task
    @Delete
    suspend fun deleteTask(task: Task)
}
```

**Key concepts:**
- `@Dao` → Data Access Object (defines database methods)
- `@Query` → SQL query (like asking database a question)
- `Flow<List<Task>>` → Live stream of data (updates automatically when database changes)
- `suspend` → This runs in the background (won't freeze the UI)

**What's Flow?**
Imagine a river of data. When you add/delete a task, a new "wave" flows down and the UI automatically updates. No need to manually refresh!

#### **TemplateDao.kt** - Database operations for templates

```kotlin
@Dao
interface TemplateDao {
    // Get all templates with their tasks
    @Transaction
    @Query("SELECT * FROM templates ORDER BY name ASC")
    fun getAllTemplatesWithTasks(): Flow<List<TemplateWithTasks>>
    
    // Create template + tasks together
    @Transaction
    suspend fun insertTemplateWithTasks(template: Template, tasks: List<TemplateTask>): Long
    
    @Delete
    suspend fun deleteTemplate(template: Template)
}
```

`@Transaction` → Makes sure all operations succeed or fail together (atomic operation)

#### **AppDatabase.kt** - Database configuration

```kotlin
@Database(
    entities = [Task::class, Template::class, TemplateTask::class], 
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun templateDao(): TemplateDao
}
```

**What this does:**
- Lists all tables (`entities`)
- `version = 2` → Database schema version (increment when you change structure)
- Room auto-generates all the database code!

---

### 🏪 **Layer 3: Repository** (`data/repository/`)

The repository is like a **middleman** between ViewModel and Database. It provides a clean API.

#### **TaskRepository.kt**

```kotlin
@Singleton  // Only one instance app-wide
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    // Get active tasks (returns Flow)
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    
    // Get completed tasks (returns Flow)
    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
    
    // Add a task
    suspend fun addTask(task: Task): Long = taskDao.insertTask(task)
    
    // Mark task as complete
    suspend fun completeTask(task: Task) {
        taskDao.updateTask(
            task.copy(
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
    }
}
```

**Why have a repository?**
- Clean separation: ViewModel doesn't care if data comes from database, network, or file
- Easy to test: Can create fake repository for testing
- Can combine multiple data sources (e.g., cache + network)

#### **TemplateRepository.kt**

```kotlin
@Singleton
class TemplateRepository @Inject constructor(
    private val templateDao: TemplateDao
) {
    fun getAllTemplatesWithTasks(): Flow<List<TemplateWithTasks>> = 
        templateDao.getAllTemplatesWithTasks()
    
    suspend fun createTemplate(template: Template, tasks: List<TemplateTask>) {
        templateDao.insertTemplateWithTasks(template, tasks)
    }
    
    suspend fun deleteTemplate(template: Template) {
        templateDao.deleteTemplate(template)
    }
}
```

---

### 🧠 **Layer 4: ViewModel** (`viewmodel/`)

The ViewModel contains **business logic** and manages **UI state**.

#### **TaskViewModel.kt**

```kotlin
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    
    // UI State (what the screen shows)
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // Timer manager
    val timerManager = TimerManager(viewModelScope)
    
    // Load tasks when ViewModel is created
    init {
        loadTasks()
        loadCompletedTasks()
    }
    
    // Observe active tasks from database
    private fun loadTasks() {
        viewModelScope.launch {
            repository.getActiveTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(
                    activeTasks = tasks,
                    currentTask = tasks.firstOrNull()
                )
            }
        }
    }
    
    // Add a new task
    fun addTask(title: String, estimatedMinutes: Int) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                estimatedMinutes = estimatedMinutes,
                order = _uiState.value.activeTasks.size
            )
            repository.addTask(task)
        }
    }
    
    // Start timer for a task
    fun startTask(task: Task) {
        _uiState.value = _uiState.value.copy(currentTask = task)
        timerManager.startTimer(task.estimatedMinutes) {
            // Auto-complete when timer finishes
        }
    }
    
    // Mark task as complete
    fun completeTask(task: Task) {
        viewModelScope.launch {
            repository.completeTask(task)
            timerManager.stopTimer()
        }
    }
}
```

**Key concepts:**
- `@HiltViewModel` → Hilt will create this ViewModel automatically
- `StateFlow` → Observable state container (like LiveData but better for Compose)
- `viewModelScope.launch { }` → Run code in background
- `_uiState` (private) vs `uiState` (public) → Internal state vs exposed state
- `.collect { }` → Listen to Flow updates

**UI State Pattern:**

```kotlin
data class TaskUiState(
    val activeTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val currentTask: Task? = null,
    val isLoading: Boolean = false
)
```

This bundles all UI data into one object. The screen observes this and redraws when it changes.

#### **TemplateViewModel.kt**

```kotlin
@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val templateRepository: TemplateRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    // All templates
    val templates: StateFlow<List<TemplateWithTasks>> = templateRepository
        .getAllTemplatesWithTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Create a new template
    fun createTemplate(name: String, description: String, icon: String, tasks: List<TemplateTask>) {
        viewModelScope.launch {
            val template = Template(name = name, description = description, icon = icon)
            templateRepository.createTemplate(template, tasks)
        }
    }
    
    // Add template tasks to today's list
    fun addTemplateToToday(templateWithTasks: TemplateWithTasks) {
        viewModelScope.launch {
            val activeTasks = taskRepository.getActiveTasksList()
            val maxOrder = if (activeTasks.isEmpty()) -1 else activeTasks.maxOf { it.order }
            
            // Convert template tasks to real tasks
            templateWithTasks.tasks.forEachIndexed { index, templateTask ->
                val task = Task(
                    title = templateTask.title,
                    estimatedMinutes = templateTask.estimatedMinutes,
                    order = maxOrder + index + 1
                )
                taskRepository.addTask(task)
            }
        }
    }
    
    // Create 3 default templates on first launch
    fun initializeDefaultTemplates() {
        viewModelScope.launch {
            if (templates.value.isEmpty()) {
                createTemplate(
                    name = "Morning Routine",
                    description = "Start your day right",
                    icon = "☀️",
                    tasks = listOf(
                        TemplateTask(templateId = 0, title = "Meditation", estimatedMinutes = 10, order = 0),
                        TemplateTask(templateId = 0, title = "Exercise", estimatedMinutes = 30, order = 1),
                        TemplateTask(templateId = 0, title = "Healthy Breakfast", estimatedMinutes = 15, order = 2)
                    )
                )
                // ... more default templates
            }
        }
    }
}
```

---

### 🎨 **Layer 5: UI (Screens)** (`ui/screens/`)

This is where the magic happens! Compose lets you build UI with Kotlin code (no XML).

#### **MainActivity.kt** - App entry point with navigation

```kotlin
@Composable
fun HelmutApp() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "today",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("today") { TaskListScreen() }
            composable("templates") { TemplatesScreen() }
            composable("history") { HistoryScreen() }
        }
    }
}
```

**What this does:**
- `Scaffold` → Material Design layout structure (gives you top bar, bottom bar, content area)
- `NavigationBar` → Bottom nav with 3 tabs
- `NavHost` → Container that switches between screens
- `composable("today") { TaskListScreen() }` → Route "today" shows TaskListScreen

#### **TaskListScreen.kt** - Today's tasks

```kotlin
@Composable
fun TaskListScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerManager.timerState.collectAsState()
    
    var showAddTask by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Today") })
        
        // Add task form
        AnimatedVisibility(visible = showAddTask) {
            Card {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task") }
                )
                Button(onClick = {
                    viewModel.addTask(taskTitle, taskMinutes.toInt())
                    showAddTask = false
                }) {
                    Text("Add Task")
                }
            }
        }
        
        // Active tasks list
        LazyColumn {
            items(uiState.activeTasks) { task ->
                TaskItem(
                    task = task,
                    onStart = { viewModel.startTask(task) },
                    onComplete = { viewModel.completeTask(task) }
                )
            }
        }
        
        // Floating Action Button
        FloatingActionButton(onClick = { showAddTask = !showAddTask }) {
            Icon(Icons.Default.Add, "Add Task")
        }
    }
}
```

**Key Compose concepts:**
- `@Composable` → Function that builds UI
- `remember { }` → Saves state across recompositions
- `by viewModel.uiState.collectAsState()` → Convert Flow to Compose State
- When `uiState` changes → UI automatically rebuilds (recomposes)
- `LazyColumn` → Like RecyclerView (efficient scrolling list)
- `items(list) { }` → Render each item

#### **TemplatesScreen.kt** - Browse templates

```kotlin
@Composable
fun TemplatesScreen(viewModel: TemplateViewModel = hiltViewModel()) {
    val templates by viewModel.templates.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.initializeDefaultTemplates()
    }
    
    Column {
        TopAppBar(title = { Text("Templates") })
        
        LazyColumn {
            items(templates) { template ->
                TemplateCard(
                    template = template,
                    onAddToToday = { viewModel.addTemplateToToday(template) },
                    onDelete = { viewModel.deleteTemplate(template.template) }
                )
            }
        }
        
        FloatingActionButton(onClick = { showCreateDialog = true }) {
            Icon(Icons.Default.Add, "Create Template")
        }
    }
    
    if (showCreateDialog) {
        CreateTemplateDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description, icon, tasks ->
                viewModel.createTemplate(name, description, icon, tasks)
                showCreateDialog = false
            }
        )
    }
}
```

**LaunchedEffect(Unit)** → Runs once when screen appears (like `onCreate`)

#### **TemplateCard.kt** - Individual template display

```kotlin
@Composable
fun TemplateCard(
    template: TemplateWithTasks,
    onAddToToday: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column {
            // Header
            Row {
                Text(text = template.template.icon, style = MaterialTheme.typography.headlineMedium)
                Column {
                    Text(text = template.template.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = template.template.description, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
            
            Divider()
            
            // Task list
            template.tasks.forEach { task ->
                Row {
                    Text("• ${task.title}")
                    Text("${task.estimatedMinutes} min")
                }
            }
            
            // Total time
            val totalMinutes = template.tasks.sumOf { it.estimatedMinutes }
            Text("Total: $totalMinutes minutes")
            
            // Add to today button
            Button(onClick = onAddToToday) {
                Text("Add to Today")
            }
        }
    }
}
```

#### **HistoryScreen.kt** - Completed tasks + stats

```kotlin
@Composable
fun HistoryScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val completedTasks by viewModel.uiState.collectAsState()
    
    Column {
        TopAppBar(title = { Text("History") })
        
        // Stats cards
        Row {
            StatCard(title = "Total", value = completedTasks.completedTasks.size.toString())
            StatCard(title = "Focus Time", value = "${totalMinutes}m")
            StatCard(title = "Streak", value = "${streak}d")
        }
        
        // Completed tasks grouped by date
        val groupedTasks = completedTasks.completedTasks.groupByDate()
        
        LazyColumn {
            groupedTasks.forEach { (date, tasks) ->
                item {
                    Text(formatDate(date))  // "Today", "Yesterday", "Monday, Nov 20"
                }
                items(tasks) { task ->
                    CompletedTaskCard(task = task)
                }
            }
        }
    }
}
```

**Streak calculation:**
Counts consecutive days with at least one completed task.

---

### ⚙️ **Utils** (`utils/`)

#### **TimerManager.kt** - Countdown timer

```kotlin
class TimerManager(private val scope: CoroutineScope) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState
    
    fun startTimer(minutes: Int, onComplete: () -> Unit) {
        val totalSeconds = minutes * 60
        _timerState.value = TimerState(
            totalSeconds = totalSeconds,
            remainingSeconds = totalSeconds,
            isRunning = true
        )
        
        scope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)  // Wait 1 second
                
                if (!_timerState.value.isPaused) {
                    _timerState.value = _timerState.value.copy(
                        remainingSeconds = _timerState.value.remainingSeconds - 1
                    )
                }
            }
            
            if (_timerState.value.remainingSeconds == 0) {
                onComplete()
            }
        }
    }
    
    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isPaused = true)
    }
    
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
}

data class TimerState(
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false
)
```

**How it works:**
1. `startTimer()` creates a coroutine loop
2. Every second, decrement `remainingSeconds`
3. UI observes `timerState` and updates countdown display
4. When reaches 0, call `onComplete()`

---

### 🔌 **Dependency Injection** (`di/`)

#### **DatabaseModule.kt**

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
        )
        .fallbackToDestructiveMigration()  // Delete & recreate DB on version change
        .build()
    }
    
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    fun provideTemplateDao(database: AppDatabase): TemplateDao {
        return database.templateDao()
    }
}
```

**What Hilt does:**
- When ViewModel needs `TaskRepository`, Hilt creates it
- When `TaskRepository` needs `TaskDao`, Hilt creates it
- When `TaskDao` needs `AppDatabase`, Hilt creates it
- All automatic! Just add `@Inject constructor()`

---

### 🔑 **Key Kotlin Concepts**

#### **1. Null Safety**
```kotlin
val name: String = "Helmut"       // Cannot be null
val name: String? = null          // Can be null (? = nullable)
val length = name?.length         // Safe call: returns null if name is null
val length = name?.length ?: 0    // Elvis operator: default to 0 if null
```

#### **2. Data Classes**
```kotlin
data class Task(val title: String, val minutes: Int)
// Auto-generates: equals(), hashCode(), toString(), copy()

val task1 = Task("Write code", 30)
val task2 = task1.copy(minutes = 45)  // Copy with changes
```

#### **3. Extension Functions**
```kotlin
fun Int.formatTime(): String {
    val mins = this / 60
    val secs = this % 60
    return String.format("%02d:%02d", mins, secs)
}

val time = 125.formatTime()  // "02:05"
```

#### **4. Lambda Functions (Anonymous Functions)**
```kotlin
// Traditional function
fun add(a: Int, b: Int): Int {
    return a + b
}

// Lambda
val add = { a: Int, b: Int -> a + b }

// Passing lambda to function
Button(onClick = { viewModel.addTask("Task", 10) }) {
    Text("Add")
}
```

#### **5. Coroutines (Background Work)**
```kotlin
// suspend = can pause and resume
suspend fun fetchData(): String {
    delay(1000)  // Wait 1 second (doesn't block thread)
    return "Data"
}

// Launch coroutine
viewModelScope.launch {
    val data = fetchData()  // Runs in background
    println(data)
}
```

#### **6. Flow (Reactive Streams)**
```kotlin
flow {
    emit(1)
    delay(1000)
    emit(2)
}.collect { value ->
    println(value)  // Prints 1, then 2 after 1 second
}
```

#### **7. Sealed Classes (Restricted Inheritance)**
```kotlin
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
}

when (result) {
    is Result.Success -> println(result.data)
    is Result.Error -> println(result.message)
}
```

---

### 📊 **How Data Flows Through the App**

```
┌─────────────────────────────────────────────────────┐
│                    USER ACTION                       │
│         (User taps "Add Task" button)                │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│                   UI LAYER                           │
│              TaskListScreen.kt                       │
│   Button(onClick = { viewModel.addTask("Task", 10) })│
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│                 VIEWMODEL LAYER                      │
│               TaskViewModel.kt                       │
│   fun addTask(title: String, minutes: Int) {         │
│       viewModelScope.launch {                        │
│           repository.addTask(Task(...))              │
│       }                                              │
│   }                                                  │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│                REPOSITORY LAYER                      │
│              TaskRepository.kt                       │
│   suspend fun addTask(task: Task) =                  │
│       taskDao.insertTask(task)                       │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│                  DATABASE LAYER                      │
│                   TaskDao.kt                         │
│   @Insert                                            │
│   suspend fun insertTask(task: Task): Long           │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│                  ROOM DATABASE                       │
│              (SQLite database file)                  │
│   INSERT INTO tasks (title, estimatedMinutes, ...)  │
└─────────────────────────────────────────────────────┘
                           ↓
                  DATABASE CHANGED!
                           ↓
┌─────────────────────────────────────────────────────┐
│              FLOW AUTO-UPDATES                       │
│   TaskDao.getActiveTasks(): Flow<List<Task>>        │
│              emits new list                          │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│            VIEWMODEL OBSERVES FLOW                   │
│   repository.getActiveTasks().collect { tasks ->    │
│       _uiState.value = _uiState.value.copy(          │
│           activeTasks = tasks                        │
│       )                                              │
│   }                                                  │
└─────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│              UI OBSERVES STATE                       │
│   val uiState by viewModel.uiState.collectAsState()  │
│                                                      │
│   LazyColumn {                                       │
│       items(uiState.activeTasks) { task ->           │
│           TaskItem(task)  ← RECOMPOSES!              │
│       }                                              │
│   }                                                  │
└─────────────────────────────────────────────────────┘
                           ↓
                  UI UPDATES ON SCREEN!
```

**The beauty:** After you add a task, you don't manually update the UI. The Flow automatically emits the new list, ViewModel updates state, and Compose redraws the screen. **Reactive programming!**

---

### 🧪 **Testing**

#### Run Unit Tests
```bash
./gradlew test
```

#### Run Instrumented Tests (requires emulator/device)
```bash
./gradlew connectedAndroidTest
```

#### Generate Code Coverage Report
```bash
./gradlew jacocoTestReport
```
View report at: `app/build/reports/jacoco/html/index.html`

---

### 🔧 **Troubleshooting**

#### **Build fails with "Unable to locate a Java Runtime"**

**Solution 1:** Use Android Studio's bundled JDK
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug -x lint
```

**Solution 2:** Install JDK 11 or higher
- macOS: `brew install openjdk@11`
- Windows: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/)

#### **Build fails with "SDK location not found"**

Create `local.properties` file in project root:
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

Replace `YOUR_USERNAME` with your actual username.

#### **Database version conflict errors**

The app uses `.fallbackToDestructiveMigration()` which means it will delete and recreate the database when the schema changes. This is fine for development but **don't use in production** (you'll lose user data!).

For production, create proper migrations:
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE templates ...")
    }
}

Room.databaseBuilder(...)
    .addMigrations(MIGRATION_1_2)
    .build()
```

#### **Notifications not showing when timer completes**

1. **Check notification permissions:** Go to Settings → Apps → Helmut → Notifications → Enable
2. **Check in-app settings:** Open Settings tab in the app and ensure "Notifications" toggle is ON
3. **Battery optimization:** Some Android devices kill background processes. Go to Settings → Battery → Battery Optimization → Helmut → Don't optimize

#### **Vibration not working**

1. **Check Settings tab:** Ensure "Vibration" is enabled in the app
2. **Check phone settings:** Make sure your phone isn't in silent mode (some devices disable vibration in silent mode)
3. **Check permissions:** Verify VIBRATE permission in AndroidManifest.xml (already added)

#### **No sound when timer completes**

1. **Check volume:** Make sure notification volume is turned up
2. **Check Settings tab:** Try selecting a different notification sound (Default/Alarm/Ringtone)
3. **Do Not Disturb mode:** Turn off DND mode temporarily to test

---

### 📚 **Learning Resources**

#### **For Kotlin Beginners:**
- [Official Kotlin Docs](https://kotlinlang.org/docs/getting-started.html)
- [Kotlin Koans](https://play.kotlinlang.org/koans) - Interactive exercises
- [Kotlin by Example](https://play.kotlinlang.org/byExample/overview)

#### **For Android Developers:**
- [Android Developers Guide](https://developer.android.com/guide)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Codelabs (hands-on tutorials)](https://developer.android.com/courses)

#### **For This Codebase:**
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow Guide](https://kotlinlang.org/docs/flow.html)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore)
- [Android Notifications](https://developer.android.com/develop/ui/views/notifications)

---

### ❓ **FAQ**

**Q: Why use Room instead of raw SQLite?**  
A: Room provides compile-time verification of SQL queries, automatic object mapping, and Flow support for reactive UIs. Less boilerplate, fewer bugs!

**Q: What's the difference between `val` and `var`?**  
A: `val` is immutable (like `final` in Java), `var` is mutable. Prefer `val` when possible (functional programming style).

**Q: Why use Hilt/Dagger?**  
A: It handles dependency creation and injection automatically, making code more testable and reducing boilerplate. You don't have to manually pass dependencies everywhere.

**Q: What's a StateFlow vs LiveData?**  
A: Both are observable data holders. StateFlow is Kotlin-first, works better with coroutines, has a clearer API, and is the recommended choice for new projects.

**Q: How does Compose differ from XML layouts?**  
A: Compose is declarative (describe what UI should look like) vs XML's imperative approach (manually update views). Compose has less boilerplate, better Kotlin integration, and enables powerful UI patterns.

**Q: What's the `by` keyword?**  
A: Kotlin delegation. `val state by viewModel.state.collectAsState()` automatically unwraps the State object so you can use `state` directly instead of `state.value`.

**Q: What does `suspend` mean?**  
A: Marks a function as "suspendable" - it can pause execution and resume later without blocking the thread. Used for async operations like network calls or database queries.

**Q: What's a `Flow` vs regular `List`?**  
A: `List` is static data. `Flow` is a stream that can emit multiple values over time. Perfect for live database updates!

**Q: What's DataStore vs SharedPreferences?**  
A: DataStore is the modern replacement for SharedPreferences. It's type-safe, asynchronous (uses coroutines), handles errors better, and supports Flow for reactive updates.

**Q: How do notifications work in Android?**  
A: The app uses NotificationCompat to create notifications that work across all Android versions. You need to:
1. Request POST_NOTIFICATIONS permission (Android 13+)
2. Create a NotificationChannel (defines importance, sound, vibration)
3. Build the notification with NotificationCompat.Builder
4. Display it with NotificationManager

**Q: Why are there so many layers? Seems complicated!**  
A: Separation of concerns! Each layer has a single responsibility:
- **Models**: Define data structure
- **DAO**: Database operations
- **Repository**: Abstract data source
- **ViewModel**: Business logic
- **UI**: Display & user interaction

This makes testing easier, code more maintainable, and allows swapping implementations (e.g., mock data for testing).

**Q: Can I use this code in my own app?**  
A: Yes! This project is MIT licensed. Feel free to fork, modify, and use it however you want.

---

### 🚀 **Next Steps for Learning**

1. **Experiment**: Change colors, add new fields to Task, modify UI
2. **Add features**: 
   - Task categories/tags
   - Dark mode toggle
   - Export tasks to CSV
   - Task priority levels
   - Recurring tasks
   - Custom notification sounds (upload your own)
   - Widget for quick task access
3. **Read the official docs** for each technology
4. **Build your own app** using this as a template

---

### 🤝 **Contributing**

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

#### Ideas for Contributions

- 📸 Add screenshots to the README
- 🌙 Implement dark mode toggle
- 🏷️ Add task categories/tags
- 📤 Export tasks to CSV
- 🔁 Recurring tasks support
- 🎨 More theme customizations
- 🌍 Internationalization (i18n)
- 🧪 Write more unit tests
- 📖 Improve documentation
- 🐛 Fix bugs and improve performance

Please read our [Contributing Guidelines](CONTRIBUTING.md) before contributing.

---

### 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

### 🙏 **Acknowledgments**

- Inspired by [LlamaLife](https://llamalife.co/)
- Built with love for the Android community
- Special thanks to all open-source contributors

---

**Built with ❤️ using Kotlin and Jetpack Compose**
