package com.example.base_clean_architecture.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

object SerializationUtil {
    val json = Json {
        isLenient = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
}

inline fun <reified T> String?.deserialization(default: T): T {
    return this?.let {
        try {
            SerializationUtil.json.decodeFromString(it)
        } catch (e: Exception) {
            null
        }
    } ?: default
}

inline fun <reified T> T?.serialization(default: String = ""): String {
    return this?.let {
        try {
            SerializationUtil.json.encodeToString(it)
        } catch (e: Exception) {
            null
        }
    } ?: default
}

fun String?.getJsonElement(): JsonElement {
    return this?.let {
        try {
            SerializationUtil.json.parseToJsonElement(it)
        } catch (e: Exception) {
            null
        }
    } ?: JsonNull
}

fun JsonElement?.getFloat(key: String, default: Float = 0f): Float {
    return try {
        this?.jsonObject?.get(key)?.jsonPrimitive?.float
    } catch (e: Exception) {
        null
    } ?: default
}

fun JsonElement?.getBoolean(key: String, default: Boolean = false): Boolean {
    return try {
        this?.jsonObject?.get(key)?.jsonPrimitive?.boolean
    } catch (e: Exception) {
        null
    } ?: default
}

fun JsonElement?.getStringList(key: String, default: List<String> = listOf()): List<String> {
    return try {
        this?.jsonObject?.get(key)?.jsonArray?.map {
            it.jsonPrimitive.content
        }
    } catch (e: Exception) {
        null
    } ?: default
}

fun JsonElement?.asOneDimensionAnyTypeMap(default: Map<String, Any> = mapOf()): Map<String, Any> {
    return try {
        this?.jsonObject?.entries?.associate {
            val jsonValue = it.value
            it.key to when (jsonValue) {
                is JsonPrimitive -> jsonValue.content
                is JsonObject, is JsonArray -> jsonValue.toString()
                else -> ""
            }
        }
    } catch (e: IllegalArgumentException) {
        null
    } ?: default
}

fun JsonElement?.getString(key: String, default: String = ""): String {
    return try {
        this?.jsonObject?.get(key)?.jsonPrimitive?.content
    } catch (e: Exception) {
        null
    } ?: default
}
