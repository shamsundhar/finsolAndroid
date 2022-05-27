package com.finsol.tech.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.orders.GetOrderHistoryData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import com.finsol.tech.domain.portfolio.GetPortfolioData
import com.finsol.tech.presentation.orders.OrdersViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val portfolioData: GetPortfolioData
) : ViewModel() {


    private val _state = MutableStateFlow<PortfolioViewState>(PortfolioViewState.Init)
    val mState: StateFlow<PortfolioViewState> get() = _state


    fun requestPortfolioDetails(userID: String) {
        viewModelScope.launch {
            portfolioData.execute(userID).onStart {
                _state.value = PortfolioViewState.IsLoading(true)
            }.catch {
                _state.value = PortfolioViewState.IsLoading(false)
                _state.value = PortfolioViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = PortfolioViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        PortfolioViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = PortfolioViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = PortfolioViewState.PortfolioSuccessResponse(it.value)
                    }
                }
            }
        }
    }
    fun resetStateToDefault() {
        _state.value = PortfolioViewState.Init
    }
}

sealed class PortfolioViewState {
    object Init : PortfolioViewState()
    data class IsLoading(val isLoading: Boolean) : PortfolioViewState()
    data class ShowToast(val message: String) : PortfolioViewState()
    data class PortfolioSuccessResponse(val portfolioResponse: PortfolioResponse) :
        PortfolioViewState()
    data class ErrorResponse(val message: String) : PortfolioViewState()
}
