package com.elfefe.hex.player.mvvm

import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfefe.hex.player.mvvm.model.Friend
import com.elfefe.hex.player.mvvm.model.Player
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.utils.Authentication
import com.elfefe.hex.player.utils.GoogleOneTap
import com.elfefe.hex.player.utils.app
import com.elfefe.hex.player.utils.extensions.ViewModelExtension
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FirebaseViewModel : ViewModelExtension() {
    private val repository = app.apiRepository

    private val _googleSignIn = MutableLiveData<GoogleOneTap>()
    private val _authentication = MutableLiveData<Authentication>()
    val authentication: LiveData<Authentication>
        get() = _authentication

    fun StartActivity.googleSign(): LiveData<GoogleOneTap> =
        repository.run {
            _googleSignIn.apply {
                googleSignIn
                    .onEach {
                        postValue(it)
                    }.launchIn(viewModelScope)
            }
        }

    fun authentication(result: ActivityResult, client: SignInClient): LiveData<Authentication> =
        repository
            .firebaseAuthentication(result, client)
            .run {
                _authentication.apply {
                    onEach {
                        postValue(it)
                    }.launchIn(viewModelScope)
                }
            }

    fun friends(): LiveData<List<Friend>> =
        repository
            .friends
            .run {
                MutableLiveData<List<Friend>>().apply {
                    onEach {
                        postValue(it)
                    }.launchIn(viewModelScope)
                }
            }

    fun players(): LiveData<List<Player>> =
        repository
            .players
            .run {
                MutableLiveData<List<Player>>().apply {
                    onEach {
                        postValue(it)
                    }.launchIn(viewModelScope)
                }
            }

    fun askGameMaster() {
        repository.askGameMaster()
    }
}