package com.vtu.mmproject

import android.app.Application
import com.google.firebase.FirebaseApp

class MMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
