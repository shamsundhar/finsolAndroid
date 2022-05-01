package com.finsol.tech.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.contracts.AddToWatchListData
import com.finsol.tech.domain.contracts.GetAllContractsData
import com.finsol.tech.domain.marketdata.GetLoginData
import com.finsol.tech.domain.marketdata.GetMarketData
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.domain.profile.GetProfileData
import com.finsol.tech.presentation.prelogin.LoginMarketViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WatchListSearchViewModel @Inject constructor(
    private val addToWatchListData: AddToWatchListData
    ) : ViewModel() {


    private val _state = MutableStateFlow<WatchListSearchViewState>(WatchListSearchViewState.Init)
    val mState: StateFlow<WatchListSearchViewState> get() = _state

    fun addToWatchList(userID: String, watchListNumber: String, securityID:String) {
        viewModelScope.launch {
            addToWatchListData.execute(userID, watchListNumber, securityID).onStart {
                _state.value = WatchListSearchViewState.IsLoading(true)
            }.catch {
                _state.value = WatchListSearchViewState.IsLoading(false)
                _state.value = WatchListSearchViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = WatchListSearchViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        WatchListSearchViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = WatchListSearchViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = WatchListSearchViewState.AddToWatchListSuccessResponse(it.value)
                    }
                }
            }
        }
    }


}

sealed class WatchListSearchViewState {
    object Init : WatchListSearchViewState()
    data class IsLoading(val isLoading: Boolean) : WatchListSearchViewState()
    data class ShowToast(val message: String) : WatchListSearchViewState()
    data class AddToWatchListSuccessResponse(val genericMessageResponse: GenericMessageResponse) :
        WatchListSearchViewState()
    data class ErrorResponse(val message: String) : WatchListSearchViewState()
}