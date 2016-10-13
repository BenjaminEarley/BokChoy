package com.benjaminearley.bokchoy.util

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import com.benjaminearley.bokchoy.R

fun animateTitleChange(activity: AppCompatActivity, toolbar: Toolbar, newTitle: String) {
    val view = getToolbarTitle(activity, toolbar)

    if (view is TextView) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = activity.resources.getInteger(R.integer.title_animation_speed).toLong()
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                activity.supportActionBar?.title = newTitle

                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.duration = activity.resources.getInteger(R.integer.title_animation_speed).toLong()
                view.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        view.startAnimation(fadeOut)
    }
}

private fun getToolbarTitle(activity: Activity, toolbar: Toolbar): View {
    val childCount = toolbar.childCount
    for (i in 0..childCount - 1) {
        val child = toolbar.getChildAt(i)
        if (child is TextView) {
            return child
        }
    }

    return View(activity)
}
