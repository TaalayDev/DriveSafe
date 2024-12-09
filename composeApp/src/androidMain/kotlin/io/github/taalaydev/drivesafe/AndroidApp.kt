package io.github.taalaydev.drivesafe

import android.app.Application

class AndroidApp : Application() {
    companion object {
        /**
         * Reference to the application instance.
         * This is a workaround to access the application context from the shared code.
         */
        lateinit var instance: AndroidApp
            private set
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}