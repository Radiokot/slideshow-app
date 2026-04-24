# Slideshow app

This is a [test assignment](https://github.com/vladimir-nvs/slideshow-task-android/blob/54ee326835a0ae6b302b244f333d70d176ea3b17/README.md) implementation for [Novisign](https://www.novisign.com/).

## Tech stack

- Kotlin 2.3.20
- Compose UI
- MVVM architecture
- Flow + coroutines for concurrency
- Koin dependency injection
- Room database
- Ktor + OkHTTP + Kotlin JSON serialization for networking
- ExoPlayer for video playback
- Landscapist for showing images

## Key points

- The code is in a single module, packaged by feature, and within each feature there are data, domain, and presentation packages if needed
- The majority of the app components live in a user session Koin scope, which holds the screen key. Sign out and sign in actions replace the session, and so the scope (see `KoinScopeUserSessionHolder`)
- Playlist data is cached in the local database and updated from the backend once a minute (see `CachedPlaylistRepository`)
- The app stores multiple versions of the same playlist but only plays the most recent version that is ready to play
- A playlist is ready to play once it has been prepared successfully, so all its media files (creatives) are downloaded (see `PlaylistPreparation`)
- When the playlist is playing, the user can skip the current item by a long press

## Build tools

- Android Studio Panda 3
- Java 21

## Assumptions

- Creative content type (image or video) – I assume it can be determined by the creativeKey extension. It is unlikely the server feeds raw user uploads to the player. To me, it seems more realistic that the server converts user uploads to the most widely supported formats (JPEG image, H264 video + AAC audio), therefore the extension from the key can be trusted
- Creative key – I assume the key points to an immutable file. The cache-control header for videos is set for 10 years, which to me further suggests that once I have a local copy of a file of a given key, I shouldn't ever re-download it
- Order key – seems important even though the items in the response are sorted accordingly
- Creative properties – if set, comma-separated key-value pairs separated with '='. I'm not sure if it's an attribute of a playlist item, or a creative itself. In the studio, I didn't find a way to set these properties neither in the Playlists nor in the Creatives views. I assume it's an attribute of a creative since it has the 'creative' prefix
- Video volume – if creative properties are set, the value of the 'soundVolume' property, which is a floating point percentage from 0 to at least 100
- Video looping – if the slide duration is greater than the video duration, it should be looped. If I were designing slides, I would like to have the ability to present a looped animation for an extensive period of time
- Playlists have no name in the response, although they can be named in the studio. Perhaps there's another endpoint to fetch named playlists, but I couldn't find it
- Modified – I assume the modified timestamp of a playlist item is changed each time it is put to another position, has its duration changed, etc. I assume that for the purpose of the app, the last modification time of the playlist is max(modified) of its items, since a playlist object itself has no other valuable fields
- Live playlist update – I think that if the playlist is updated, it must be prepared and played as soon as possible, because there could be an error in the data. The app shouldn't wait for the current playlist version to end before playing the updated version

## Caveats

IMO the implementation is good enough for a test assignment. However, if it was a production feature, the following caveats would have to be resolved:

- The UI is minimalistic
- Not much data loading progress is shown in the UI
- The available storage size is not checked before downloading creatives
- The storage is not cleaned up, outdated files and playlist versions remain
- The screen key is not saved into the storage
- The video player is being initialized just when it's time to play the video, which takes some time on the first launch
