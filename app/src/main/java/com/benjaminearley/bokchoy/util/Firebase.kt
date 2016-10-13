package com.benjaminearley.bokchoy.util

import android.content.Intent
import com.benjaminearley.bokchoy.R
import com.firebase.ui.auth.AuthUI

object Keys {

    val LISTS_CHILD = "lists"
    val LIST_CHILD = "list"

    val LISTS_TITLE = "title"
    val LISTS_COLOR = "color"

    val LISTS_TITLE_LENGTH_CONFIG = "lists_title_length"

    val LIST_TEXT = "text"
    val LIST_CHECKBOX = "checkbox"
}

val colorPrimaryMap = mapOf(
        Pair("Green", R.color.green_custom),
        Pair("Blue", R.color.blue_A200),
        Pair("Red", R.color.red_A200)
)

val colorSecondaryMap = mapOf(
        Pair("Green", R.color.colorPrimary),
        Pair("Blue", R.color.blue_A700),
        Pair("Red", R.color.red_A700)
)

val colorThemeMap = mapOf(
        Pair("Green", R.style.AppTheme_NoActionBar_GreenTheme),
        Pair("Blue", R.style.AppTheme_NoActionBar_BlueTheme),
        Pair("Red", R.style.AppTheme_NoActionBar_RedTheme)
)

val SignInScreenIntent: Intent by lazy {
    AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.drawable.ic_bokchoy)
            .setTheme(R.style.AppTheme)
            .setIsSmartLockEnabled(false)
            .setProviders(
                    AuthUI.EMAIL_PROVIDER,
                    AuthUI.GOOGLE_PROVIDER)
            .build()
}
