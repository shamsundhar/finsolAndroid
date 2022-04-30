//package com.finsol.tech.data.repository
//
//import android.util.Log
//import com.finsol.tech.data.model.LoginResponse
//import com.finsol.tech.data.model.Market
//import com.finsol.tech.data.model.ProfileResponse
//import com.finsol.tech.data.model.ResponseWrapper
//import com.finsol.tech.data.remote.LoginDataSource
//import com.finsol.tech.data.remote.MarketDataSource
//import com.finsol.tech.data.remote.ProfileDataSource
//import com.finsol.tech.domain.LoginResponseDataRepository
//import com.finsol.tech.domain.MarketDataRepository
//import com.finsol.tech.domain.ProfileResponseDataRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import javax.inject.Inject
//import javax.inject.Singleton
//
//
//
//@Singleton
//class ProfileRepositoryImp @Inject constructor(private val remoteDataSource: ProfileDataSource): ProfileResponseDataRepository {
//
//    override suspend fun getProfileData(userID : String): Flow<ResponseWrapper<ProfileResponse>> {
//        return flow {
//            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
//            emit(remoteDataSource.getProfileData(userID))
//        }
//    }
//
//
//    companion object{
//        private const val TAG = "ProfileRepositoryImp"
//    }
//
//}