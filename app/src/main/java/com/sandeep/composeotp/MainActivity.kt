package com.sandeep.composeotp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sandeep.composeotp.ui.theme.MyApplicationTheme
import com.sandeep.composeotp.ui.theme.PLCodingGray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PLCodingGray
                ) { innerPadding ->
                    val viewModel = viewModel<OtpViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val focusRequesters = remember {
                        (1..4).map { FocusRequester() }
                    }
                    val focusManager = LocalFocusManager.current
                    val keyboardManger = LocalSoftwareKeyboardController.current

                    LaunchedEffect(state.focusedIndex) {
                        state.focusedIndex?.let { index ->
                            focusRequesters.getOrNull(index)?.requestFocus()
                        }
                    }
                    LaunchedEffect(state.code, keyboardManger) {
                        val allNumbersEntered = state.code.none { it == null }
                        if (allNumbersEntered) {
                            focusRequesters.forEach {
                                it.freeFocus()
                            }
                            focusManager.clearFocus()
                            keyboardManger?.hide()
                        }
                    }
                    OtpScreen(
                        state = state,
                        onAction = { action ->
                            when (action) {
                                is OtpAction.OnEnterNumber -> {
                                    if (action.number != null) {
                                        focusRequesters[action.index].freeFocus()
                                    }
                                }

                                else -> Unit
                            }
                            viewModel.onAction(action)
                        },
                        focusedRequesters = focusRequesters,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }
}