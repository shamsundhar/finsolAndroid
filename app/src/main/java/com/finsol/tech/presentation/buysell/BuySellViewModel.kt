package com.finsol.tech.presentation.buysell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.ModifyOrder
import com.finsol.tech.domain.marketdata.PlaceBuyOrder
import com.finsol.tech.domain.marketdata.PlaceSellOrder
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import com.finsol.tech.domain.portfolio.GetPortfolioData
import com.finsol.tech.presentation.account.ChangePasswordViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class BuySellViewModel @Inject constructor(
    private val buyOrder: PlaceBuyOrder,
    private val sellOrder: PlaceSellOrder,
    private val modifyOrder: ModifyOrder
) : ViewModel() {


    private val _state = MutableStateFlow<BuySellViewState>(BuySellViewState.Init)
    val mState: StateFlow<BuySellViewState> get() = _state


    fun placeBuyOrder(securityID: String, userID: String, orderType: String, timeInForce: String, price: String, quantity: String, trigger:String?) {
        viewModelScope.launch {
            buyOrder.execute(securityID, userID, orderType, timeInForce, price, quantity, trigger).onStart {
                _state.value = BuySellViewState.IsLoading(true)
            }.catch {
                _state.value = BuySellViewState.IsLoading(false)
                _state.value = BuySellViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = BuySellViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        BuySellViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = BuySellViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = BuySellViewState.BuySuccessResponse
                    }
                }
            }
        }
    }

    fun placeSellOrder(securityID: String, userID: String, orderType: String, timeInForce: String, price: String, quantity: String, trigger: String?) {
        viewModelScope.launch {
            sellOrder.execute(securityID, userID, orderType, timeInForce, price, quantity, trigger).onStart {
                _state.value = BuySellViewState.IsLoading(true)
            }.catch {
                _state.value = BuySellViewState.IsLoading(false)
                _state.value = BuySellViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = BuySellViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        BuySellViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = BuySellViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = BuySellViewState.SellSuccessResponse
                    }
                }
            }
        }
    }
    fun modifyOrder(uniqueOrderID: String, stopPrice: String?, price: String, quantity: String) {
        viewModelScope.launch {
            modifyOrder.execute(uniqueOrderID, stopPrice, price, quantity).onStart {
                _state.value = BuySellViewState.IsLoading(true)
            }.catch {
                _state.value = BuySellViewState.IsLoading(false)
                _state.value = BuySellViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = BuySellViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        BuySellViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = BuySellViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success2 -> {
                        _state.value = BuySellViewState.modifySuccessResponse
                    }
                }
            }
        }
    }
    fun resetStateToDefault() {
        _state.value = BuySellViewState.Init
    }
}

sealed class BuySellViewState {
    object Init : BuySellViewState()
    data class IsLoading(val isLoading: Boolean) : BuySellViewState()
    data class ShowToast(val message: String) : BuySellViewState()
    object BuySuccessResponse : BuySellViewState()
    object SellSuccessResponse : BuySellViewState()
    object modifySuccessResponse : BuySellViewState()
    data class ErrorResponse(val message: String) : BuySellViewState()
}
