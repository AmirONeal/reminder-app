# Reminder App

A native Android app (Kotlin) that:
- Lets you type your own custom messages (Farsi or English)
- Auto-detects Farsi vs English per message and applies the right font + text direction (RTL/LTR)
- Lets you set a background image per-message, or one default background for all messages
- Fires a random one of your messages a few times a day, at random times within an active window (default 9am–10pm)

## How to open it
1. Install [Android Studio](https://developer.android.com/studio) (free).
2. File → Open → select this `ReminderApp` folder.
3. Let Android Studio sync Gradle (it will auto-generate the Gradle wrapper on first sync).
4. Plug in your phone (with USB debugging on) or use an emulator, then hit ▶ Run.

## Adding the Farsi + English fonts
The app works out of the box with the system default font. For the real look:
1. Download **Vazirmatn** (Farsi) and **Roboto** (English) from Google Fonts.
2. Rename the files to `Vazirmatn-Regular.ttf` and `Roboto-Regular.ttf`.
3. Drop them in `app/src/main/assets/fonts/` (replacing the README there).
4. Rebuild — no code changes needed.

## Where things live
- `Message.kt` — your message model + the Farsi-detection logic (checks Unicode ranges)
- `MessageAdapter.kt` — applies the right font/direction per message in the list
- `ReminderWorker.kt` — builds and fires the actual notification, with the background image as a big-picture notification
- `Scheduler.kt` — each day, picks N random times within your active window and queues one notification job per time
- `SettingsActivity.kt` — change how many reminders per day (1–8) and your default background image
- `AddEditMessageActivity.kt` — add/edit a message and optionally pick its own background image

## Notes
- Backgrounds are picked from your phone's photo gallery (`ACTION_GET_CONTENT`), so any image on your device works — no need to bundle images in the app.
- The active hour window (9am–10pm) is currently fixed in `SettingsActivity.kt` — easy to expose as two time pickers if you want that adjustable too.
- This is a solid working scaffold, not a Play-Store-polished app — happy to help refine the UI, add a full Farsi settings screen, or add per-weekday scheduling if you want to take it further.
