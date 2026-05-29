package com.commons

import android.content.Context
import androidx.core.net.ParseException

object JsonReader {
    fun readJsonFromAssets(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }
}
