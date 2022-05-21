package com.finsol.tech.presentation.buysell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.ResponseWrapper
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
class BuySellViewModel @Inject constructor(
    private val portfolioData: GetPortfolioData
) : ViewModel() {


    private val _state = MutableStateFlow<BuySellViewState>(BuySellViewState.Init)
    val mState: StateFlow<BuySellViewState> get() = _state


    fun placeBuyOrder(userID: String) {
        viewModelScope.launch {
            portfolioData.execute(userID).onStart {
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

    fun placeSellOrder(userID: String) {
        viewModelScope.launch {
            portfolioData.execute(userID).onStart {
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

}

sealed class BuySellViewState {
    object Init : BuySellViewState()
    data class IsLoading(val isLoading: Boolean) : BuySellViewState()
    data class ShowToast(val message: String) : BuySellViewState()
    object BuySuccessResponse : BuySellViewState()
    object SellSuccessResponse : BuySellViewState()
    data class ErrorResponse(val message: String) : BuySellViewState()
}
