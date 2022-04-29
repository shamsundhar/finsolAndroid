package com.finsol.tech.presentation.watchlist

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.databinding.FragmentWatchlistBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.prelogin.LoginMarketViewState
import com.finsol.tech.presentation.prelogin.LoginViewModel
import com.finsol.tech.util.AppConstants.KEY_PREF_NAME
import com.finsol.tech.util.AppConstants.KEY_PREF_USER_ID
import com.finsol.tech.util.PreferenceHelper
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class WatchListFragment: BaseFragment(){
    private lateinit var watchListViewModel: WatchListViewModel
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding:FragmentWatchlistBinding
    private lateinit var adapter:Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }
    private fun initObservers() {
        watchListViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true

        watchListViewModel = ViewModelProvider(requireActivity()).get(WatchListViewModel::class.java)

        watchListViewModel.requestAllContractsDetails(preferenceHelper.getString(context, KEY_PREF_USER_ID, ""))

        // Setting ViewPager for each Tabs
        setupViewPager(binding.viewpager)
        // Set Tabs inside Toolbar
        binding.resultTabs.setupWithViewPager(binding.viewpager)
        binding.subTitle.text = preferenceHelper.getString(context, KEY_PREF_NAME, "")

        return binding.root
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(childFragmentManager)
        adapter.addFragment(ChildWatchListFragment1(), "WatchList-1")
        adapter.addFragment(ChildWatchListFragment2(), "WatchList-2")
        adapter.addFragment(ChildWatchListFragment3(), "WatchList-3")
        viewPager.adapter = adapter
    }

    internal class Adapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    private fun processResponse(state: WatchListViewState) {
        when(state){
            is WatchListViewState.AllContractsSuccessResponse -> handleAllContractsSuccessResponse(state.allContractsResponse)
            is WatchListViewState.IsLoading -> handleLoading(state.isLoading)
            }
    }
    private fun handleAllContractsSuccessResponse(allContractsResponse: GetAllContractsResponse) {
        (requireActivity().application as FinsolApplication).setAllContracts(allContractsResponse)
        (adapter.getItem(0) as ChildWatchListFragment1).updateWatchListData(allContractsResponse.watchlist1)
    }
    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

}