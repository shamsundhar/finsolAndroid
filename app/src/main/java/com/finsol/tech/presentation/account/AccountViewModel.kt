package com.finsol.tech.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.Logout
import com.finsol.tech.domain.marketdata.PlaceBuyOrder
import com.finsol.tech.domain.marketdata.PlaceSellOrder
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import com.finsol.tech.domain.portfolio.GetPortfolioData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val logout: Logout
) : ViewModel() {


    private val _state = MutableStateFlow<AccountViewState>(AccountViewState.Init)
    val mState: StateFlow<AccountViewState> get() = _state


    fun doLogout(userID: String) {
        viewModelScope.launch {
            logout.execute(userID).onStart {
                _state.value = AccountViewState.IsLoading(true)
            }.catch {
                _state.value = AccountViewState.IsLoading(false)
                _state.value = AccountViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = AccountViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        AccountViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = AccountViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = AccountViewState.LogoutSuccessResponse
                    }
                }
            }
        }
    }
    fun resetStateToDefault() {
        _state.value = AccountViewState.Init
    }
}

sealed class AccountViewState {
    object Init : AccountViewState()
    data class IsLoading(val isLoading: Boolean) : AccountViewState()
    data class ShowToast(val message: String) : AccountViewState()
    object LogoutSuccessResponse : AccountViewState()
    data class ErrorResponse(val message: String) : AccountViewState()
}
