package com.processout.sdk.ui.card.scanner.recognition

internal interface CardAttributeDetector<out T> {

    fun firstMatch(candidates: List<String>): T?
}
