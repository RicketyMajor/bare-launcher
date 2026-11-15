package dev.ricketymajor.bare.home

import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HomeActivity : ComponentActivity() {

    private val launcherApps by lazy { getSystemService(LauncherApps::class.java) }
    private var apps by mutableStateOf(emptyList<LauncherActivityInfo>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this) { } // back on home does nothing (SPEC 5.5)
        setContent { AppList(apps, ::open) }
    }

    override fun onResume() {
        super.onResume()
        // ponytail: reload on every resume; LauncherApps.Callback in F1 (M0) for live install/uninstall
        apps = launcherApps.getActivityList(null, Process.myUserHandle())
            .sortedBy { it.label.toString().lowercase() }
    }

    private fun open(app: LauncherActivityInfo) =
        launcherApps.startMainActivity(app.componentName, app.user, null, null)
}

@Composable
private fun AppList(apps: List<LauncherActivityInfo>, onOpen: (LauncherActivityInfo) -> Unit) {
    val style = TextStyle(color = Color.White, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        items(apps, key = { it.componentName.flattenToString() }) { app ->
            BasicText(
                text = app.label.toString().lowercase(),
                style = style,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(app) }
                    .padding(vertical = 14.dp),
            )
        }
    }
}
