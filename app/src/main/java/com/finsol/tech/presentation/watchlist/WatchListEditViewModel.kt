package com.finsol.tech.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.contracts.AddToWatchListData
import com.finsol.tech.domain.contracts.GetAllContractsData
import com.finsol.tech.domain.contracts.RemoveFromWatchListData
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
class WatchListEditViewModel @Inject constructor(
    private val removeFromWatchListData: RemoveFromWatchListData
    ) : ViewModel() {


    private val _state = MutableStateFlow<WatchListEditViewState>(WatchListEditViewState.Init)
    val mState: StateFlow<WatchListEditViewState> get() = _state

    fun removeFromWatchList(userID: String, watchListNumber: String, securityID:String) {
        viewModelScope.launch {
            removeFromWatchListData.execute(userID, watchListNumber, securityID).onStart {
                _state.value = WatchListEditViewState.IsLoading(true)
            }.catch {
                _state.value = WatchListEditViewState.IsLoading(false)
                _state.value = WatchListEditViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = WatchListEditViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        WatchListEditViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = WatchListEditViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = WatchListEditViewState.RemoveFromWatchListSuccessResponse(it.value)
                    }
                }
            }
        }
    }
    fun resetStateToDefault() {
        _state.value = WatchListEditViewState.Init
    }

}

sealed class WatchListEditViewState {
    object Init : WatchListEditViewState()
    data class IsLoading(val isLoading: Boolean) : WatchListEditViewState()
    data class ShowToast(val message: String) : WatchListEditViewState()
    data class RemoveFromWatchListSuccessResponse(val genericMessageResponse: GenericMessageResponse) :
        WatchListEditViewState()
    data class ErrorResponse(val message: String) : WatchListEditViewState()
}