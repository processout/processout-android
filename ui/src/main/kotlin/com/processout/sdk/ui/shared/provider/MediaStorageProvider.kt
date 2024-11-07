package com.processout.sdk.ui.shared.provider

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

internal class MediaStorageProvider(
    private val app: Application,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun saveImage(bitmap: Bitmap): ProcessOutResult<Unit> =
        withContext(workDispatcher) {
            try {
                val currentTimeMillis = System.currentTimeMillis()
                val filename = "IMG_$currentTimeMillis.png"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.Images.Media.DATE_TAKEN, currentTimeMillis)
                        put(MediaStore.Images.Media.DATE_ADDED, currentTimeMillis / 1000L)
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                    val resolver = app.contentResolver
                    resolver.insert(contentUri, contentValues)?.let { uri ->
                        resolver.openOutputStream(uri)?.write(bitmap)
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                    }
                } else {
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val file = File(path, filename)
                    FileOutputStream(file).write(bitmap)
                    MediaScannerConnection.scanFile(app, arrayOf(file.absolutePath), null, null)
                }
                ProcessOutResult.Success(Unit)
            } catch (e: Exception) {
                ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Failed to save image in media storage.",
                    cause = e
                )
            }
        }

    private fun OutputStream.write(bitmap: Bitmap) = use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}
