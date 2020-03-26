package tech.davidburns.activitytracker.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.session_log_time.*
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.R

class SessionLog(val activity: Activity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.session_log_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enter_start_time.setOnClickListener {
            val timePickerDialog: TimePickerDialog = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hour: Int, minute: Int ->
                    var amPm: String
                    var amPmHour = hour
                    if (hour >= 12) {
                        amPm = "PM"
                        amPmHour -= 12
                    } else {
                        amPm = "AM"
                    }
                    enter_start_time.text = String.format("%02d:%02d", amPmHour, minute) + amPm;
                }, 0,0, false);

            timePickerDialog.show()
        }

        enter_end_time.setOnClickListener {
            val timePickerDialog: TimePickerDialog = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hour: Int, minute: Int ->
                    var amPm: String
                    var amPmHour = hour
                    if (hour >= 12) {
                        amPm = "PM"
                        amPmHour -= 12
                    } else {
                        amPm = "AM"
                    }
                    enter_end_time.text = String.format("%02d:%02d", amPmHour, minute) + amPm;
                }, 0,0, false);

            timePickerDialog.show()
        }
    }
}