package com.benjaminearley.bokchoy.util

import android.content.Intent
import com.benjaminearley.bokchoy.BuildConfig
import com.benjaminearley.bokchoy.R
import com.firebase.ui.auth.AuthUI

object Keys {

    val LISTS_CHILD = "lists"
    val LIST_CHILD = "list"
}

val colorMap = mapOf(
        Pair("Green", R.color.green_custom),
        Pair("Blue", R.color.blue_A200),
        Pair("Red", R.color.red_A200)
)

val SignInScreenIntent: Intent by lazy {
    AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.drawable.ic_bokchoy)
            .setTheme(R.style.AppTheme)
            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
            .setProviders(
                    AuthUI.EMAIL_PROVIDER,
                    AuthUI.GOOGLE_PROVIDER)
            .build()
}
