# FinCord Android

Discord-inspired Android UI foundation with a FinCord plugin/settings hub.

## Included in this version
- Discord-like dark navigation, channel list and chat placeholder
- FinCord settings grouped into categories, with Enable/Disable switches
- Badge catalog for local previews (does not grant Discord badges)
- Music integrations page placeholders (Spotify, YouTube Music, Apple Music, SoundCloud)
- Audio file picker and local audio library list
- Wallpaper image picker
- GitHub Actions workflow to compile and upload a debug APK artifact

## Important limitations
This is not yet a connected Discord client. Discord OAuth2, live server/channel sync, messaging and sending attachments are not implemented.
- Deleted message logs require a server administrator to install an authorized bot with appropriate permissions/intents. Only messages observed before deletion can be logged.
- Official Discord badges cannot be added arbitrarily to an account. Badge page is a local catalog/preview.
- Music status depends on each service's API/OAuth terms and Discord's supported integrations. Not all Android apps expose playback to third-party apps.
- Audio import currently creates a local list only. Sending attachments and native voice-message uploads require a supported official Discord integration/API.
- Do not enter user account tokens or automate a user account (self-bot). The app does not extract/store Discord tokens or spoof PC/VR/mobile client identity.

## Build APK using GitHub
1. Create a repository named `FinCord`.
2. Upload the contents of this folder to the repository root.
3. Open **Actions** and run **Build FinCord APK** (or push to `main`).
4. Download `FinCord-debug-apk` from the workflow run's Artifacts section.

The APK is a debug build. It is not signed for Play Store release.
