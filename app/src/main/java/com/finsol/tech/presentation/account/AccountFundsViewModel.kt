package com.finsol.tech.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.ChangePasswordData
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import com.finsol.tech.domain.profile.AddFundsData
import com.finsol.tech.domain.profile.WithdrawFundsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AccountFundsViewModel @Inject constructor(
    private val addFundsData: AddFundsData,
    private val withdrawFundsData: WithdrawFundsData
) : ViewModel() {


    private val _state = MutableStateFlow<FundsViewState>(FundsViewState.Init)
    val mState: StateFlow<FundsViewState> get() = _state

    init {
    }

    fun addFunds(userName: String) {
        viewModelScope.launch {
            addFundsData.execute(userName).onStart {
                _state.value = FundsViewState.IsLoading(true)
            }.catch {
                _state.value = FundsViewState.IsLoading(false)
                _state.value = FundsViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = FundsViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        FundsViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = FundsViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = FundsViewState.AddFundsSuccessResponse(it.value)
                    }
                }
            }
        }
    }
    fun withdrawFunds(userName: String) {
        viewModelScope.launch {
            withdrawFundsData.execute(userName).onStart {
                _state.value = FundsViewState.IsLoading(true)
            }.catch {
                _state.value = FundsViewState.IsLoading(false)
                _state.value = FundsViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = FundsViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        FundsViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = FundsViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = FundsViewState.WithdrawFundsSuccessResponse(it.value)
                    }
                }
            }
        }
    }

    fun resetStateToDefault() {
        _state.value = FundsViewState.Init
    }

}

sealed class FundsViewState {
    object Init : FundsViewState()
    data class IsLoading(val isLoading: Boolean) : FundsViewState()
    data class ShowToast(val message: String) : FundsViewState()
    data class AddFundsSuccessResponse(val genericMessageResponse: GenericMessageResponse) :
        FundsViewState()
    data class WithdrawFundsSuccessResponse(val genericMessageResponse: GenericMessageResponse) :
        FundsViewState()
    data class ErrorResponse(val message: String) : FundsViewState()
}
