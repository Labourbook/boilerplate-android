package com.boilerplate.network.model

data class APIHeaders(
    var deviceId: String,
    var systemId: String,
    var appVersion: String,
    var isUserLoggedIn: Boolean,
    var acceptLanguage: String,
    var languageMode: String,
    var deviceSegment: String,
    var countryCode: String,
    var timeZone: String,
    var osVersion: String,
    var accessToken: String = "",
    var userId: String = "",
    var internetTier: String = "",
    var internetSpeed: String = "",
    var performanceClass: String = "",
    var screenDensity: String = "",
    var ipv4: String = "",
    var ipv6: String = "",
    var isNewUser: Boolean = false,
)
