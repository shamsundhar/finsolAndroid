//package com.finsol.tech.data.repository
//
//import android.util.Log
//import com.finsol.tech.data.model.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import javax.inject.Inject
//import javax.inject.Singleton
//
//
//
//@Singleton
//class AllContractsRepositoryImp @Inject constructor(private val remoteDataSource: AllContractsDataSource): AllContractsResponseDataRepository {
//
//    override suspend fun getAllContractsData(userID : String): Flow<ResponseWrapper<GetAllContractsResponse>> {
//        return flow {
//            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
//            emit(remoteDataSource.getAllContractsData(userID))
//        }
//    }
//
//
//    companion object{
//        private const val TAG = "AllContractsRepositoryImp"
//    }
//
//}