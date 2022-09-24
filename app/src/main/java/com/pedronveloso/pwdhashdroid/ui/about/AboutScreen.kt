@file:OptIn(ExperimentalMaterial3Api::class)

package com.pedronveloso.pwdhashdroid.ui.about

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedronveloso.pwdhashdroid.BuildConfig
import com.pedronveloso.pwdhashdroid.R


@Composable
fun AboutScreen(onNavBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.about))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavBackPressed,
                        colors = IconButtonDefaults.filledIconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Filled.ArrowBack, stringResource(R.string.a11y_navigate_back))
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        }, content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val screenContents = buildScreenContents(context)
                AboutScreenContent(screenContents)
            }
        })
}

private fun buildScreenContents(context: Context): AboutScreenContents {
    val applicationInfo: ApplicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    val appName =
        if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    val appVersion = BuildConfig.VERSION_NAME
    val websiteUrl = "https://pedronveloso.com"
    val websiteLabel = "pedronveloso.com"
    return AboutScreenContents(
        appName = appName,
        appVersion = appVersion,
        websiteUrl = websiteUrl,
        websiteLabel = websiteLabel
    )
}

private class AboutScreenContents(
    val appName: String,
    val appVersion: String,
    val websiteUrl: String,
    websiteLabel: String
)

@Composable
private fun AboutScreenContent(screenContents: AboutScreenContents) {
    Spacer(modifier = Modifier.size(16.dp))
    Text(text = screenContents.appName, fontSize = 32.sp, fontWeight = FontWeight.Normal)
    Text(text = screenContents.appVersion, fontSize = 24.sp, fontWeight = FontWeight.Light)
}