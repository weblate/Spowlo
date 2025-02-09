package com.bobbyesp.spowlo.ui.pages.settings.appearance

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.PreferenceSingleChoiceItem
import com.bobbyesp.spowlo.ui.components.PreferencesHintCard
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import com.bobbyesp.spowlo.utils.LANGUAGE
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.getLanguageConfiguration
import com.bobbyesp.spowlo.utils.SYSTEM_DEFAULT
import com.bobbyesp.spowlo.utils.getLanguageDesc
import com.bobbyesp.spowlo.utils.languageMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePage(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    var language by remember { mutableStateOf(PreferencesUtil.getLanguageNumber()) }
    val uriHandler = LocalUriHandler.current

    val spowloWeblateUrl = "https://hosted.weblate.org/engage/spowlo/"

    fun setLanguage(selectedLanguage: Int) {
        language = selectedLanguage
        PreferencesUtil.encodeInt(LANGUAGE, language)
        MainActivity.setLanguage(getLanguageConfiguration())
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.language),
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .selectableGroup()
            ) {
                item {
                    PreferencesHintCard(
                        title = stringResource(R.string.translate),
                        description = stringResource(R.string.translate_desc),
                        icon = Icons.Outlined.Translate,
                        isDarkTheme = LocalDarkTheme.current.isDarkTheme()
                    ) { ChromeCustomTabsUtil.openUrl(spowloWeblateUrl) }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.follow_system),
                        selected = language == SYSTEM_DEFAULT,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 18.dp)
                    ) { setLanguage(SYSTEM_DEFAULT) }
                }
                for (languageData in languageMap) {
                    item {
                        PreferenceSingleChoiceItem(
                            text = getLanguageDesc(languageData.key),
                            selected = language == languageData.key,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 18.dp)
                        ) { setLanguage(languageData.key) }
                    }
                }
            }
        })
}