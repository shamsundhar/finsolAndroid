package com.finsol.tech.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.contracts.GetAllContractsData
import com.finsol.tech.domain.marketdata.GetLoginData
import com.finsol.tech.domain.marketdata.GetMarketData
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.domain.profile.GetProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WatchListViewModel @Inject constructor(private val getContractsData: GetAllContractsData
                                         ) : ViewModel() {


    private val _state = MutableStateFlow<WatchListViewState>(WatchListViewState.Init)
    val mState: StateFlow<WatchListViewState> get() = _state


    fun requestAllContractsDetails(userID: String) {
        viewModelScope.launch {
            getContractsData.execute(userID).onStart {
                _state.value = WatchListViewState.IsLoading(true)
            }.catch {
                _state.value = WatchListViewState.IsLoading(false)
                _state.value = WatchListViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = WatchListViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        WatchListViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = WatchListViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = WatchListViewState.AllContractsSuccessResponse(it.value)
                    }
                }
            }
        }
    }

}

sealed class WatchListViewState {
    object Init : WatchListViewState()
    data class IsLoading(val isLoading: Boolean) : WatchListViewState()
    data class ShowToast(val message: String) : WatchListViewState()
    data class AllContractsSuccessResponse(val allContractsResponse: GetAllContractsResponse) :
        WatchListViewState()
    data class ErrorResponse(val message: String) : WatchListViewState()
}
//sealed class ProfileDataViewState {
//    object Init : ProfileDataViewState()
//    data class IsLoading(val isLoading: Boolean) : ProfileDataViewState()
//    data class ShowToast(val message: String) : ProfileDataViewState()
//    data class SuccessResponse(val loginResponseDomainModel: ProfileResponseDomainModel) :
//        ProfileDataViewState()
//
//    data class ErrorResponse(val message: String) : ProfileDataViewState()
//}