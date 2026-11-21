package com.bscan.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bscan.app.core.ui.theme.BscanTheme
import com.bscan.app.navigation.BscanRootApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        val appContainer = (application as BscanApplication).container
        setContent {
            BscanTheme(darkTheme = true) {
                BscanRootApp(container = appContainer)
            }
        }
    }
}
