package com.finsol.tech

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()

        mySingletonViewModel  = MySingletonViewModel.getMyViewModel(this)
        RabbitMQ.setMySingletonViewModel(mySingletonViewModel)
        listenForBadgeUpdates()
        val navController: NavController =
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
                else -> showBottomNav(bottomNavigationView)
            }
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