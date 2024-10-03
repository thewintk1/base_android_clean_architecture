package com.example.base_clean_architecture.data.data_source.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by Tim
 */

class AppPreManager @Inject constructor(@ApplicationContext context: Context) : Storage {

    companion object {
        const val SP_NAME = "BaseProject"
    }

    val appContext = context
    private val pref = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    override fun setString(key: String, value: String) {
        with(pref.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun getString(key: String): String? = pref.getString(key, null)

    override fun setBoolean(key: String, value: Boolean) {
        with(pref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        pref.getBoolean(key, defaultValue)

    fun clearData(){
        pref.edit().clear().apply()
    }
}

object PrefKey{
    const val KEY_BASE_URL = "pref_key_base_url"

}