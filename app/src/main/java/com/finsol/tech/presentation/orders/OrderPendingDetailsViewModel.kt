package com.finsol.tech.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.CancelOrder
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import com.finsol.tech.presentation.buysell.BuySellViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OrderPendingDetailsViewModel @Inject constructor(
    private val cancelOrder: CancelOrder
) : ViewModel() {


    private val _state = MutableStateFlow<PendingOrderDetailsViewState>(PendingOrderDetailsViewState.Init)
    val mState: StateFlow<PendingOrderDetailsViewState> get() = _state

    fun cancelOrder(uniqueOrderID: String) {
        viewModelScope.launch {
            cancelOrder.execute(uniqueOrderID).onStart {
                _state.value = PendingOrderDetailsViewState.IsLoading(true)
            }.catch {
                _state.value = PendingOrderDetailsViewState.IsLoading(false)
                _state.value = PendingOrderDetailsViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = PendingOrderDetailsViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        PendingOrderDetailsViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = PendingOrderDetailsViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = PendingOrderDetailsViewState.CancelOrderSuccessResponse
                    }
                }
            }
        }
    }

//    fun modifyOrder(userID: String) {
//        viewModelScope.launch {
//            orderHistoryData.execute(userID).onStart {
//                _state.value = PendingOrderDetailsViewState.IsLoading(true)
//            }.catch {
//                _state.value = PendingOrderDetailsViewState.IsLoading(false)
//                _state.value = PendingOrderDetailsViewState.ErrorResponse("UnknownError")
//            }.collect {
//                _state.value = PendingOrderDetailsViewState.IsLoading(false)
//                when (it) {
//                    is ResponseWrapper.NetworkError -> _state.value =
//                        PendingOrderDetailsViewState.ShowToast("Please check your network Conection!")
//                    is ResponseWrapper.GenericError -> {
//                        it.error?.let { msg ->
//                            _state.value = PendingOrderDetailsViewState.ShowToast(
//                                msg
//                            )
//                        }
//                    }
//                    is ResponseWrapper.Success -> {
//                        _state.value = PendingOrderDetailsViewState.OrderHistorySuccessResponse(it.value)
//                    }
//                }
//            }
//        }
//    }
fun resetStateToDefault() {
    _state.value = PendingOrderDetailsViewState.Init
}
}

sealed class PendingOrderDetailsViewState {
    object Init : PendingOrderDetailsViewState()
    data class IsLoading(val isLoading: Boolean) : PendingOrderDetailsViewState()
    data class ShowToast(val message: String) : PendingOrderDetailsViewState()
    object CancelOrderSuccessResponse : PendingOrderDetailsViewState()
    object ModifyOrderSuccessResponse:PendingOrderDetailsViewState()

    data class ErrorResponse(val message: String) : PendingOrderDetailsViewState()
}
