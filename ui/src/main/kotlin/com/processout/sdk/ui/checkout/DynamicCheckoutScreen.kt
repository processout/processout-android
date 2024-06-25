package com.processout.sdk.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.isImeVisibleAsState

@Composable
internal fun DynamicCheckoutScreen() {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
        Scaffold(
            modifier = Modifier.clip(shape = ProcessOutTheme.shapes.topRoundedCornersLarge),
            containerColor = ProcessOutTheme.colors.surface.level1,
            topBar = { Header() },
            bottomBar = { Footer() }
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Content()
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO
    }
}

@Composable
private fun Content() {
    // TODO
}

@Composable
private fun Footer() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO
        }
        val isImeVisible by isImeVisibleAsState()
        val imePaddingValues = WindowInsets.ime.asPaddingValues()
        val systemBarsPaddingValues = WindowInsets.systemBars.asPaddingValues()
        Spacer(
            Modifier.requiredHeight(
                if (isImeVisible) imePaddingValues.calculateBottomPadding()
                else systemBarsPaddingValues.calculateBottomPadding()
            )
        )
    }
}
