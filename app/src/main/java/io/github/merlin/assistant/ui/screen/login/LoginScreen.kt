package io.github.merlin.assistant.ui.screen.login

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import io.github.merlin.assistant.ui.base.LaunchedEvent

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
    val viewModel: LoginViewModel = hiltViewModel()

    LaunchedEvent(viewModel = viewModel) { event ->
        when (event) {
            LoginEvent.NavigateBack -> navController.popBackStack()
            is LoginEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
                        viewModel.trySendAction(LoginAction.LoginMenuClick(cookie))
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
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setCookie(
                        initialUrl,
                        "_qpsvr_localtk=0.4957078562492454; RK=2+tZD/++1+; ptcz=2ba32ea72555e684298f591f9e213f642b1f5ea67813baa7a4d2e3c7a1af9f86; qmdld_login_report=1; policyplay=0; h5game_appid=1105748669; h5game_accesstoken=6BEF5E982B7326E2DB69BBE07AA4E369; h5game_openid=C66C58092F87266326EEC5F33A649967; h5game_paytoken=E65EB2A6F24686F828212AD434ABF612"
                    )
                    webView.settings.javaScriptEnabled = true
                    webView.settings.userAgentString = userAgent
                    webView.settings.domStorageEnabled = true
                }
            )
        }
    }

}