package tech.davidburns.activitytracker

import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import tech.davidburns.activitytracker.fragments.SessionLength
import tech.davidburns.activitytracker.fragments.SessionLog

class SliderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SessionLength()
        } else {
            SessionLog()
        }
    }

}