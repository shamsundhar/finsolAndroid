package com.finsol.tech

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.PreferenceHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()

        val mySingletonViewModel  = MySingletonViewModel.getMyViewModel(this)
        RabbitMQ.setMySingletonViewModel(mySingletonViewModel)

        val navController: NavController =
            Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        val bottomNavigationView =
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
    }

    override fun onPause() {
        super.onPause()
        RabbitMQ.unregisterAll()
    }
}