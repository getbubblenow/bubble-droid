package com.getbubblenow.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.WgQuickBackend
import com.getbubblenow.android.util.ExceptionLoggers

class BootShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Application.getBackendAsync().thenAccept { backend: Backend? ->
            if (backend !is WgQuickBackend) return@thenAccept
            val action = intent.action ?: return@thenAccept
            val tunnelManager = Application.getTunnelManager()
            if (Intent.ACTION_BOOT_COMPLETED == action) {
                Log.i(TAG, "Broadcast receiver restoring state (boot)")
                tunnelManager.restoreState(false).whenComplete(ExceptionLoggers.D)
            } else if (Intent.ACTION_SHUTDOWN == action) {
                Log.i(TAG, "Broadcast receiver saving state (shutdown)")
                tunnelManager.saveState()
            }
        }
    }

    companion object {
        private const val TAG = "WireGuard/BootShutdownReceiver"
    }
}
