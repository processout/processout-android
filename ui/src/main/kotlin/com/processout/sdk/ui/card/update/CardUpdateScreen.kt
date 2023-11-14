package com.processout.sdk.ui.card.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import com.processout.sdk.ui.core.theme.ProcessOutTheme

@Composable
internal fun CardUpdateScreen(
    state: CardUpdateState,
    onEvent: (CardUpdateEvent) -> Unit
) {
    Surface(
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
        shape = ProcessOutTheme.shapes.topRoundedCornersLarge
    ) {
        Scaffold(
            containerColor = ProcessOutTheme.colors.surface.level1,
            bottomBar = {
                // TODO: buttons
            }
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // TODO: content
            }
        }
    }
}
