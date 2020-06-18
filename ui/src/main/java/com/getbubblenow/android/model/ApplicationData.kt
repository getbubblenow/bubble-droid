package com.getbubblenow.android.model

import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.getbubblenow.android.BR
import com.getbubblenow.android.databinding.Keyed

class ApplicationData(val icon: Drawable, val name: String, val packageName: String, isSelected: Boolean) : BaseObservable(), Keyed<String> {
    override val key = name

    @get:Bindable
    var isSelected = isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}
