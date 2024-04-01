package com.processout.sdk.ui.shared.extension

internal inline fun <T> T?.orElse(block: () -> T): T {
    return this ?: block()
}
