package com.tms.financiapp


import androidx.test.core.app.ApplicationProvider
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.initialize
import org.junit.After
import org.junit.Before

const val APP_ID = "1:1074538729469:android:bf54a0f1cddb845ab85394"
const val API_KEY = "AIzaSyDvOudb8AiTZnJvRGU05BH1C0rhKVw3wbA"
const val EXISTING_APP = "financiapp-e529f"

abstract class BaseTestCase {
    @Before
    open fun setUp() {
        Firebase.initialize(
            ApplicationProvider.getApplicationContext(),
            FirebaseOptions.Builder()
                .setApplicationId(APP_ID)
                .setApiKey(API_KEY)
                .setProjectId("financiapp-e529f")
                .build()
        )

        Firebase.initialize(
            ApplicationProvider.getApplicationContext(),
            FirebaseOptions.Builder()
                .setApplicationId(APP_ID)
                .setApiKey(API_KEY)
                .setProjectId("financiapp-e529f")
                .build(),
            EXISTING_APP
        )
    }

    @After
    fun cleanUp() {
        FirebaseApp.clearInstancesForTest()
    }
}