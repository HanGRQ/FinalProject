# Final Year Project

**FoodMind —— Android-based nutrition and emotional behavior tracking system**

## Overview

FoodMind is an innovative Android application designed to help users track their nutritional intake, analyze food consumption habits, and understand the emotional connection between food and mood. With unique features such as barcode scanning, personalized dietary recommendations, and emotional analysis, FoodMind aims to bridge the gap between traditional calorie tracking and holistic well-being.

## Features

- **Barcode Scanning**: Quickly scan food barcodes to retrieve nutritional details, powered by the OpenFoodFacts API.
- **Nutritional Analysis**: Detailed insights into macronutrient content, including energy, carbohydrates, fats, proteins, and sodium.
- **Emotional Tracking**: Log your mood and correlate it with eating habits for better understanding and behavioral adjustments.
- **Personalized Recommendations**: Adaptive suggestions based on nutritional intake and mood patterns.
- **Custom Food Entry**: Manually add foods that are not available in the database.
- **Offline Functionality**: Track and analyze data without requiring an internet connection.
- **Trend Analysis Reports**: Visual representation of dietary patterns and emotional trends over time.

## Target Audience

1. **Health-Conscious Individuals**: People aiming to monitor and improve their dietary habits.
2. **Emotional Eaters**: Users seeking to understand the emotional triggers of their eating behaviors.
3. **Fitness Enthusiasts**: Individuals looking to balance nutrition with fitness goals.

## Installation

1. Clone this repository:

   ```
   git clone https://github.com/yourusername/FoodMind.git
   ```

2. Open the project in Android Studio.

3. Ensure you have the following installed:

   - Android Studio (latest version)
   - Kotlin 1.9.10
   - JDK 11

4. Build and run the project on an Android emulator or physical device.

## Tech Stack

- **Programming Languages**: Kotlin, Java
- **UI Framework**: Jetpack Compose
- **Database**: SQLite (dynamically created tables for storing food and emotional data)
- **APIs**: OpenFoodFacts API for food data retrieval
- **Charting Library**: MPAndroidChart for visualizing trends
- **Barcode Scanning**: ML Kit Barcode Scanning API

## Usage

1. **Scanning Food**:
   - Open the Scan Screen.
   - Use the camera to scan a food barcode.
   - If the food is found, nutritional details will be displayed.
   - If not found, you can manually add the food details.
2. **Tracking Mood**:
   - Log your mood before or after eating.
   - Observe emotional trends in the analysis reports.
3. **Viewing Reports**:
   - Access detailed graphs and trends showing how mood and nutrition correlate over time.

## Data Model

### FoodDetails Table

- **barcode** (TEXT, Primary Key)
- **name** (TEXT)
- **spec** (TEXT)
- **unit** (TEXT)
- **price** (REAL)
- **brand** (TEXT)
- **supplier** (TEXT)
- **madeIn** (TEXT)
- **createdAt** (TEXT)
- **updatedAt** (TEXT)
- **deletedAt** (TEXT)