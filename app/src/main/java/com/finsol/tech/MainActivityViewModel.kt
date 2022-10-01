package com.finsol.tech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.Logout
import com.finsol.tech.domain.marketdata.SessionValidate
import com.finsol.tech.domain.marketdata.SessionValidateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val logout: Logout,
    private val sessionValidate: SessionValidate
) : ViewModel() {

    private val _state = MutableStateFlow<MainActivityViewState>(MainActivityViewState.Init)
    val mState: StateFlow<MainActivityViewState> get() = _state

    fun doLogout(userID: String) {
        viewModelScope.launch {
            logout.execute(userID).onStart {
                _state.value = MainActivityViewState.IsLoading(true)
            }.catch {
                _state.value = MainActivityViewState.IsLoading(false)
                _state.value = MainActivityViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = MainActivityViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        MainActivityViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = MainActivityViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = MainActivityViewState.LogoutSuccessResponse
                    }
                }
            }
        }
    }

    fun validateSession(userID: String) {
        viewModelScope.launch {
            sessionValidate.execute(userID).onStart {
                _state.value = MainActivityViewState.IsLoading(true)
            }.catch {
                _state.value = MainActivityViewState.IsLoading(false)
                _state.value = MainActivityViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = MainActivityViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        MainActivityViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = MainActivityViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = MainActivityViewState.sessionValidationResponse(it.value)
                    }
                }
            }
        }
    }
}

sealed class MainActivityViewState {
    object Init : MainActivityViewState()
    data class IsLoading(val isLoading: Boolean) : MainActivityViewState()
    data class ShowToast(val message: String) : MainActivityViewState()
    object LogoutSuccessResponse : MainActivityViewState()
    data class sessionValidationResponse(val sessionValidateResponse: SessionValidateResponse) :
        MainActivityViewState()

    data class ErrorResponse(val message: String) : MainActivityViewState()
}