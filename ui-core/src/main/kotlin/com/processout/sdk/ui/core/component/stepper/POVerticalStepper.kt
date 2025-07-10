package com.processout.sdk.ui.core.component.stepper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POExpandableText
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.stepper.POStepper.StepState.*
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POVerticalStepper(
    steps: POImmutableList<POStepper.Step>,
    modifier: Modifier = Modifier,
    style: POStepper.Style = POStepper.default,
    activeStepIndex: Int = 0
) {
    Column(modifier = modifier) {
        val states = List(steps.elements.size) { index ->
            when {
                index < activeStepIndex -> COMPLETED
                index == activeStepIndex -> ACTIVE
                else -> PENDING
            }
        }
        steps.elements.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                val stepStyle = POStepper.stepStyle(
                    style = style,
                    state = states[index]
                )
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    POStepIcon(
                        style = stepStyle.icon
                    )
                    if (index != steps.elements.lastIndex) {
                        val connectorStyle = POStepper.connectorStyle(
                            style = style,
                            states = states,
                            currentStepIndex = index
                        )
                        Canvas(
                            modifier = Modifier
                                .requiredWidth(connectorStyle.width)
                                .fillMaxHeight()
                                .defaultMinSize(minHeight = 28.dp)
                        ) {
                            val pathEffect = connectorStyle.dashInterval?.toPx()
                                ?.let { dashIntervalPx ->
                                    PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(dashIntervalPx, dashIntervalPx)
                                    )
                                }
                            drawLine(
                                color = connectorStyle.color,
                                start = Offset(x = 0f, y = 0f),
                                end = Offset(x = 0f, y = size.height),
                                strokeWidth = size.width,
                                pathEffect = pathEffect
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = spacing.space6)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(spacing.space4)
                ) {
                    val titleTextStyle = stepStyle.title.textStyle
                    POText(
                        text = step.title,
                        color = stepStyle.title.color,
                        style = titleTextStyle,
                        modifier = Modifier.padding(
                            top = POText.measuredPaddingTop(
                                textStyle = titleTextStyle,
                                componentHeight = 36.dp
                            )
                        )
                    )
                    POExpandableText(
                        text = step.description,
                        style = stepStyle.description,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
@Composable
@Preview(showBackground = true)
private fun POVerticalStepperPreview() {
    POVerticalStepper(
        steps = POImmutableList(
            listOf(
                POStepper.Step(
                    title = "Step 1",
                    description = "Description"
                ),
                POStepper.Step(
                    title = "Step 2",
                    description = "Description"
                ),
                POStepper.Step(
                    title = "Step 3",
                    description = "Description"
                ),
                POStepper.Step(
                    title = "Step 4",
                    description = "Description"
                )
            )
        ),
        activeStepIndex = 2
    )
}
