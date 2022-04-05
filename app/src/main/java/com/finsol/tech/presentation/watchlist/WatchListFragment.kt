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
import com.google.android.material.tabs.TabLayout


class WatchListFragment: Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_watchlist, container, false)
        // Setting ViewPager for each Tabs
        // Setting ViewPager for each Tabs
        val viewPager = view.findViewById<View>(R.id.viewpager) as ViewPager
        setupViewPager(viewPager)
        // Set Tabs inside Toolbar
        // Set Tabs inside Toolbar
        val tabs = view.findViewById<View>(R.id.result_tabs) as TabLayout
        tabs.setupWithViewPager(viewPager)


        return view
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