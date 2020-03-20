package tech.davidburns.activitytracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.enter_name_popup.*

class ActivityViewController : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddActivity.setOnClickListener {
            val enterName = PopupWindow(enterNamePopup)
            enterName.showAsDropDown(it)
        }
    }
}