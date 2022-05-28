package com.finsol.tech.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.CancelOrder
import com.finsol.tech.domain.marketdata.GetMarketData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OrderPendingDetailsViewModel @Inject constructor(
    private val cancelOrder: CancelOrder,
    private val getMarketData: GetMarketData
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
                    is ResponseWrapper.Success2 -> {
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

    fun fetchMarketData(securityID: String, exchangeName: String) {
        viewModelScope.launch {
            getMarketData.execute(securityID, exchangeName).onStart {
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
                        _state.value = PendingOrderDetailsViewState.MarketDataSuccessResponse(it.value)
                    }
                }
            }
        }

    }
    
fun updateMarketDataFromSocket(it: Market) {
    _state.value = PendingOrderDetailsViewState.MarketDataSocketSuccessResponse(it)
}
    
fun resetStateToDefault() {
    _state.value = PendingOrderDetailsViewState.Init
}
}

sealed class PendingOrderDetailsViewState {
    object Init : PendingOrderDetailsViewState()
    data class IsLoading(val isLoading: Boolean) : PendingOrderDetailsViewState()
    data class ShowToast(val message: String) : PendingOrderDetailsViewState()
    data class MarketDataSuccessResponse(val marketDetails: Market):PendingOrderDetailsViewState()
    data class MarketDataSocketSuccessResponse(val marketDetails: Market):PendingOrderDetailsViewState()
    object CancelOrderSuccessResponse : PendingOrderDetailsViewState()
    object ModifyOrderSuccessResponse:PendingOrderDetailsViewState()

    data class ErrorResponse(val message: String) : PendingOrderDetailsViewState()
}
