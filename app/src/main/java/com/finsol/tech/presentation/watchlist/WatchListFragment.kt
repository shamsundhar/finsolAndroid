package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentWatchlistBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.util.AppConstants.KEY_PREF_NAME
import com.finsol.tech.util.PreferenceHelper
import com.google.android.material.tabs.TabLayout


class WatchListFragment: BaseFragment(){
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var binding:FragmentWatchlistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        // Setting ViewPager for each Tabs
        setupViewPager(binding.viewpager)
        // Set Tabs inside Toolbar
        binding.resultTabs.setupWithViewPager(binding.viewpager)
        binding.subTitle.text = preferenceHelper.getString(context, KEY_PREF_NAME, "")

        return binding.root
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(childFragmentManager)
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

}