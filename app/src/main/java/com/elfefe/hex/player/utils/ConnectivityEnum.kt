package com.elfefe.hex.player.utils

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult

sealed class Connection {
    class Success(val result: String) : Connection()
    class Failure(val error: Exception) : Connection()
    class Loading() : Connection()
}

sealed class GoogleOneTap: Connection() {
    class Success(val result: BeginSignInResult) : GoogleOneTap()
    class Failure(val error: Exception) : GoogleOneTap()
    class Loading(val progress: Int = 0, val max: Int = 0) : GoogleOneTap()
}

sealed class Authentication: Connection() {
    class Success() : Authentication()
    class Failure(val error: Exception?) : Authentication()
    class Pending(val progress: Int = 0, val max: Int = 0) : Authentication()
}