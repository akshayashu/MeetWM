package com.akshay.meetwm.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akshay.meetwm.ui.chatFragment.ChatFragment
import com.akshay.meetwm.ui.historyFragment.HistoryFragment
import com.akshay.meetwm.ui.statusFragment.StatusFragment

class PageAdapter(fm : FragmentManager, lifeCycle : Lifecycle) : FragmentStateAdapter(fm, lifeCycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when(position){
            0 -> ChatFragment()
            1 -> StatusFragment()
            2 -> HistoryFragment()
            else -> Fragment()
        }
    }
}