package com.example.base_clean_architecture.data.data_source.local

interface Storage {
    fun setString(key: String, value: String)
    fun getString(key: String): String?
    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
}
