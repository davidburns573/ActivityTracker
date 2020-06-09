package tech.davidburns.activitytracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val timerListeners = mutableListOf<Timer>()
    fun startTimer(timer: Timer): Intent {
        val serviceIntent = Intent(this, TimerService::class.java)
        startService(serviceIntent)
        // Bind to LocalService
        Intent(this, TimerService::class.java).also { bindingIntent ->
            bindService(bindingIntent, timer.connection, Context.BIND_AUTO_CREATE)
        }
        timerListeners.add(timer)
        GlobalTimer.subscribe(timer)
        return serviceIntent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        User.mainActivity = this
    }

    override fun onStop() {
        timerListeners.forEach {
            unbindService(it.connection)
            it.bound = false
        }
        super.onStop()
    }

    override fun onRestart() {
        timerListeners.forEach {
            Intent(this, TimerService::class.java).also { intent ->
                bindService(intent, it.connection, Context.BIND_AUTO_CREATE)
            }
            it.bound = true
        }
        super.onRestart()
    }
}
