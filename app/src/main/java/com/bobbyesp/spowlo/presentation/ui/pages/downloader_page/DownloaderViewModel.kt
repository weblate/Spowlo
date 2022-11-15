package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class DownloaderViewModel @Inject constructor() : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(DownloaderViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private var currentJob: Job? = null

    data class DownloaderViewState(
        val spotUrl: String = "",
        val ytUrl: String = "",
        val progress: Float = 0f,
        val isDownloading: Boolean = false,
        val isCancelled: Boolean = false,
        val songTitle: String = "",
        val songArtist: String = "",
        val isDownloadError: Boolean = false,
        val debugMode: Boolean = false,
        val showDownloadSettingDialog: Boolean = false,
        val downloadingTaskId: String = "",
        val isUrlSharingTriggered: Boolean = false,
        val drawerState: Boolean = false,
        /*val drawerState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            isSkipHalfExpanded = true
        ),*/
    )

    fun updateUrl(url: String, isUrlSharingTriggered: Boolean = false) =
        mutableStateFlow.update {
            it.copy(
                ytUrl = url,
                isUrlSharingTriggered = isUrlSharingTriggered
            )
        }
    fun hideDialog(scope: CoroutineScope, isDialog: Boolean) {
        scope.launch {
            if (isDialog)
                mutableStateFlow.update { it.copy(showDownloadSettingDialog = false) }
            else
                stateFlow.value.drawerState//.hide()
        }
    }

    fun showDialog(scope: CoroutineScope, isDialog: Boolean) {
        scope.launch {
            if (isDialog)
                mutableStateFlow.update { it.copy(showDownloadSettingDialog = true) }
            else
                stateFlow.value.drawerState//.show()
        }
    }

}