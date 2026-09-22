package com.example.reminderapp

import android.content.Context
import android.graphics.Typeface

/**
 * Central place for the two fonts the app uses. Fonts are loaded from
 * assets/fonts/ at runtime (see assets/fonts/README.txt for where to drop the
 * real .ttf files: Vazirmatn for Farsi, any Latin font for English).
 * Loading from assets (rather than res/font) means the project compiles fine
 * even before you've added the real font files - it just falls back to the
 * system font until then.
 */
object FontProvider {

    private var farsiCache: Typeface? = null
    private var englishCache: Typeface? = null

    fun farsiFont(context: Context): Typeface {
        farsiCache?.let { return it }
        return try {
            Typeface.createFromAsset(context.assets, "fonts/Vazirmatn-Regular.ttf").also { farsiCache = it }
        } catch (e: Exception) {
            Typeface.SANS_SERIF
        }
    }

    fun englishFont(context: Context): Typeface {
        englishCache?.let { return it }
        return try {
            Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf").also { englishCache = it }
        } catch (e: Exception) {
            Typeface.SANS_SERIF
        }
    }
}
