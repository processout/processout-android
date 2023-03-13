package com.processout.sdk.utils

internal fun CharSequence.isAlphanumericsOnly() =
    matches(Regex("^[\\p{L}\\p{M}\\p{N}]*\$"))
