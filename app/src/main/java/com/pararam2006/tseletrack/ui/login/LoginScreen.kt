package com.pararam2006.tseletrack.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pararam2006.tseletrack.core.Role

@Composable
fun LoginScreen(
    onLoginSuccess: (Role) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

    }
}