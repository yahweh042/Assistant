package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    loadingDialogState: LoadingDialogState
) {

    when (loadingDialogState) {
        LoadingDialogState.Nothing -> Unit

        is LoadingDialogState.Loading -> BasicAlertDialog(
            onDismissRequest = {},
        ) {
            Card(shape = ShapeDefaults.Medium) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    loadingDialogState.msg?.let {
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = it)
                    }
                }
            }
        }

        is LoadingDialogState.Error -> Unit
    }

}

sealed class LoadingDialogState {
    data object Nothing : LoadingDialogState()
    data class Loading(val msg: String? = null) : LoadingDialogState()
    data class Error(val error: String) : LoadingDialogState()
}