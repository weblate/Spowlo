package com.bobbyesp.spowlo.presentation.ui.pages.downloader_page

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.bobbyesp.spowlo.data.auth.AuthModel
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyPkceLoginActivityImpl
import com.bobbyesp.spowlo.domain.spotify.web_api.utilities.guardValidSpotifyApi
import com.bobbyesp.spowlo.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearcherViewModel @Inject constructor() : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(SearcherViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private var currentJob: Job? = null

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery


    data class SearcherViewState(
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
        val logged: Boolean = false,
        val pageLoaded: Boolean = false,
        val recentBroadcasts: List<SpotifyBroadcastEventData> = mutableListOf(),
        val listOfTracks: List<Track> = mutableListOf(),
        val isSearching: Boolean = false,
        val isSearchError: Boolean = false,
        val activity: Activity? = MainActivity(),
        /*val drawerState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            isSkipHalfExpanded = true
        ),*/
    )

    fun setup(activity: Activity? = SearcherViewState().activity) {
        currentJob = CoroutineScope(Job()).launch {
            mutableStateFlow.update {
                if (AuthModel.credentialStore.spotifyToken != null) {
                    it.copy(logged = true)
                } else {
                    it.copy(logged = false)
                }
            }
            activity?.guardValidSpotifyApi(MainActivity::class.java) { api ->
                if(!api.isTokenValid(true).isValid)
                    throw SpotifyException.ReAuthenticationNeededException()
                mutableStateFlow.update {
                    it.copy(logged = true)
                }
            }
            mutableStateFlow.update {
                it.copy(pageLoaded = true)
            }
        }
    }

    fun onSearch(query: String, activity: Activity? = SearcherViewState().activity) {
        Log.d("DownloaderViewModel", "onSearch: $activity")
        mutableStateFlow.update {
            it.copy(isSearching = true)
        }
        _searchQuery.value = query
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(500L)
            try {
                val tracks =
                    activity?.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                        //if query is not empty, search for it
                        if (query.isNotEmpty()) {
                            api.search.searchTrack(query).items
                        } else {
                            //if query is empty, make the tracks list empty
                            listOf()
                        }
                    }
                mutableStateFlow.update {
                    it.copy(listOfTracks = tracks ?: listOf(), isSearching = false)
                }
                Log.d("DownloaderViewModel", "Search query: $tracks")
            } catch (e: Exception) {
                Log.d("DownloaderViewModel", "Error: $e")
                mutableStateFlow.update {
                    it.copy(isSearchError = true, isSearching = false)
                }
            }
        }
    }

    fun spotifyPkceLogin(activity: Activity? = null) {
        activity?.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
    }

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