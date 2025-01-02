package io.github.merlin.assistant.ui.screen.login

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val initialUrl = "https://res.ledou.qq.com/login.html?_wv=181008&pf=sq&from=1&h5sdkqqconnect=1"
    val userAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
    val webViewState = rememberWebViewState(url = initialUrl)
    val loadingState = webViewState.loadingState
    val webViewNavigator = rememberWebViewNavigator()
    val loginViewModel: LoginViewModel = hiltViewModel()

    LaunchedEffect(key1 = loginViewModel) {
        loginViewModel.eventFlow.onEach { event ->
            when (event) {
                LoginEvent.NavigateBack -> navController.popBackStack()
                is LoginEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT)
                    .show()
            }
        }.launchIn(this)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = ""
                        )
                    }
                },
                title = { Text("登录") },
                actions = {
                    IconButton(onClick = {
                        CookieManager.getInstance().removeAllCookies {}
                        webViewNavigator.reload()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "")
                    }
                    IconButton(onClick = {
                        val cookie = CookieManager.getInstance().getCookie(initialUrl)
                        loginViewModel.trySendAction(LoginAction.LoginMenuClick(cookie))
                    }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (loadingState is LoadingState.Loading) {
                LinearProgressIndicator(
                    progress = { loadingState.progress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            WebView(
                state = webViewState,
                navigator = webViewNavigator,
                modifier = Modifier.fillMaxSize(),
                onCreated = { webView ->
                    webView.settings.javaScriptEnabled = true
                    webView.settings.userAgentString = userAgent
                    webView.settings.domStorageEnabled = true
                }
            )
        }
    }

}