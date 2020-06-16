package com.getbubblenow.android.util

import android.util.Log
import java9.util.function.BiConsumer

/**
 * Helpers for logging exceptions from asynchronous tasks. These can be passed to
 * `CompletionStage.whenComplete()` at the end of an asynchronous future chain.
 */
enum class ExceptionLoggers(private val priority: Int) : BiConsumer<Any?, Throwable?> {
    D(Log.DEBUG), E(Log.ERROR);

    override fun accept(result: Any?, throwable: Throwable?) {
        if (throwable != null)
            Log.println(Log.ERROR, TAG, Log.getStackTraceString(throwable))
        else if (priority <= Log.DEBUG)
            Log.println(priority, TAG, "Future completed successfully")
    }

    companion object {
        private const val TAG = "WireGuard/ExceptionLoggers"
    }
}
