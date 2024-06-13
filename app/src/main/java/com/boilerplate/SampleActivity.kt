package com.boilerplate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.boilerplate.network.apiclient.NetworkModule

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sample)
        val baseUrl = "https://reqres.in/api/"
        val headers = mapOf(
            "Authorization" to "Bearer your_token",
            "Content-Type" to "application/json"
        )

        NetworkModule.initialize(baseUrl, headers)
    }
}