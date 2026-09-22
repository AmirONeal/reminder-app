Drop your two font files here with these exact names:

  Vazirmatn-Regular.ttf   <- Farsi/Persian font
  Roboto-Regular.ttf      <- English font

Both are free/open-source and downloadable from Google Fonts:
  https://fonts.google.com/specimen/Vazirmatn
  https://fonts.google.com/specimen/Roboto

Until you add them, the app runs fine and just uses the system default font
as a fallback (see FontProvider.kt).

Want a different Farsi font (e.g. Sahel, Yekan)? Just rename the file to
match, or update the filename in FontProvider.kt.
