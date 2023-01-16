package com.processout.sdk.ui.shared.view.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

internal fun crossfade(
    viewsToHide: List<View>,
    viewsToShow: List<View>,
    duration: Long
) {
    fadeOut(viewsToHide, duration)
    fadeIn(viewsToShow, duration)
}

internal fun fadeOut(views: List<View>, duration: Long) {
    views.forEach {
        it.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    it.visibility = View.GONE
                }
            })
    }
}

internal fun fadeIn(views: List<View>, duration: Long) {
    views.forEach {
        it.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null)
        }
    }
}
