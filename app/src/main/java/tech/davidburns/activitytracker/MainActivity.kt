package tech.davidburns.activitytracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

const val ACTIVITY_ID = "ACTIVITY_ID"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        User.mainActivity = this
    }

    override fun onResume() {
        super.onResume()
        if(intent != null) {
            if ((intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0)
                intent.extras?.getInt(ACTIVITY_ID)?.let { activity ->
                    User.intentActivity = activity
                }
        }
    }

    override fun onStop() {
        TimerManager.unbindServices()
        super.onStop()
    }

    override fun onRestart() {
        TimerManager.rebindServices()
        super.onRestart()
    }
}
