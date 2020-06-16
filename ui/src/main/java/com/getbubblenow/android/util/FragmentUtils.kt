package com.getbubblenow.android.util

import android.view.ContextThemeWrapper
import androidx.preference.Preference
import com.getbubblenow.android.activity.SettingsActivity

object FragmentUtils {
    fun getPrefActivity(preference: Preference): SettingsActivity {
        val context = preference.context
        if (context is ContextThemeWrapper) {
            if (context is SettingsActivity) {
                return context
            }
        }
        throw IllegalStateException("Failed to resolve SettingsActivity")
    }
}
