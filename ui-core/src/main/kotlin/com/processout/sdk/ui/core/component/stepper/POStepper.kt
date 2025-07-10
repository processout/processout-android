package com.processout.sdk.ui.core.component.stepper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.stepper.POStepper.StepState.*
import com.processout.sdk.ui.core.style.POStepperStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
object POStepper {

    data class Step(
        val title: String,
        val description: String? = null
    )

    enum class StepState {
        PENDING,
        ACTIVE,
        COMPLETED
    }

    data class Style(
        val pending: StepStyle,
        val active: StepStyle,
        val completed: StepStyle
    )

    data class StepStyle(
        val title: POText.Style,
        val description: POText.Style,
        val icon: POStepIcon.Style,
        val connector: POStroke.Style
    )

    val default: Style
        @Composable get() = Style(
            pending = StepStyle(
                title = POText.Style(
                    color = colors.text.tertiary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                description = POText.Style(
                    color = colors.text.tertiary,
                    textStyle = typography.s12(FontWeight.Medium)
                ),
                icon = POStepIcon.pending,
                connector = POStroke.Style(
                    width = 2.dp,
                    color = Color(0xFFCECECE),
                    dashInterval = 3.dp
                )
            ),
            active = StepStyle(
                title = POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                description = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s12(FontWeight.Medium)
                ),
                icon = POStepIcon.active,
                connector = POStroke.Style(
                    width = 2.dp,
                    color = POStepIcon.DefaultCompletedColor,
                    dashInterval = 3.dp
                )
            ),
            completed = StepStyle(
                title = POText.Style(
                    color = colors.text.positive,
                    textStyle = typography.s15(FontWeight.Medium)
                ),
                description = POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s12(FontWeight.Medium)
                ),
                icon = POStepIcon.completed,
                connector = POStroke.Style(
                    width = 2.dp,
                    color = POStepIcon.DefaultCompletedColor
                )
            )
        )

    @Composable
    fun custom(style: POStepperStyle) = Style(
        pending = style.pending.toStepStyle(),
        active = style.active.toStepStyle(),
        completed = style.completed.toStepStyle()
    )

    @Composable
    private fun POStepperStyle.StepStyle.toStepStyle() = StepStyle(
        title = POText.custom(style = title),
        description = POText.custom(style = description),
        icon = POStepIcon.custom(style = icon),
        connector = POStroke.custom(style = connector)
    )

    internal fun stepStyle(
        style: Style,
        state: StepState
    ): StepStyle =
        when (state) {
            PENDING -> style.pending
            ACTIVE -> style.active
            COMPLETED -> style.completed
        }

    internal fun connectorStyle(
        style: Style,
        states: List<StepState>,
        currentStepIndex: Int
    ): POStroke.Style {
        val current = states.getOrNull(index = currentStepIndex)
        val next = states.getOrNull(index = currentStepIndex + 1)
        return when {
            current == COMPLETED && next == COMPLETED -> style.completed.connector
            current == COMPLETED && next == ACTIVE -> style.active.connector
            else -> style.pending.connector
        }
    }
}
