package com.processout.sdk.ui.shared.extension

internal inline infix fun <reified E : Enum<E>, V> ((E) -> V).findBy(value: V): E? {
    return enumValues<E>().firstOrNull { this(it) == value }
}
