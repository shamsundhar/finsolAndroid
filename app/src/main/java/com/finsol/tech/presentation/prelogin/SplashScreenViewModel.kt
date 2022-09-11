package com.finsol.tech.presentation.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.*
import com.finsol.tech.domain.marketdata.GetExchangeEnumData
import com.finsol.tech.domain.marketdata.GetExchangeOptionsData
import com.finsol.tech.domain.marketdata.RegisterData
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getExchangeEnumData: GetExchangeEnumData,
    private val getExchangeOptionsData: GetExchangeOptionsData
) : ViewModel() {


    private val _state = MutableStateFlow<SplashScreenViewState>(SplashScreenViewState.Init)
    val mState: StateFlow<SplashScreenViewState> get() = _state

    fun getExchangeEnumData() {
        viewModelScope.launch {
            getExchangeEnumData.execute().onStart {
                _state.value = SplashScreenViewState.IsLoading(true)
            }.catch {
                _state.value = SplashScreenViewState.IsLoading(false)
                _state.value = SplashScreenViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = SplashScreenViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        SplashScreenViewState.ShowToast("Please check your network Connection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = SplashScreenViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = SplashScreenViewState.ExchangeEnumSuccessResponse(it.value)
                    }
                }
            }
        }
    }

    fun getExchangeOptionsData() {
        viewModelScope.launch {
            getExchangeOptionsData.execute().onStart {
                _state.value = SplashScreenViewState.IsLoading(true)
            }.catch {
                _state.value = SplashScreenViewState.IsLoading(false)
                _state.value = SplashScreenViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = SplashScreenViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        SplashScreenViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = SplashScreenViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value =
                            SplashScreenViewState.ExchangeEnumOptionsSuccessResponse(it.value)
                    }
                }
            }
        }
    }
    
    fun resetStateToDefault() {
        _state.value = SplashScreenViewState.Init
    }

}

sealed class SplashScreenViewState {
    object Init : SplashScreenViewState()
    data class IsLoading(val isLoading: Boolean) : SplashScreenViewState()
    data class ShowToast(val message: String) : SplashScreenViewState()
    data class ExchangeEnumSuccessResponse(val exchangeEnumData: Array<ExchangeEnumModel>) :
        SplashScreenViewState()
    data class ExchangeEnumOptionsSuccessResponse(val exchangeOptionsData: Array<ExchangeOptionsModel>) :
        SplashScreenViewState()
    data class SuccessResponse(val genericMessageResponse: GenericMessageResponse):SplashScreenViewState()
    data class ErrorResponse(val message: String) : SplashScreenViewState()
}
