package com.example.ecommercetaggingapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

class EcommerceApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // ✅ Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // ✅ Habilita Analytics (garantia explícita)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)

        // ✅ Inicializa o AnalyticsManager com contexto
        AnalyticsManager.init(this)
    }
}
