package com.processout.sdk.ui.shared.component

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes

@Composable
internal fun CameraPreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }
    AndroidView(
        modifier = modifier.clip(shapes.roundedCornersMedium),
        factory = {
            PreviewView(it).apply {
                controller = cameraController
                clipToOutline = true
                scaleType = PreviewView.ScaleType.FILL_START
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        onRelease = { cameraController.unbind() }
    )
}
