# Books Analyzer (Android)

![Android](https://img.shields.io/badge/platform-Android-green)
![Kotlin](https://img.shields.io/badge/language-Kotlin-black)

![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)
![Material 3](https://img.shields.io/badge/design-Material%203-teal)
![MVVM](https://img.shields.io/badge/architecture-MVVM-yellow)
![Room](https://img.shields.io/badge/database-Room-red)
![Hilt](https://img.shields.io/badge/DI-Hilt-purple)
![Coroutines](https://img.shields.io/badge/async-Coroutines-blue)
![Flow](https://img.shields.io/badge/reactive-Flow-black)

![Modern Android](https://img.shields.io/badge/Android-Modern%20Architecture-brightgreen)
![StateFlow](https://img.shields.io/badge/state-StateFlow-blue)
![Compose Navigation](https://img.shields.io/badge/navigation-Compose-yellow)

![Offline First](https://img.shields.io/badge/architecture-Offline--First-important)
![Min API](https://img.shields.io/badge/minSdk-24-brightgreen)
![License](https://img.shields.io/badge/license-MIT-purple)


Books Analyzer is an offline-first Android application designed to help readers and writers track, analyze, and reflect on their reading in a structured, private, and intentional way.

---

## Purpose

The primary goals of the app are:

* Track books in a personal library

* Analyze reading patterns and engagement

* Allow structured and honest private feedback on books

* Provide a reliable dataset for readers and writers to learn from their reading habits

The app is designed to work independently of external platforms such as Amazon or Goodreads, while still supporting search and discovery through public APIs.

## Project Philosophy

This app prioritizes:

* Stability over feature count

* Offline reliability

* User privacy and ownership of data

* Clear and maintainable architecture

* Practical, intentional feature design

No account is required. All data remains on the user's device.

Future sync and backup will be optional and user-controlled.

---

## Planned Features

### Status : Active development.

### MVP 1 - Personal Library

* 🌳 Search books via Google Books and Open Library APIs

* 🌳 Duplicate prevention and normalization

* 🌳 Incremental loading ("Show more")

* 🌳 Search history with suggestions

* 🌳 Offline-first storage using Room

* 🌳 Library view with filtering, sorting, and search

* 🪴 Book detail screen

* 🌳 Reading status management (Not started, Currently reading, Finished, DNF-ed)

* 🌳 Delete with undo support

* 🌱 Manual book addition

### MVP 2 – Reading sessions

* 🌰 Track reading sessions and duration

* 🌰 Analyze reading time per book

* 🌰 Reading habit insights

### MVP 3 – Notes and Analysis

* 🌰 Structured notes and private tags

* 🌰 Personal quality and engagement metrics

### Nice-to-have-s (To Be Planned)

* 🌰 Apply light / dark theme

* 🌰 Backup and restore

* 🌰 Optional cloud sync

* 🌰 Advanced analytics and insights

* 🌰 Data export

### Legend

```
🌰               → Seed, just an idea
🌱               → Seedling, taking root and developing
🪴               → Plant, developed but could use more work
🌳               → Tree, mostly developed (for now)
🪾               → Lifeless tree, sad and abandoned (for now)
```

---

## Architecture

The project follows modern Android development standards:

* Kotlin

* Jetpack Compose

* MVVM architecture

* Repository pattern

* Room (local DB)

* Hilt (DI)

* Kotlin Coroutines and Flow

* Offline-first design

### Structure overview:

```
data/           → Repositories
  local/        → Entities, DAOs
  remote/       → API clients (Google Books, Open Library)
di/             → Dependency injection modules
domain/         → Business logic, data models
ui/             → Screens, routes, composables
  nav/          → Navigation graph and routes
```
