package com.processout.sdk.ui.shared.configuration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Specifies action confirmation configuration (e.g. dialog).
 *
 * @param[title] Custom title. Pass _null_ to use default text.
 * @param[message] Custom message. Pass _null_ to use default text. Pass empty string to hide.
 * @param[confirmActionText] Custom confirm action text. Pass _null_ to use default text.
 * @param[dismissActionText] Custom dismiss action text. Pass _null_ to use default text. Pass empty string to hide.
 */
@Parcelize
data class POActionConfirmationConfiguration(
    val title: String? = null,
    val message: String? = null,
    val confirmActionText: String? = null,
    val dismissActionText: String? = null
) : Parcelable
