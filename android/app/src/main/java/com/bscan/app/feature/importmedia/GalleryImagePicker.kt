package com.bscan.app.feature.importmedia

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun rememberGalleryImagePicker(
    maxItems: Int = 20,
    onImagesPicked: (List<Uri>) -> Unit,
    onPickerUnavailable: () -> Unit = {},
): () -> Unit {
    val latestOnPicked = rememberUpdatedState(onImagesPicked)
    val latestOnUnavailable = rememberUpdatedState(onPickerUnavailable)

    val fallbackDocumentsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        if (uris.isNotEmpty()) {
            latestOnPicked.value(uris)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxItems),
    ) { uris ->
        if (uris.isNotEmpty()) {
            latestOnPicked.value(uris)
        }
    }

    return {
        try {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        } catch (_: ActivityNotFoundException) {
            try {
                fallbackDocumentsLauncher.launch(arrayOf("image/*"))
            } catch (_: ActivityNotFoundException) {
                latestOnUnavailable.value()
            }
        }
    }
}
