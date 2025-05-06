# Mental Wellness App - Music Module with Firebase Integration

This document outlines the Firebase integration for the music module in the Mental Wellness Android app.

## Firebase Components 

### Authentication
- Uses Firebase Authentication to manage user accounts
- User must be authenticated to create and manage playlists

### Firestore
- Stores user playlists under `users/{userId}/playlists/{playlistId}`
- Playlist document structure:
  ```json
  {
    "name": "Playlist Name",
    "colorTheme": "blue", // blue, yellow, purple, green, red, teal
    "trackCount": 3,
    "createdAt": Timestamp,
    "songs": [
      {
        "songTitle": "Song Title",
        "artistName": "Artist Name",
        "songUrl": "path/to/song.mp3",
        "addedAt": Timestamp
      },
      // More songs...
    ]
  }
  ```

### Storage
- Stores music files in Firebase Storage under `music/{filename}`
- URLs are retrieved at runtime for streaming

## Key Classes

### Model Classes
- `Song.java` - Model for song metadata
- `UserPlaylist.java` - Model for user playlist with Firebase integration

### Utility Classes
- `FirebasePlaylistManager.java` - Singleton for managing Firebase operations
- `MediaPlayerManager.java` - Singleton for managing audio playback

### Fragments
- `MusicMainFragment.java` - Main music UI with user playlists section
- `UserPlaylistFragment.java` - Fragment for displaying a user playlist's songs
- `FirebasePlayerFragment.java` - Player fragment for streaming music from Firebase
- `CreatePlaylistDialogFragment.java` - Dialog for creating a new playlist

## How It Works

1. **User Authentication**:
   - Users must sign in to access their playlists
   - Login state is checked before playlist operations

2. **Creating Playlists**:
   - User creates a playlist via the dialog
   - Playlist is saved to Firestore with Firebase-generated ID

3. **Displaying Playlists**:
   - User playlists are loaded from Firestore on startup
   - Real-time listener updates UI when playlists change

4. **Playing Music**:
   - Songs are streamed directly from Firebase Storage
   - MediaPlayer handles audio streaming and playback controls

## Setting Up Firebase

1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the project
3. Download `google-services.json` and place it in the app directory
4. Enable Authentication, Firestore, and Storage services
5. Set up Firestore security rules to protect user data:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      match /playlists/{playlistId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

6. Set up Storage security rules:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /music/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.token.admin == true;
    }
  }
}
```

## Adding Songs

For admin users to add songs to Firebase Storage:
1. Upload MP3 files to the `music/` folder in Firebase Storage
2. Add songs to a user's playlist by adding the song object to the songs array
3. The song URL should point to the file path in Storage (e.g., `music/song.mp3`)

## Future Improvements

- Add ability for users to add songs to playlists from a central music library
- Implement playlist sharing between users
- Add song favorites and play history tracking
- Add background playback service for continuous playback
- Implement download feature for offline listening 