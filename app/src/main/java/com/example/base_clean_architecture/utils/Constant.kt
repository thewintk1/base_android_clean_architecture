package com.example.base_clean_architecture.utils

object Constant {
    const val PLAY_STORE_URI = "market://details?id=%s"
    const val BROWSER_URI = "https://play.google.com/store/apps/details?id=%s"

    // Exception Network
    const val ERROR_CODE_NETWORK_DNS = "-101"
    const val ERROR_CODE_NETWORK_CONNECTION_TIME_OUT = "-102"
    const val ERROR_CODE_NETWORK_SOCKET_TIME_OUT = "-103"
    const val ERROR_CODE_NETWORK_SOCKET = "-104"
    const val ERROR_CODE_NETWORK_SSL_HAND_SHAKE = "-105"
    const val ERROR_CODE_NETWORK_SSL_PEER_UNVERIFIED = "-106"
    const val ERROR_CODE_NETWORK_NO_NETWORK = "-107"


    enum class Tab(val value: String) {
        HOME("home"), TAB1("Tab1"), TAB2("Tab2"), TAB3("Tab3")
    }
}