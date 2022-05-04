package com.finsol.tech.presentation.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.ResponseWrapper
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
class RegisterViewModel @Inject constructor(
    private val registerData: RegisterData
) : ViewModel() {


    private val _state = MutableStateFlow<RegisterViewState>(RegisterViewState.Init)
    val mState: StateFlow<RegisterViewState> get() = _state

    init {
//        fetchMarketData()
    }

    fun register(name: String, email: String, phone: String) {
        viewModelScope.launch {
            registerData.execute(name, email, phone).onStart {
                _state.value = RegisterViewState.IsLoading(true)
            }.catch {
                _state.value = RegisterViewState.IsLoading(false)
                _state.value = RegisterViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = RegisterViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        RegisterViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = RegisterViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = RegisterViewState.SuccessResponse(it.value)
                    }
                }
            }
        }
    }

}

sealed class RegisterViewState {
    object Init : RegisterViewState()
    data class IsLoading(val isLoading: Boolean) : RegisterViewState()
    data class ShowToast(val message: String) : RegisterViewState()
    data class SuccessResponse(val genericMessageResponse: GenericMessageResponse):RegisterViewState()
    data class ErrorResponse(val message: String) : RegisterViewState()
}
