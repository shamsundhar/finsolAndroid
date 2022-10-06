package com.finsol.tech

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.finsol.tech.db.AppDatabase
import com.finsol.tech.domain.marketdata.SessionValidateResponse
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.rabbitmq.MySingletonViewModel.setUserLogout
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var navController : NavController
    private lateinit var appDatabase : AppDatabase


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(this)
        setContentView(R.layout.activity_main)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()

        mySingletonViewModel  = MySingletonViewModel.getMyViewModel(this)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        RabbitMQ.setMySingletonViewModel(mySingletonViewModel)
        listenForBadgeUpdates()
        navController =
            Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_main_bottom_navigation_view)
        setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.watchListSymbolDetailsFragment -> hideBottomNav(bottomNavigationView)
                R.id.watchListSearchFragment -> hideBottomNav(bottomNavigationView)
                R.id.buySellFragment -> hideBottomNav(bottomNavigationView)
                R.id.splashScreenFragment -> hideBottomNav(bottomNavigationView)
                R.id.orderHistoryDetailsFragment -> hideBottomNav(bottomNavigationView)
                R.id.orderPendingDetailsFragment -> hideBottomNav(bottomNavigationView)
                R.id.loginFragment -> hideBottomNav(bottomNavigationView)
                R.id.forgotPasswordFragment -> hideBottomNav(bottomNavigationView)
                R.id.registerFragment -> hideBottomNav(bottomNavigationView)
                R.id.watchListEditFragment -> hideBottomNav(bottomNavigationView)
                R.id.portfolioDetailsFragment -> hideBottomNav(bottomNavigationView)
                R.id.notificationsFragment -> hideBottomNav(bottomNavigationView)
                else -> showBottomNav(bottomNavigationView)
            }
        }

        initObservers()
//        Handler(Looper.myLooper()!!).postDelayed({
//            val userID = preferenceHelper.getString(this, AppConstants.KEY_PREF_USER_ID, "")
//            if(!userID.equals("")){
//                RabbitMQ.unregisterAll()
//                mainActivityViewModel.doLogout(userID)
//            }
//
//        },5000)
    }



    private fun initObservers() {

        mySingletonViewModel.getUserLogout().observe(this){
            if(it){
                processLogout()
            }
        }

        mainActivityViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(it: MainActivityViewState) {
        when(it){
            is MainActivityViewState.sessionValidationResponse -> processSessionResponse(it.sessionValidateResponse)
            is MainActivityViewState.LogoutSuccessResponse -> processLogout()
        }
    }

    private fun processLogout() {
        setUserLogout(false)
        getValuesBeforeClearingPreference()
        GlobalScope.launch {
            appDatabase.notificationDao().deleteAll()
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                R.id.nav_graph,
                false
            ).build()
        navController.navigate(R.id.loginFragment,null,navOptions)

        Utilities.showDialogWithOneButton(
            this,
            getString(R.string.logout_session_expiry),
            null
        )
    }



    private fun processSessionResponse(sessionValidateResponse: SessionValidateResponse) {
        if(sessionValidateResponse.message.equals("false",true)){
            RabbitMQ.unregisterAll()
            mainActivityViewModel.doLogout(preferenceHelper.getString(this, AppConstants.KEY_PREF_USER_ID, ""))
        }
    }

    private fun getValuesBeforeClearingPreference() {
        val rememberIPAddress = preferenceHelper.getBoolean(this,
            AppConstants.KEY_PREF_IP_ADDRESS, false)
        val rememberUserName = preferenceHelper.getBoolean(this,
            AppConstants.KEY_PREF_USERNAME_REMEMBER, false)
        val ipAddress = preferenceHelper.getString(this,
            AppConstants.KEY_PREF_IP_ADDRESS_VALUE, "")
        val username = preferenceHelper.getString(this, AppConstants.KEY_PREF_USERNAME_VALUE, "")
        val nightMode:Boolean = preferenceHelper.getBoolean(this,
            AppConstants.KEY_PREF_DARK_MODE, false)
        preferenceHelper.clear(this)
        preferenceHelper.setBoolean(this, AppConstants.KEY_PREF_DARK_MODE, nightMode)
        if(rememberIPAddress){
            preferenceHelper.setBoolean(this, AppConstants.KEY_PREF_IP_ADDRESS, true)
            preferenceHelper.setString(this, AppConstants.KEY_PREF_IP_ADDRESS_VALUE, ipAddress)
        }
        if(rememberUserName){
            preferenceHelper.setBoolean(this, AppConstants.KEY_PREF_USERNAME_REMEMBER, true)
            preferenceHelper.setString(this, AppConstants.KEY_PREF_USERNAME_VALUE, username)
        }
    }

    fun hideBottomNav(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.visibility = View.GONE
    }
    fun showBottomNav(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
//        val userID = preferenceHelper.getString(this, AppConstants.KEY_PREF_USER_ID, "")
//        if(!userID.equals("",true)){
//            RabbitMQ.subscribeForUserUpdates(userID)
//        }
        val userID = preferenceHelper.getString(this, AppConstants.KEY_PREF_USER_ID, "")
        if(!userID.equals("")){
            RabbitMQ.subscribeForUserUpdates(userID)
            mainActivityViewModel.validateSession(userID)
        }
        val allContractsResponse =  (application as FinsolApplication).getAllContracts()
        allContractsResponse?.let {
            it.watchlist1.forEach { contract ->
                lifecycleScope.launch(Dispatchers.IO) {
                    RabbitMQ.subscribeForMarketData(contract.securityID.toString())
                }
            }
            it.watchlist2.forEach { contract ->
                lifecycleScope.launch(Dispatchers.IO) {
                    RabbitMQ.subscribeForMarketData(contract.securityID.toString())
                }
            }
            it.watchlist3.forEach { contract ->
                lifecycleScope.launch(Dispatchers.IO) {
                    RabbitMQ.subscribeForMarketData(contract.securityID.toString())
                }
            }
        }
    }

    private fun listenForBadgeUpdates(){
        val userID = preferenceHelper.getString(this, AppConstants.KEY_PREF_USER_ID, "")
        if(!userID.equals("")){
            RabbitMQ.subscribeForUserUpdates(userID)
        }
        mySingletonViewModel.getNotificationTracker().observe(this){
            if(it.isEmpty()){
                bottomNavigationView.removeBadge(R.id.ordersFragment)
            }else{
                bottomNavigationView.getOrCreateBadge(R.id.ordersFragment).clearNumber()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        RabbitMQ.unregisterAll()
    }
}