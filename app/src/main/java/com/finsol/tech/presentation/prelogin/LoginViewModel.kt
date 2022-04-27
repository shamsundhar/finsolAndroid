package com.finsol.tech.presentation.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.GetLoginData
import com.finsol.tech.domain.marketdata.GetMarketData
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(private val getLoginData: GetLoginData) : ViewModel() {


    private val _state = MutableStateFlow<LoginMarketViewState>(LoginMarketViewState.Init)
    val mState: StateFlow<LoginMarketViewState> get() = _state

    init {
//        fetchMarketData()
    }

    fun requestLogin(userID: String, password: String) {
        viewModelScope.launch {
            getLoginData.execute(userID,password).onStart {
                _state.value = LoginMarketViewState.IsLoading(true)
            }.catch {
                _state.value = LoginMarketViewState.IsLoading(false)
                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = LoginMarketViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        LoginMarketViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = LoginMarketViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = LoginMarketViewState.SuccessResponse(it.value)
                    }
                }
            }
        }
    }

//    private fun fetchMarketData() {
//        viewModelScope.launch {
//            getMarketData.execute().onStart {
//                _state.value = LoginMarketViewState.IsLoading(true)
//            }.catch {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
//            }.collect {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                when(it){
//                    is ResponseWrapper.NetworkError -> _state.value =
//                        LoginMarketViewState.ShowToast("Please check your network Conection!")
//                    is ResponseWrapper.GenericError -> {
//                        it.error?.let { msg ->
//                            _state.value = LoginMarketViewState.ShowToast(
//                                msg
//                            )
//                        }
//                    }
//                    is ResponseWrapper.Success -> {
//
//                    }
//                }
//            }
//        }
//
//    }
}

sealed class LoginMarketViewState {
    object Init : LoginMarketViewState()
    data class IsLoading(val isLoading: Boolean) : LoginMarketViewState()
    data class ShowToast(val message: String) : LoginMarketViewState()
    data class SuccessResponse(val loginResponseDomainModel: LoginResponseDomainModel) :
        LoginMarketViewState()

    data class ErrorResponse(val message: String) : LoginMarketViewState()
}