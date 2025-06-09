package com.processout.sdk.ui.core.transformation

import android.telephony.PhoneNumberUtils
import android.text.Selection
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.transformation.POPhoneNumberVisualTransformation.PhoneNumberFormat.INTERNATIONAL
import com.processout.sdk.ui.core.transformation.POPhoneNumberVisualTransformation.PhoneNumberFormat.NATIONAL

/** @suppress */
@ProcessOutInternalApi
class POPhoneNumberVisualTransformation(
    private val regionCode: String,
    private val expectedFormat: PhoneNumberFormat
) : POBaseVisualTransformation() {

    enum class PhoneNumberFormat {
        NATIONAL,
        INTERNATIONAL
    }

    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    private val formatter = phoneNumberUtil.getAsYouTypeFormatter(regionCode)

    override fun transform(text: String): String {
        formatter.clear()
        var dialingCode: String? = null
        val phoneNumber = when (expectedFormat) {
            NATIONAL -> if (regionCode.isNotBlank()) {
                dialingCode = "+${phoneNumberUtil.getCountryCodeForRegion(regionCode)}"
                "$dialingCode$text"
            } else text
            INTERNATIONAL -> text
        }
        var formatted: String? = null
        val cursorIndex = Selection.getSelectionEnd(phoneNumber) - 1
        var lastNonSeparator = 0.toChar()
        var hasCursor = false
        phoneNumber.forEachIndexed { index, char ->
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
        return when (expectedFormat) {
            NATIONAL -> formatted?.let {
                if (dialingCode != null) {
                    val dialingCodeRegex = """^\s*(?:\+|00)?\s*\(?\s*$dialingCode\s*\)?[\s-]*""".toRegex()
                    it.replaceFirst(dialingCodeRegex, String())
                } else text
            } ?: text
            INTERNATIONAL -> formatted ?: text
        }
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
        if (regionCode != other.regionCode) return false
        if (expectedFormat != other.expectedFormat) return false
        return true
    }

    override fun hashCode(): Int {
        var result = regionCode.hashCode()
        result = 31 * result + expectedFormat.hashCode()
        return result
    }
}
