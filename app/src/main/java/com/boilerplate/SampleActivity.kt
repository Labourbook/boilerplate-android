package com.boilerplate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.boilerplate.network.NetworkHandler

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sample)
        val baseUrl = "https://reqres.in/api/"

        val networkHandler = NetworkHandler.getInstance()
        networkHandler.initialize(
            deviceId = "your-device-id",
            systemId = "your-system-id",
            appVersion = "your-app-version"
        )

        val apiService: YourApiService = networkHandler.getApiClient<YourApiService>("https://api.yourservice.com/")
    }
}