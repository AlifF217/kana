# Kana - Japanese Language Learning Application

## 🛠️ Technologies Used
Kotlin - Primary programming language

Jetpack Compose - Modern UI toolkit for building native Android UI

Firebase Authentication - User authentication and account management

Firebase Firestore - NoSQL cloud database for storing user data and progress

Firebase Storage - Cloud storage for user profile pictures

Android SpeechRecognizer - Voice recognition for pronunciation practice

Coroutines - Asynchronous programming for smooth app performance

StateFlow/MutableStateFlow - Reactive state management

Material Design 3 - UI components and theming

## ✨ Features
### 🔐 Authentication
User registration with email and password

Secure login system

Password confirmation validation

## 📚 Core Learning Modules
<details> <summary><b>1. Flashcards</b></summary>
Interactive flashcard system with image-based learning

Multiple choice questions with shuffled options

Progress tracking with experience points (EXP)

Immediate feedback on answers

</details><details> <summary><b>2. Build-a-Sentence</b></summary>
Sentence construction practice

Japanese sentences with English translations

Word rearrangement game mechanics

Score tracking (correct/wrong attempts)

</details><details> <summary><b>3. Say It!</b></summary>
Pronunciation practice using speech recognition

Voice-to-text conversion

Japanese phrase training with romaji to katakana conversion

Real-time feedback on pronunciation

</details><details> <summary><b>4. Story Time</b></summary>
Reading comprehension practice through stories

Quiz-based learning with each story

Progress tracking and EXP rewards

Random question generation

</details><details> <summary><b>5. Letters</b></summary>
Learn Hiragana, Katakana, and basic Kanji

Interactive character grid

Sound playback for correct pronunciation

Tab-based navigation between character sets

</details>

## 👤 User Features

### Profile Management

Change profile picture from a selection of avatars

Update username, email, and password

Track experience points and levels

### Settings

Dark/Light mode toggle

Account management options

Theme preferences saved locally

### Feedback System

Submit feedback directly from the app

Store feedback in Firestore for review

### Share Progress

Share learning achievements with friends

Integration with Android share intent

## 📖 How to Use It
Getting Started
Register - Create a new account using your email and username

Login - Sign in with your registered credentials

Main Menu - Access all learning modules from the main dashboard

Learning Modules
Flashcards
View an image and select the correct Japanese word from four options

Track your progress with EXP points

Immediate feedback on correct/incorrect answers

Build-a-Sentence
Read the Japanese sentence

Arrange the English words in the correct order

Submit your answer and get instant feedback

Say It!
Select a Japanese phrase

Press the microphone button and speak the phrase

The app will convert your speech and check for correctness

Story Time
Read a Japanese story

Answer questions related to the story

Earn EXP points for correct answers

Letters
Switch between Hiragana, Katakana, and Kanji tabs

Tap on characters to hear their pronunciation

Visual feedback on character selection

Progress Tracking
Your experience points (EXP) are tracked across all modules

Level up as you earn more EXP

Progress is automatically saved to the cloud

## 🏗️ How It Is Built
Architecture
The app follows a modern Android architecture using:

MVVM Pattern - ViewModel components handle business logic

Repository Pattern - Data management through Firebase services

State Management - Using StateFlow for reactive UI updates

Composable Functions - Declarative UI with Jetpack Compose

Key Components
Authentication Flow
LoginViewModel.kt - Handles sign-in logic with Firebase Auth

RegisterViewModel.kt - Manages user registration and Firestore storage

Learning Modules
Each learning module follows a consistent pattern:

Data classes for content representation

Composable functions for UI rendering

Firebase integration for data persistence

State management for user progress

Firebase Integration
Authentication - FirebaseAuth for user management

Firestore - Stores user data, progress, and feedback

Storage - Hosts profile pictures and learning resources

## 📚 What I Learned
Technical Skills
Skill	Description
Jetpack Compose	Building responsive and dynamic UIs with declarative programming
Firebase Integration	Implementing authentication, real-time database, and cloud storage
State Management	Using StateFlow for reactive UI updates
Speech Recognition	Implementing voice-to-text functionality in Android
Coroutines	Handling asynchronous operations efficiently
MVVM Architecture	Structuring a clean and maintainable codebase
Development Practices
User-Centered Design - Creating features based on real user needs (UKM students)

Data Persistence - Implementing cloud-based storage for user progress

Mobile UX Design - Creating intuitive and accessible interfaces

Debugging and Testing - Ensuring app stability and performance

Project Management
Requirements Elicitation - Understanding user needs through research

System Design - Creating use case diagrams and system models

Iterative Development - Building and refining features based on feedback

## 🚀 How Could It Be Improved
### Features to Add or to change
□ Offline Support - Cache content for learning without internet

□ Social Features - Leaderboards and friend challenges

□ Daily Goals - Streak tracking and achievement system

□ Vocabulary Lists - Custom word lists for focused learning

□ Grammar Lessons - Structured grammar instruction based on JLPT

□ Speaking Practice - More advanced pronunciation exercises

□ a working profile setting - user can update or delete their account

□ Profile Picture update - user can upload their own profile picture instead of using the pre-existing one

### Technical Improvements
□ Jetpack Compose Navigation - Implement proper navigation components

□ More accurate text to speech input for say-it module - Implement an accurate voice recording to text when using the say-it module

□ Performance Optimization - Improve loading times and memory usage

□ Accessibility - Enhance support for users with disabilities

□ Push Notifications - Reminders for daily practice


## 📲 How to Install It
Prerequisites
Android Studio Arctic Fox or later

Android SDK with API level 26+

Firebase account (for backend services)

### Project File Installation
Installation Steps
1. Clone the Repository
bash
git clone https://github.com/AlifF217/kana.git
cd kana
2. Set Up Firebase
Create a new Firebase project at firebase.google.com

Register your Android app with the package name

Download the google-services.json file

Place it in the app/ directory of your project

3. Configure Firebase Services
Enable Email/Password authentication

Create Firestore database with appropriate security rules

Set up Firebase Storage for profile pictures

4. Open in Android Studio
Open the project in Android Studio

Sync Gradle files

Build the project

5. Run the App
Connect an Android device or start an emulator

Click the "Run" button in Android Studio

The app will be installed on your device

### Direct APK File Installation


6. Create a Test Account
Launch the app

Register a new account

Start learning Japanese!

### Troubleshooting
Issue	Solution
Firebase Connection Issues	Ensure google-services.json is correctly placed
Speech Recognition	Request microphone permissions on first use
Build Errors	Clean and rebuild the project
## 🎥 Video Demo
https://drive.google.com/file/d/1DydEUq7J3u97TMK6QcmwGM4ylRE4S2A9/view?usp=sharing

Click the image above to watch a full walkthrough of the Kana Japanese Learning App.

Demo Highlights:
✅ User registration and login process

✅ Navigating the main menu

✅ Using the flashcard learning module

✅ Building sentences practice

✅ Pronunciation practice with voice recognition

✅ Reading comprehension with Story Time

✅ Learning Japanese characters

✅ Profile management and settings

✅ Progress tracking and achievements

## 👥 Contributors
Name	Role
Alif Firdaus Azhar 	Lead Developer
Ridzuan Zainordin Project Manager
National University of Malaysia (UKM)	Project Sponsor
## 📄 License
This project is created for educational purposes as part of a university project at the National University of Malaysia.

## 🙏 Acknowledgments
National University of Malaysia for the project opportunity

All students who participated in the requirements gathering

Open source community for the amazing tools and libraries

<div align="center">
Happy Learning! がんばってください！

⬆ Back to Top

</div>
