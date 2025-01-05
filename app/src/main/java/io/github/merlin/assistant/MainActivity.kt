package io.github.merlin.assistant

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.merlin.assistant.ui.screen.main.MainScreen
import io.github.merlin.assistant.ui.theme.AssistantTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 获取系统window支持的模式
            val modes = window.windowManager.defaultDisplay.supportedModes
            // 对获取的模式，基于刷新率的大小进行排序，从小到大排序
            modes.sortedBy { it.refreshRate }.sortedBy { it.physicalWidth }
            window.let {
                val lp = it.attributes
                // 取出最大的那一个刷新率，直接设置给window
                val modeId = lp.preferredDisplayModeId
                lp.preferredDisplayModeId = modes.last().modeId
                it.attributes = lp
            }
        }
        setContent {
            AssistantTheme {
                MainScreen()
            }
        }
    }
}
