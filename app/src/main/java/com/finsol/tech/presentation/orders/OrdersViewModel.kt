package com.finsol.tech.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val pendingOrdersData: GetPendingOrdersData,
    private val orderHistoryData: GetOrderHistoryData
) : ViewModel() {


    private val _state = MutableStateFlow<OrdersViewState>(OrdersViewState.Init)
    val mState: StateFlow<OrdersViewState> get() = _state

    init {
//        fetchMarketData()
    }

    fun requestPendingOrderDetails(userID: String) {
        viewModelScope.launch {
            pendingOrdersData.execute(userID).onStart {
                _state.value = OrdersViewState.IsLoading(true)
            }.catch {
                _state.value = OrdersViewState.IsLoading(false)
                _state.value = OrdersViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = OrdersViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        OrdersViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = OrdersViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = OrdersViewState.PendingOrdersSuccessResponse(it.value)
                    }
                }
            }
        }
    }

    fun requestOrderHistoryDetails(userID: String) {
        viewModelScope.launch {
            orderHistoryData.execute(userID).onStart {
                _state.value = OrdersViewState.IsLoading(true)
            }.catch {
                _state.value = OrdersViewState.IsLoading(false)
                _state.value = OrdersViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = OrdersViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        OrdersViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = OrdersViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = OrdersViewState.OrderHistorySuccessResponse(it.value)
                    }
                }
            }
        }
    }
    fun resetStateToDefault() {
        _state.value = OrdersViewState.Init
    }
}

sealed class OrdersViewState {
    object Init : OrdersViewState()
    data class IsLoading(val isLoading: Boolean) : OrdersViewState()
    data class ShowToast(val message: String) : OrdersViewState()
    data class PendingOrdersSuccessResponse(val pendingOrdersArray: Array<PendingOrderModel>) :
        OrdersViewState()
    data class OrderHistorySuccessResponse(val orderHistoryArray:Array<OrderHistoryModel>):OrdersViewState()

    data class ErrorResponse(val message: String) : OrdersViewState()
}
