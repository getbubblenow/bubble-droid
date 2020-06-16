package com.getbubblenow.android.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment

fun Context.resolveAttribute(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

fun Fragment.requireTargetFragment(): Fragment {
    return requireNotNull(targetFragment) { "A target fragment should always be set for $this" }
}
