package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_card.activity_title
import kotlinx.android.synthetic.main.activity_detail.*
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.SliderAdapter
import tech.davidburns.activitytracker.User

class ActivityController : Fragment() {
    private lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = User.currentActivity
        return inflater.inflate(R.layout.activity_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity_title.text = activity.name

        slide_view_pager.adapter = SliderAdapter(requireActivity(), activity)
    }
}