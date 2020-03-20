package tech.davidburns.activitytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).
            setGraph(R.navigation.nav_graph)

    }
}
