@file:Suppress("NAME_SHADOWING", "MayBeConstant")

package com.processout.sdk.ui.napm.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioField
import com.processout.sdk.ui.core.component.stepper.POStepper
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Action
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.ActionConfirmationRequested
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen.LogoHeight
import com.processout.sdk.ui.shared.component.AndroidTextView
import com.processout.sdk.ui.shared.extension.conditional
import com.processout.sdk.ui.shared.extension.dpToPx

@Composable
internal fun NativeAlternativePaymentScreen(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    isLightTheme: Boolean,
    style: NativeAlternativePaymentScreen.Style = NativeAlternativePaymentScreen.style()
) {
    if (state is Loaded) {
        when (state.content.stage) {
            is Stage.Pending,
            is Stage.Completed -> LocalFocusManager.current.clearFocus(force = true)
            else -> {}
        }
    }
    var topBarHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = {
            Header(
                logo = if (state is Loaded) state.header?.logo else null,
                title = if (state is Loaded) state.header?.title else null,
                titleStyle = style.title,
                dividerColor = style.dividerColor,
                dragHandleColor = style.dragHandleColor,
                isLightTheme = isLightTheme,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .onGloballyPositioned {
                        topBarHeight = it.size.height
                    }
            )
        },
        bottomBar = {
            Actions(
                state = state,
                onEvent = onEvent,
                containerStyle = style.actionsContainer,
                dialogStyle = style.dialog,
                modifier = Modifier.onGloballyPositioned {
                    bottomBarHeight = it.size.height
                }
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.space20),
            verticalArrangement = if (state is Loading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val adjustedContentHeight = 90.dp.dpToPx()
            when (state) {
                is Loading -> POCircularProgressIndicator.Large(
                    color = style.progressIndicatorColor
                )
                is Loaded -> AnimatedVisibility {
                    NativeAlternativePaymentContent(
                        content = state.content,
                        onEvent = onEvent,
                        style = style,
                        isPrimaryActionEnabled = state.primaryAction?.let { it.enabled && !it.loading } ?: false,
                        isLightTheme = isLightTheme,
                        modifier = Modifier.onGloballyPositioned {
                            val contentHeight = it.size.height + topBarHeight + bottomBarHeight + adjustedContentHeight
                            onContentHeightChanged(contentHeight)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    logo: POImageResource?,
    title: String?,
    titleStyle: POText.Style,
    dividerColor: Color,
    dragHandleColor: Color,
    isLightTheme: Boolean,
    modifier: Modifier = Modifier,
    withDragHandle: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier
                    .padding(top = spacing.space10)
                    .align(alignment = Alignment.TopCenter),
                color = dragHandleColor
            )
        }
        var showLogo by remember { mutableStateOf(true) }
        val logoUrl = logo?.let {
            if (isLightTheme) {
                it.lightUrl.raster
            } else {
                it.darkUrl?.raster ?: it.lightUrl.raster
            }
        }
        AnimatedVisibility(
            visible = showLogo && !logoUrl.isNullOrBlank() || !title.isNullOrBlank(),
            enter = fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis)),
        ) {
            Column(
                modifier = Modifier.conditional(
                    condition = withDragHandle,
                    whenTrue = { padding(top = spacing.space20) },
                    whenFalse = { padding(top = spacing.space12) }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = spacing.space20,
                            end = spacing.space20,
                            bottom = spacing.space12
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        if (showLogo && !logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .requiredHeight(LogoHeight)
                                    .padding(end = spacing.space16),
                                contentScale = ContentScale.FillHeight,
                                onError = {
                                    showLogo = false
                                }
                            )
                        }
                    }
                    if (!title.isNullOrBlank()) {
                        POText(
                            text = title,
                            modifier = Modifier.weight(1f, fill = false),
                            color = titleStyle.color,
                            style = titleStyle.textStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
    }
}

@Composable
private fun Actions(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    containerStyle: POActionsContainer.Style,
    dialogStyle: PODialog.Style,
    modifier: Modifier = Modifier
) {
    var primary: POActionState? = null
    val secondary: POActionState?
    when (state) {
        is Loading -> secondary = state.secondaryAction
        is Loaded -> {
            primary = state.primaryAction
            secondary = state.secondaryAction
        }
    }
    val actions = listOfNotNull(primary, secondary)
    POActionsContainer(
        modifier = modifier,
        actions = POImmutableList(
            if (containerStyle.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(id = it)) },
        onConfirmationRequested = { onEvent(ActionConfirmationRequested(id = it)) },
        containerStyle = containerStyle,
        confirmationDialogStyle = dialogStyle,
        animationDurationMillis = AnimationDurationMillis
    )
}

@Composable
private fun AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(initialState = false)
            .apply { targetState = true }
    },
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis))
    ) {
        content()
    }
}

