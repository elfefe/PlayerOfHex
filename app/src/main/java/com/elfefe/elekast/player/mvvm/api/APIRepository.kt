package com.elfefe.elekast.player.mvvm.api

import androidx.activity.result.ActivityResult
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.utils.GoogleOneTap
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class APIRepository(val scope: CoroutineScope) {
    private val firebase = FirebaseAPI(scope)

    val StartActivity.googleSignIn: StateFlow<GoogleOneTap>
        get() = firebase.run { signIn() }

    fun firebaseAuthentication(result: ActivityResult, client: SignInClient) = firebase.authenticate(result, client)
}