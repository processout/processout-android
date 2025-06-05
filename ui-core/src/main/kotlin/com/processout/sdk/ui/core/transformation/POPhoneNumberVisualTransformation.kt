package com.processout.sdk.ui.core.transformation

import android.telephony.PhoneNumberUtils
import android.text.Selection
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import java.util.Locale

/** @suppress */
@ProcessOutInternalApi
class POPhoneNumberVisualTransformation(
    private val expectedFormat: PhoneNumberFormat,
    private val regionCode: String = Locale.getDefault().country
) : POBaseVisualTransformation() {

    enum class PhoneNumberFormat {
        NATIONAL,
        INTERNATIONAL
    }

    private val formatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(regionCode)

    override fun transform(text: String): String {
        formatter.clear()
        var formatted: String? = null
        val cursorIndex = Selection.getSelectionEnd(text) - 1
        var lastNonSeparator = 0.toChar()
        var hasCursor = false
        text.forEachIndexed { index, char ->
            if (PhoneNumberUtils.isNonSeparator(char)) {
                if (lastNonSeparator.code != 0) {
                    formatted = formatted(lastNonSeparator, hasCursor)
                    hasCursor = false
                }
                lastNonSeparator = char
            }
            if (index == cursorIndex) {
                hasCursor = true
            }
        }
        if (lastNonSeparator.code != 0) {
            formatted = formatted(lastNonSeparator, hasCursor)
        }
        return formatted ?: text
    }

    private fun formatted(lastNonSeparator: Char, hasCursor: Boolean) =
        if (hasCursor) {
            formatter.inputDigitAndRememberPosition(lastNonSeparator)
        } else {
            formatter.inputDigit(lastNonSeparator)
        }

    override fun isSeparator(char: Char) = !PhoneNumberUtils.isNonSeparator(char)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as POPhoneNumberVisualTransformation
        if (expectedFormat != other.expectedFormat) return false
        if (regionCode != other.regionCode) return false
        return true
    }

    override fun hashCode(): Int {
        var result = expectedFormat.hashCode()
        result = 31 * result + regionCode.hashCode()
        return result
    }
}
