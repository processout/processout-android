package com.processout.sdk.ui.shared.transformation

import android.telephony.PhoneNumberUtils
import android.text.Selection
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

internal class PhoneNumberVisualTransformation(
    private val countryCode: String = Locale.getDefault().country
) : BaseVisualTransformation() {

    private val formatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode)

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
        other as PhoneNumberVisualTransformation
        return countryCode == other.countryCode
    }

    override fun hashCode(): Int {
        return countryCode.hashCode()
    }
}
