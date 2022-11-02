package com.example.stepcountingapplication


import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson


class Prefs {

    private val STEPS_DATA = "STEPS_DATA"

    private var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(MainApplication.get().applicationContext)

    init {
        instance = this
    }

    val gson = Gson()


    companion object {
        private var instance: Prefs? = null
        fun init(): Prefs {
            if (instance == null) {
                instance = Prefs()
            }
            return instance!!
        }
    }


    fun clear() {
        sharedPreferences.edit().clear().apply()
    }




    var stepData: StepsData?
        get() {
            val str = sharedPreferences.getString(STEPS_DATA, "") ?: ""
            if (!str.isBlank()) return gson.fromJson(str, StepsData::class.java)
            return null
        }
        set(value) {
            sharedPreferences.edit().putString(STEPS_DATA, gson.toJson(value)).apply()
        }



}