internal object NativeAlternativePaymentScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val bodyText: AndroidTextView.Style,
        val message: POText.Style,
        val labeledContent: POLabeledContent.Style,
        val groupedContent: POGroupedContent.Style,
        val field: POField.Style,
        val codeField: POField.Style,
        val radioField: PORadioField.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val checkbox: POCheckbox.Style,
        val dialog: PODialog.Style,
        val stepper: POStepper.Style,
        val success: SuccessStyle,
        val errorMessageBox: POMessageBox.Style,
        val actionsContainer: POActionsContainer.Style,
        val backgroundColor: Color,
        val progressIndicatorColor: Color,
        val dividerColor: Color,
        val dragHandleColor: Color
    )

    @Immutable
    data class SuccessStyle(
        val title: POText.Style,
        val message: POText.Style,
        @DrawableRes
        val successImageResId: Int
    )

    @Composable
    fun style(custom: PONativeAlternativePaymentConfiguration.Style? = null) =
        with(ProcessOutTheme) {
            Style(
                title = custom?.title?.let {
                    POText.custom(style = it)
                } ?: POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s20(FontWeight.Medium)
                ),
                bodyText = custom?.bodyText?.let { style ->
                    val controlsTintColor = custom.controlsTintColorResId?.let { colorResource(id = it) }
                    AndroidTextView.custom(
                        style = style,
                        controlsTintColor = controlsTintColor ?: colors.text.primary
                    )
                } ?: AndroidTextView.default,
                message = custom?.message?.let {
                    POText.custom(style = it)
                } ?: POText.Style(
                    color = colors.text.secondary,
                    textStyle = typography.s14()
                ),
                labeledContent = custom?.labeledContent?.let {
                    POLabeledContent.custom(style = it)
                } ?: POLabeledContent.default,
                groupedContent = custom?.groupedContent?.let {
                    POGroupedContent.custom(style = it)
                } ?: POGroupedContent.default,
                field = custom?.field?.let {
                    POField.custom(style = it)
                } ?: POField.default,
                codeField = custom?.codeField?.let {
                    POField.custom(style = it)
                } ?: POCodeField.default,
                radioField = custom?.radioField?.let {
                    PORadioField.custom(style = it)
                } ?: PORadioField.default,
                dropdownMenu = custom?.dropdownMenu?.let {
                    PODropdownField.custom(style = it)
                } ?: PODropdownField.defaultMenu,
                checkbox = custom?.checkbox?.let {
                    POCheckbox.custom(style = it)
                } ?: POCheckbox.default2,
                dialog = custom?.dialog?.let {
                    PODialog.custom(style = it)
                } ?: PODialog.default,
                stepper = custom?.stepper?.let {
                    POStepper.custom(style = it)
                } ?: POStepper.default,
                success = custom?.success?.custom() ?: defaultSuccess,
                errorMessageBox = custom?.errorMessageBox?.let {
                    POMessageBox.custom(style = it)
                } ?: POMessageBox.error,
                actionsContainer = custom?.actionsContainer?.let {
                    POActionsContainer.custom(style = it)
                } ?: POActionsContainer.default2,
                backgroundColor = custom?.backgroundColorResId?.let {
                    colorResource(id = it)
                } ?: colors.surface.default,
                progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
                    colorResource(id = it)
                } ?: colors.button.primaryBackgroundDefault,
                dividerColor = custom?.dividerColorResId?.let {
                    colorResource(id = it)
                } ?: colors.border.border4,
                dragHandleColor = custom?.dragHandleColorResId?.let {
                    colorResource(id = it)
                } ?: colors.icon.disabled
            )
        }

    val defaultSuccess: SuccessStyle
        @Composable get() = SuccessStyle(
            title = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s20(FontWeight.SemiBold)
            ),
            message = POText.Style(
                color = colors.text.secondary,
                textStyle = typography.paragraph.s16()
            ),
            successImageResId = R.drawable.po_success_image_v2
        )

    @Composable
    private fun PONativeAlternativePaymentConfiguration.Style.SuccessStyle.custom() =
        SuccessStyle(
            title = title?.let { POText.custom(style = it) } ?: defaultSuccess.title,
            message = message?.let { POText.custom(style = it) } ?: defaultSuccess.message,
            successImageResId = successImageResId ?: defaultSuccess.successImageResId
        )

    val LogoHeight = 26.dp
    val AnimationDurationMillis = 300

    val ContentTransitionSpec = fadeIn(
        animationSpec = tween(
            durationMillis = AnimationDurationMillis,
            easing = LinearEasing
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = AnimationDurationMillis,
            easing = LinearEasing
        )
    )
}
