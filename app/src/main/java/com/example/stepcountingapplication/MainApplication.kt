package com.example.stepcountingapplication

import android.app.Application


class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication
        fun get(): MainApplication = instance


    }




    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}