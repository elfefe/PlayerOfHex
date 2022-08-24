package com.elfefe.hex.player.ui.component

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfefe.hex.player.R
import com.elfefe.hex.player.mvvm.FirebaseViewModel
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.ui.theme.IntroButton
import com.elfefe.hex.player.ui.theme.Subtitle
import com.elfefe.hex.player.utils.*
import com.elfefe.hex.player.utils.extensions.GAMER
import com.elfefe.hex.player.utils.extensions.START
import com.elfefe.hex.player.utils.extensions.resString
import kotlinx.coroutines.delay


class StartComponent(override val activity: StartActivity) : BaseComponent(activity) {
    override val destination = START

    private val firebaseViewModel by activity.viewModels<FirebaseViewModel>()

    override fun NavGraphBuilder.compose() =
        composable(destination) {
            Animated(exit = TRANSITION) {
                Start()
            }
        }

    @Composable
    fun Start() {
        setComponent()

        Column(
            modifier = Modifier
                .fillMaxWidth(.8f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isClickable = true
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    loge = "Activity result ${result.resultCode == Activity.RESULT_OK}"
                    firebaseViewModel.authentication(result, activity.oneTapClient)
                        .observe(activity) {
                            when (it) {
                                is Authentication.Success -> activity.navigateTo(GAMER)
                                is Authentication.Failure -> {
                                    this@StartComponent.loge =
                                        "Could not connect to your google account.\n${it.error?.localizedMessage}"
                                    isClickable = true
                                }
                                is Authentication.Pending -> {
                                    this@StartComponent.loge =
                                        "Downloading ${it.progress} / ${it.max}"
                                }
                            }
                        }
                }
            Column(
                modifier = Modifier
                    .fillMaxHeight(.6f)
                    .fillMaxWidth(.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Subtitle(text = "Welcome in the realms of Elekast !\nPlease authenticate your self.\nI need to know a little more about you..")
            }
            var showButton by remember { mutableStateOf(false) }
            LaunchedEffect(null) {
                delay(TRANSITION.toLong())
                showButton = true
            }

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(transition(TRANSITION))
            ) {
                IntroButton(text = "Authenticate") {
                    loge = "Is authentication button clickable ? $isClickable"
                    if (isClickable)
                        firebaseViewModel.run {
                            isClickable = false
                            activity.googleSign().observe(activity) { oneTap ->
                                loge = "OneTap status $oneTap"
                                when (oneTap) {
                                    is GoogleOneTap.Success -> {
                                        this@StartComponent.logd =
                                            resString(R.string.onetap_connection_success)
                                        val intent =
                                            IntentSenderRequest.Builder(oneTap.result.pendingIntent.intentSender)
                                                .build()
                                        launcher.launch(intent)
                                    }
                                    is GoogleOneTap.Failure -> {
                                        this@StartComponent.loge =
                                            resString(
                                                R.string.onetap_connection_failure,
                                                oneTap.error.localizedMessage
                                            )
                                        isClickable = true
                                    }
                                    else -> {
                                        this@StartComponent.logw =
                                            resString(R.string.onetap_connection_trying)
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}