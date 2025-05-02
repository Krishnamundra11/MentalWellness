# Mental Wellness Data Generator

This script helps you populate your Firebase Firestore database with random test data for the Mental Wellness app. It creates 3 weeks of daily data (from April 20th, 2025 to the current date in 2025) for stress, academic, and sleep questionnaires.

## Prerequisites

- Node.js installed on your machine
- Firebase project with Firestore enabled
- Service account credentials for your Firebase project

## Setup Instructions

1. **Get Firebase Service Account Key**:
   - Go to the Firebase Console: https://console.firebase.google.com/
   - Select your project
   - Go to Project Settings > Service accounts
   - Click "Generate new private key"
   - Save the downloaded JSON file as `serviceAccountKey.json` in the same directory as this script

2. **Install Dependencies**:
   ```
   npm install
   ```

3. **Run the Script**:
   ```
   npm run generate
   ```

## What the Script Does

- Creates entries for stress, academic, and sleep questionnaires for each day
- Uses the format shown in the app's design with:
  - Random scores between 20-95
  - Random answers for 5 questions
  - Appropriate recommendations based on the score
- Document ID format: `{category}-{yyyy-mm-dd}` (e.g., `stress-2025-05-02`)

## Data Structure

Each document contains:
```json
{
  "category": "stress|academic|sleep",
  "date": "2025-05-02",
  "score": 60,
  "answers": {
    "q1": "Excellent",
    "q2": "Good",
    "q3": "Okay",
    "q4": "Poor",
    "q5": "Very Poor"
  },
  "recommendations": [
    "Take short breaks during work.",
    "Practice mindfulness for 5 minutes daily.",
    "Set realistic goals for yourself.",
    "Connect with friends regularly."
  ]
}
```

## Note

The script is designed to create data for the user ID: `hZTyOD0a7TVV9LtCZ6dCLjMgmxH3`. If you want to use a different user ID, change the `userId` variable in the script. 