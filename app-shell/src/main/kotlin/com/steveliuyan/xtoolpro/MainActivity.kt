package com.steveliuyan.xtoolpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.steveliuyan.xtoolpro.core.model.AppShellState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XToolproApp(AppShellState.initial())
        }
    }
}

@Composable
@Suppress("ktlint:standard:function-naming")
private fun XToolproApp(state: AppShellState) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.layout.Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (state == AppShellState.Foundation) {
                    Text(text = stringResource(R.string.foundation_state))
                }
            }
        }
    }
}
