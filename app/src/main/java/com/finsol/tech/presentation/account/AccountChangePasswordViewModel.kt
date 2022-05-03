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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AccountChangePasswordViewModel @Inject constructor(
    private val changePasswordData: ChangePasswordData
) : ViewModel() {


    private val _state = MutableStateFlow<ChangePasswordViewState>(ChangePasswordViewState.Init)
    val mState: StateFlow<ChangePasswordViewState> get() = _state

    init {
    }

    fun requestChangePassword(userID: String, userName: String, newPassword: String) {
        viewModelScope.launch {
            changePasswordData.execute(userID, userName, newPassword).onStart {
                _state.value = ChangePasswordViewState.IsLoading(true)
            }.catch {
                _state.value = ChangePasswordViewState.IsLoading(false)
                _state.value = ChangePasswordViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = ChangePasswordViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        ChangePasswordViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = ChangePasswordViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = ChangePasswordViewState.SuccessResponse(it.value)
                    }
                }
            }
        }
    }

}

sealed class ChangePasswordViewState {
    object Init : ChangePasswordViewState()
    data class IsLoading(val isLoading: Boolean) : ChangePasswordViewState()
    data class ShowToast(val message: String) : ChangePasswordViewState()
    data class SuccessResponse(val genericMessageResponse: GenericMessageResponse) :
        ChangePasswordViewState()
    data class ErrorResponse(val message: String) : ChangePasswordViewState()
}
