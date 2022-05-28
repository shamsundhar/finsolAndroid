package com.finsol.tech.presentation.watchlist

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
class WatchListSymbolDetailsViewModel @Inject constructor(private val getMarketData: GetMarketData) :
    ViewModel() {

    private val _state =
        MutableStateFlow<WatchListSymbolDetailsState>(WatchListSymbolDetailsState.Init)
    val mState: StateFlow<WatchListSymbolDetailsState> get() = _state

    init {

    }

    fun fetchMarketData(securityID: String, exchangeName: String) {
        viewModelScope.launch {
            getMarketData.execute(securityID, exchangeName).onStart {
                _state.value = WatchListSymbolDetailsState.IsLoading(true)
            }.catch {
                _state.value = WatchListSymbolDetailsState.IsLoading(false)
                _state.value = WatchListSymbolDetailsState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = WatchListSymbolDetailsState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        WatchListSymbolDetailsState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = WatchListSymbolDetailsState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = WatchListSymbolDetailsState.MarketDataSuccessResponse(it.value)
                    }
                }
            }
        }

    }

    fun updateMarketDataFromSocket(it: Market) {
        _state.value = WatchListSymbolDetailsState.MarketDataSocketSuccessResponse(it)
    }

}

sealed class WatchListSymbolDetailsState {
    object Init : WatchListSymbolDetailsState()
    data class IsLoading(val isLoading: Boolean) : WatchListSymbolDetailsState()
    data class MarketDataSuccessResponse(val marketDetails: Market): WatchListSymbolDetailsState()
    data class MarketDataSocketSuccessResponse(val marketDetails: Market):
        WatchListSymbolDetailsState()

    data class ShowToast(val message: String) : WatchListSymbolDetailsState()

    data class ErrorResponse(val message: String) : WatchListSymbolDetailsState()
}