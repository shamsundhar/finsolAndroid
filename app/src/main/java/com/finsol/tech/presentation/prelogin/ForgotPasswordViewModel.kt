package com.finsol.tech.presentation.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.ForgotPasswordData
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
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordData: ForgotPasswordData
) : ViewModel() {


    private val _state = MutableStateFlow<ForgotPasswordViewState>(ForgotPasswordViewState.Init)
    val mState: StateFlow<ForgotPasswordViewState> get() = _state

    init {
//        fetchMarketData()
    }

    fun forgotPasword(userName: String) {
        viewModelScope.launch {
            forgotPasswordData.execute(userName).onStart {
                _state.value = ForgotPasswordViewState.IsLoading(true)
            }.catch {
                _state.value = ForgotPasswordViewState.IsLoading(false)
                _state.value = ForgotPasswordViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = ForgotPasswordViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        ForgotPasswordViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = ForgotPasswordViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = ForgotPasswordViewState.SuccessResponse(it.value)
                    }
                }
            }
        }
    }

}

sealed class ForgotPasswordViewState {
    object Init : ForgotPasswordViewState()
    data class IsLoading(val isLoading: Boolean) : ForgotPasswordViewState()
    data class ShowToast(val message: String) : ForgotPasswordViewState()
    data class SuccessResponse(val genericMessageResponse: GenericMessageResponse):ForgotPasswordViewState()
    data class ErrorResponse(val message: String) : ForgotPasswordViewState()
}
