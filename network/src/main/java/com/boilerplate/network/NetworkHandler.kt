package com.boilerplate.network

import com.boilerplate.network.auth.callback.DefaultAuthenticationCallback
import com.boilerplate.network.model.APIHeaders
import com.boilerplate.network.model.DataResponse
import com.boilerplate.network.model.NetworkResult
import com.boilerplate.network.utils.CurlLoggerInterceptor
import com.boilerplate.network.utils.NetworkConstants
import com.boilerplate.network.utils.NetworkHandlerException
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

 class NetworkHandler {

    private lateinit var headers: APIHeaders

    private var isDebug = false

     private var refreshToken  : String? = null
     private var defaultAuthenticationCallback : DefaultAuthenticationCallback? = null


    @Throws(NetworkHandlerException::class)
    fun initialize(
        deviceId: String,
        systemId: String,
        appVersion: String,
        isUserLoggedIn: Boolean,
        acceptLanguage: String,
        languageMode: String,
        deviceSegment: String,
        countryCode: String,
        timeZone: String,
        osVersion: String,
    ) {
        if (networkHandler != null)
            headers = APIHeaders(deviceId, systemId, appVersion, isUserLoggedIn, acceptLanguage, languageMode, deviceSegment, countryCode, timeZone, osVersion)
        else
            throw NetworkHandlerException("Already Initialized")
    }

    @Throws(NetworkHandlerException::class)
    fun setAccessToken(accessToken: String) {
        checkIfInitialized()
        headers.accessToken = accessToken
    }

    @Throws(NetworkHandlerException::class)
    fun setUserId(userId: String) {
        checkIfInitialized()
        headers.userId = userId
    }

    @Throws(NetworkHandlerException::class)
    fun setInternetSpeedDetails(internetSpeed: String, internetTier: String) {
        checkIfInitialized()
        headers.internetSpeed = internetSpeed
        headers.internetTier = internetTier
    }

    @Throws(NetworkHandlerException::class)
    fun setDevicePerformanceClass(perfClass: String) {
        checkIfInitialized()
        headers.performanceClass = perfClass
    }

    @Throws(NetworkHandlerException::class)
    fun setScreenDensity(screenDensity: String) {
        checkIfInitialized()
        headers.screenDensity = screenDensity
    }

    @Throws(NetworkHandlerException::class)
    fun setIpAddress(ipv4: String = "", ipv6: String = "") {
        checkIfInitialized()
        headers.ipv4 = ipv4
        headers.ipv6 = ipv6
    }

    @Throws(NetworkHandlerException::class)
    fun setIfNewUser(isNewUser: Boolean) {
        checkIfInitialized()
        headers.isNewUser = isNewUser
    }

    @Throws(NetworkHandlerException::class)
    fun enableDebugMode(boolean: Boolean) {
        checkIfInitialized()
        isDebug = boolean
    }

    @Throws(NetworkHandlerException::class)
    suspend fun <Output> getCachedData(
        remoteFetch: suspend () -> Response<DataResponse<Output>>?,
        localFetch: suspend () -> List<Output>?,
        localStore: suspend (Output) -> Unit,
        localDelete: suspend () -> Unit,
    ): Flow<NetworkResult<Output?>> {
        checkIfInitialized()
        return NetworkResource(remoteFetch, localFetch, localStore, localDelete).query()
    }

    @Throws(NetworkHandlerException::class)
    suspend fun <Output> getData(remoteFetch: suspend () -> Response<DataResponse<Output>>?): Flow<NetworkResult<Output?>> {
        checkIfInitialized()
        return NetworkResource(remoteFetch).query(true)
    }

    @Throws(NetworkHandlerException::class)
    suspend fun <Output> getDataResult(remoteFetch: suspend () -> Response<DataResponse<Output>>?): NetworkResult<Output?> {
        checkIfInitialized()
        return NetworkResource(remoteFetch).queryWithoutFlow()
    }
     inline fun <reified ApiInterface> getApiClient(baseUrl: String): ApiInterface {
        val res = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        return res
    }

     inline fun <reified ApiInterface> getDefaultApiClient(): ApiInterface {
         val baseUrl = if(isDebug()) NetworkConstants.BASE_URL_DEBUG else NetworkConstants. BASE_URL
         val res = Retrofit.Builder()
             .baseUrl(baseUrl)
             .client(getOkHttpClient())
             .addConverterFactory(GsonConverterFactory.create())
             .build()
             .create(ApiInterface::class.java)

         return res
     }

     fun getOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (isDebug) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original: Request = chain.request()
                // Request customization: add request headers
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Authorization", ("Bearer " + headers.accessToken))
                    .header("Device-ID", headers.deviceId)
                    .header("App-Version", headers.appVersion)
                    .header("App-Type", "Android")
                    .header("X-AFB-APP-VER", headers.appVersion)
                    .header("X-AFB-R-UID", headers.userId)
                    .header("X-AFB-R-LOGGEDIN", headers.isUserLoggedIn.toString())
                    .header("X-AFB-PLATFORM", "android")
                    .header("X-AFB-DEVICE-ID", headers.deviceId)
                    .header("Accept-Language", headers.acceptLanguage)
                    .header("x-afb-language-mode", headers.languageMode)
                    .header("X-AFB-INTERNET-TIER", headers.internetTier)
                    .header("X-AFB-INTERNET-SPEED", headers.internetSpeed)
                    .header("x-afb-system-id", headers.systemId)
                    .header("X-AFB-DEVICE-PERFORMANCE-CLASS", headers.performanceClass)
                    .header("X-AFB-COUNTRY-CODE", headers.countryCode)
                    .header("X-AFB-TZ", headers.timeZone)
                    .header("x-afb-screen-density", headers.screenDensity)
                    .header("x-afb-device-segment", headers.deviceSegment)
                    .header("x-afb-os-version", headers.osVersion)
                    .header("x-afb-ipv4", headers.ipv4)
                    .header("x-afb-ipv6", headers.ipv6)
                    .method(original.method, original.body)

                for(key in headers.additionalHeaders.keys){
                    requestBuilder.header(key, headers.additionalHeaders[key] ?: "")
                }

                //todo : create pref module
//                if(prefsInstance.getBoolean(Prefs.IS_NEW_USER)){
//                    requestBuilder.header("X-AFB-IS-NEW-USER", prefsInstance.getBoolean(Prefs.IS_NEW_USER).toString())
//                }
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(httpLoggingInterceptor)
//            .addNetworkInterceptor(StethoInterceptor())
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .also { if (isDebug) it.addInterceptor(CurlLoggerInterceptor("CURL")) }

            .build()

    }

    companion object {

        private var networkHandler: NetworkHandler? = null

        @Throws(NetworkHandlerException::class)
        fun getInstance() : NetworkHandler {
            return networkHandler ?: synchronized(this){
                networkHandler ?: NetworkHandler().also { networkHandler = it }
            }
        }
    }

    private fun checkIfInitialized(){
        if(networkHandler == null)
            throw NetworkHandlerException("Network Handler not initialized")
    }

    fun getHeaders() = headers

     fun addAuthentication(refreshToken : String, defaultAuthenticationCallback: DefaultAuthenticationCallback){
         this.refreshToken = refreshToken
         this.defaultAuthenticationCallback = defaultAuthenticationCallback
     }

     fun setRefreshToken(refreshToken: String){
         this.refreshToken = refreshToken
     }

     fun isDebug() = isDebug

     internal fun getDefaultAuthCallback() = defaultAuthenticationCallback

     internal fun getRefreshToken() = refreshToken

     fun setAdditionalHeaders(additionHeaders : HashMap<String, String>){
         headers.additionalHeaders = additionHeaders
     }
}