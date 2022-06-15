package com.finsol.tech.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.marketdata.GetLoginData
import com.finsol.tech.domain.marketdata.GetMarketData
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.presentation.orders.PendingOrderDetailsViewState
import com.finsol.tech.presentation.prelogin.LoginMarketViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PortfolioDetailsViewModel @Inject constructor(private val getMarketData: GetMarketData) :
    ViewModel() {

    private val _state =
        MutableStateFlow<PortfolioDetailsState>(PortfolioDetailsState.Init)
    val mState: StateFlow<PortfolioDetailsState> get() = _state

    init {

    }

    fun fetchMarketData(securityID: String, exchangeName: String) {
        viewModelScope.launch {
            getMarketData.execute(securityID, exchangeName).onStart {
                _state.value = PortfolioDetailsState.IsLoading(true)
            }.catch {
                _state.value = PortfolioDetailsState.IsLoading(false)
                _state.value = PortfolioDetailsState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = PortfolioDetailsState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        PortfolioDetailsState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = PortfolioDetailsState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = PortfolioDetailsState.MarketDataSuccessResponse(it.value)
                    }
                }
            }
        }

    }

    fun updateMarketDataFromSocket(it: Market) {
        _state.value = PortfolioDetailsState.MarketDataSocketSuccessResponse(it)
    }

}

sealed class PortfolioDetailsState {
    object Init : PortfolioDetailsState()
    data class IsLoading(val isLoading: Boolean) : PortfolioDetailsState()
    data class MarketDataSuccessResponse(val marketDetails: Market): PortfolioDetailsState()
    data class MarketDataSocketSuccessResponse(val marketDetails: Market):
        PortfolioDetailsState()

    data class ShowToast(val message: String) : PortfolioDetailsState()

    data class ErrorResponse(val message: String) : PortfolioDetailsState()
}