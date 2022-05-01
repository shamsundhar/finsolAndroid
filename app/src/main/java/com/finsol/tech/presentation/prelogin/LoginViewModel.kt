package com.finsol.tech.presentation.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.PendingOrderResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.contracts.GetAllContractsData
import com.finsol.tech.domain.marketdata.GetLoginData
import com.finsol.tech.domain.marketdata.GetMarketData
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.PortfolioResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.domain.portfolio.GetPortfolioData
import com.finsol.tech.domain.orders.GetPendingOrdersData
import com.finsol.tech.domain.profile.GetProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(private val getLoginData: GetLoginData,
                                         private val getProfileData:GetProfileData,
                                         private val getPortfolioData:GetPortfolioData,

                                         private val getContractsData: GetAllContractsData,
                                         private val getPendingOrdersData: GetPendingOrdersData
                                         ) : ViewModel() {


    private val _state = MutableStateFlow<LoginMarketViewState>(LoginMarketViewState.Init)
    val mState: StateFlow<LoginMarketViewState> get() = _state

    init {
//        fetchMarketData()
        requestPortfolioDetails(7)
    }

    fun requestLogin(userID: String, password: String) {
        viewModelScope.launch {
            getLoginData.execute(userID,password).onStart {
                _state.value = LoginMarketViewState.IsLoading(true)
            }.catch {
                _state.value = LoginMarketViewState.IsLoading(false)
                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = LoginMarketViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        LoginMarketViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = LoginMarketViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = LoginMarketViewState.SuccessResponse(it.value)
                    }
                }
            }
        }
    }

    fun requestUserProfileDetails(userID: String) {
        viewModelScope.launch {
            getProfileData.execute(userID).onStart {
                _state.value = LoginMarketViewState.IsLoading(true)
            }.catch {
                _state.value = LoginMarketViewState.IsLoading(false)
                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = LoginMarketViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        LoginMarketViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = LoginMarketViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = LoginMarketViewState.ProfileSuccessResponse(it.value)
                    }
                }
            }
        }
    }

    fun requestPendingOrdersDetails(userID: String) {
        viewModelScope.launch {
            getPendingOrdersData.execute(userID).onStart {
                _state.value = LoginMarketViewState.IsLoading(true)
            }.catch {
                _state.value = LoginMarketViewState.IsLoading(false)
                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = LoginMarketViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        LoginMarketViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = LoginMarketViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = LoginMarketViewState.AllPendingOrdersResponse(it.value)
                    }
                }
            }
        }
    }

    fun requestAllContractsDetails(userID: String) {
        viewModelScope.launch {
            getContractsData.execute(userID).onStart {
                _state.value = LoginMarketViewState.IsLoading(true)
            }.catch {
                _state.value = LoginMarketViewState.IsLoading(false)
                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = LoginMarketViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        LoginMarketViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = LoginMarketViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = LoginMarketViewState.AllContractsResponse(it.value)
                    }
                }
            }
        }
    }


    fun requestPortfolioDetails(userID: Int) {
        viewModelScope.launch {
            getPortfolioData.execute(userID).onStart {
                _state.value = LoginMarketViewState.IsLoading(true)
            }.catch {
                _state.value = LoginMarketViewState.IsLoading(false)
                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
            }.collect {
                _state.value = LoginMarketViewState.IsLoading(false)
                when (it) {
                    is ResponseWrapper.NetworkError -> _state.value =
                        LoginMarketViewState.ShowToast("Please check your network Conection!")
                    is ResponseWrapper.GenericError -> {
                        it.error?.let { msg ->
                            _state.value = LoginMarketViewState.ShowToast(
                                msg
                            )
                        }
                    }
                    is ResponseWrapper.Success -> {
                        _state.value = LoginMarketViewState.PortfolioSuccessResponse(it.value)
                    }
                }
            }
        }
    }

//    private fun fetchMarketData() {
//        viewModelScope.launch {
//            getMarketData.execute().onStart {
//                _state.value = LoginMarketViewState.IsLoading(true)
//            }.catch {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
//            }.collect {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                when(it){
//                    is ResponseWrapper.NetworkError -> _state.value =
//                        LoginMarketViewState.ShowToast("Please check your network Conection!")
//                    is ResponseWrapper.GenericError -> {
//                        it.error?.let { msg ->
//                            _state.value = LoginMarketViewState.ShowToast(
//                                msg
//                            )
//                        }
//                    }
//                    is ResponseWrapper.Success -> {
//
//                    }
//                }
//            }
//        }
//
//    }
}

sealed class LoginMarketViewState {
    object Init : LoginMarketViewState()
    data class IsLoading(val isLoading: Boolean) : LoginMarketViewState()
    data class ShowToast(val message: String) : LoginMarketViewState()
    data class SuccessResponse(val loginResponseDomainModel: LoginResponseDomainModel) :
        LoginMarketViewState()
    data class ProfileSuccessResponse(val profileResponseDomainModel: ProfileResponseDomainModel) :
        LoginMarketViewState()
    data class PortfolioSuccessResponse(val portfolioResponseDomainModel: PortfolioResponseDomainModel) :
        LoginMarketViewState()
    data class AllContractsResponse(val allContractsResponse: GetAllContractsResponse) :
        LoginMarketViewState()
    data class AllPendingOrdersResponse(val pendingOrdersResponse: PendingOrderResponse) :
        LoginMarketViewState()

    data class ErrorResponse(val message: String) : LoginMarketViewState()
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