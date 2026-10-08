Kitabu - Library Book Rental & Reservation System

Kitabu is a Android application built with Kotlin, Jetpack Compose (Material 3), and Room Database. It a library book rental and reservation system featuring full CRUD lifecycle operations (browse, search, reserve, renew, return, and cancel).

Features

Book Catalog Screen
- Catalog View: Responsive list showcasing available and borrowed books.
- Availability Badging: Visual status pills—Green for "Available"** and Red for "Borrowed".
- Dynamic Search: Live search widget filtering books by title or author.
- Reservation Dialog: Modal dialog allowing users to choose rental duration (7, 14, or 21 days) and reserve books.

User Reservations Dashboard
- Active Rentals View: Dedicated dashboard listing active bookings.
- Rental Info Display: Displays book title, reservation date, return deadline, and remaining days (flagged as "Overdue" if past deadline).
- Lifecycle Action Controls:
  - Renew: Extends rental deadline by 7 days.
  - Return: Marks booking as returned and frees up book availability in real-time.
  - Cancel: Cancels pending reservations prior to pickup with confirmation dialog.

Architecture & Tech Stack

Tech Stack
- Language: Kotlin
- UI Framework: Jetpack Compose with Material 3
- Navigation: Jetpack Navigation Compose (`NavHost`, `composable`, `NavController`)
- Database: Room Database with KSP annotation processing
- Asynchronous Execution: Kotlin Coroutines (`viewModelScope.launch`) & `Flow` / `StateFlow`
- Minimum SDK: API 26 (Android 8.0)
- Target SDK: API 36

Architecture Pattern (MVVM)
- UI Layer: Compose screens (`CatalogScreen`, `ReservationsScreen`) observing ViewModel state via `collectAsState()`.
- ViewModel Layer: `BookViewModel` and `BookingViewModel` managing UI states and coroutine dispatching.
- Repository Layer: `BookRepository` and `BookingRepository` abstracting data sources.
- Data Layer: Room DAOs (`BookDao`, `BookingDao`), Entities (`BookEntity`, `BookingEntity`), and Type Converters (`Converters`).

Database Schema
BookEntity (`books` table)
| Column | Type | Constraints | Description |
|---|---|---|---|
| `bookId` | `Int` | Primary Key, Auto-generate | Unique ID for book |
| `title` | `String` | Not Null | Book title |
| `author` | `String` | Not Null | Book author |
| `category` | `String` | Not Null | Book category |
| `isAvailable` | `Boolean` | Default: `true` | Availability status |

BookingEntity (`bookings` table)
| Column | Type | Constraints | Description |
|---|---|---|---|
| `bookingId` | `Int` | Primary Key, Auto-generate | Unique ID for booking |
| `bookOwnerId` | `Int` | Foreign Key (`books.bookId`), CASCADE | Reference to `BookEntity` |
| `userName` | `String` | Not Null | User identifier |
| `bookingDate` | `Long` | Timestamp | Date of booking creation |
| `returnDeadline` | `Long` | Timestamp | Return deadline date |
| `status` | `BookingStatus` | Enum (`PENDING`, `ACTIVE`, `RETURNED`) | Current booking status |

Project Structure
com.example.kitabu
├── data
│   ├── local
│   │   ├── BookDao.kt
│   │   ├── BookEntity.kt
│   │   ├── BookingDao.kt
│   │   ├── BookingEntity.kt
│   │   ├── BookingStatus.kt
│   │   ├── Converters.kt
│   │   └── KitabuDatabase.kt
│   └── repository
│       ├── BookRepository.kt
│       └── BookingRepository.kt
├── viewmodel
│   ├── BookViewModel.kt
│   ├── BookingViewModel.kt
│   └── ViewModelFactory.kt
└── MainActivity.kt


Setup & Installation

1. Prerequisites:
   - Android Studio (Ladybug or newer)
   - JDK 17 or higher
   - Android Emulator or physical device running Android API 26+

2. Steps:
3. bash
   git clone <repository-url>
   - Open Android Studio and choose Open Project.
   - Select the `Kitabu` root folder.
   - Allow Gradle to sync dependencies.
   - Run the app on your emulator or connected Android device (`API 26+`).

Verification & Quality Standards
- Zero Main Thread DB Access: All Room DB calls run asynchronously within `viewModelScope.launch`.
- Pure Jetpack Compose UI: 100% Compose UI without legacy XML layouts.
- Reactive UI: Immediate badge and dashboard status updates powered by Kotlin `Flow`.
