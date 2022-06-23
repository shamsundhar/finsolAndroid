package com.finsol.tech.presentation.prelogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsol.tech.TripleDESEncryption
import com.finsol.tech.data.model.*
import com.finsol.tech.domain.marketdata.GetExchangeEnumData
import com.finsol.tech.domain.marketdata.GetExchangeOptionsData
import com.finsol.tech.domain.marketdata.GetLoginData
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.domain.portfolio.GetPortfolioData
import com.finsol.tech.domain.profile.GetProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getLoginData: GetLoginData,
    private val getProfileData: GetProfileData,
    private val getExchangeEnumData: GetExchangeEnumData,
    private val getExchangeOptionsData: GetExchangeOptionsData,
    private val getPortfolioData: GetPortfolioData
) : ViewModel() {


    private val _state = MutableStateFlow<LoginMarketViewState>(LoginMarketViewState.Init)
    val mState: StateFlow<LoginMarketViewState> get() = _state

    init {
        requestPortfolio("1120")
    }

    fun requestLogin(userID: String, password: String) {
        viewModelScope.launch {
            val tripleDESEncryption = TripleDESEncryption()
            val encryptedPassword = tripleDESEncryption.encrypt(password)
            getLoginData.execute(userID, encryptedPassword).onStart {
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


    fun requestPortfolio(userID: String) {
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



//    fun requestPendingOrdersDetails(userID: String) {
//        viewModelScope.launch {
//            getPendingOrdersData.execute(userID).onStart {
//                _state.value = LoginMarketViewState.IsLoading(true)
//            }.catch {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
//            }.collect {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                when (it) {
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
//                        _state.value = LoginMarketViewState.AllPendingOrdersResponse(it.value)
//                    }
//                }
//            }
//        }
//    }

//    fun requestOrderHistoryDetails(userID: String) {
//        viewModelScope.launch {
//            getOrderHistoryData.execute(userID).onStart {
//                _state.value = LoginMarketViewState.IsLoading(true)
//            }.catch {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
//            }.collect {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                when (it) {
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
//                        _state.value = LoginMarketViewState.AllOrderHistoryResponse(it.value)
//                    }
//                }
//            }
//        }
//    }

    //    fun requestAllContractsDetails(userID: String) {
//        viewModelScope.launch {
//            getContractsData.execute(userID).onStart {
//                _state.value = LoginMarketViewState.IsLoading(true)
//            }.catch {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                _state.value = LoginMarketViewState.ErrorResponse("UnknownError")
//            }.collect {
//                _state.value = LoginMarketViewState.IsLoading(false)
//                when (it) {
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
//                        _state.value = LoginMarketViewState.AllContractsResponse(it.value)
//                    }
//                }
//            }
//        }
//    }
    fun resetStateToDefault() {
        _state.value = LoginMarketViewState.Init
    }
}

sealed class LoginMarketViewState {
    object Init : LoginMarketViewState()
    data class IsLoading(val isLoading: Boolean) : LoginMarketViewState()
    data class ShowToast(val message: String) : LoginMarketViewState()
    data class SuccessResponse(val loginResponseDomainModel: LoginResponseDomainModel) :
        LoginMarketViewState()

    data class ProfileSuccessResponse(val profileResponseDomainModel: ProfileResponseDomainModel) :
        LoginMarketViewState()

    data class AllContractsResponse(val allContractsResponse: GetAllContractsResponse) :
        LoginMarketViewState()

    data class AllPendingOrdersResponse(val pendingOrdersResponse: Array<PendingOrderModel>) :
        LoginMarketViewState()

    data class AllOrderHistoryResponse(val orderHistoryResponse: Array<OrderHistoryModel>) :
        LoginMarketViewState()

    data class PortfolioSuccessResponse(val portfolioResponse: PortfolioResponse) :
        LoginMarketViewState()

    data class ErrorResponse(val message: String) : LoginMarketViewState()
}