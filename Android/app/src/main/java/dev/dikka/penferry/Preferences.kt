package dev.dikka.penferry

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    private lateinit var prefs: SharedPreferences

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences("penferry", Context.MODE_PRIVATE)
    }

    private const val PREF_ADDRESS_KEY = "penferry.address"
    var address: String
        get() = prefs.getString(PREF_ADDRESS_KEY, "192.168.0.2") ?: "192.168.0.2"
        set(value) = prefs.edit().putString(PREF_ADDRESS_KEY, value).apply()

    private const val PREF_PORT_KEY = "penferry.port"
    var port: Int
        get() = prefs.getInt(PREF_PORT_KEY, 17420)
        set(value) = prefs.edit().putInt(PREF_PORT_KEY, value).apply()
}