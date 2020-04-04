package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.enums.LoginState
import tech.davidburns.activitytracker.util.Authentication
import tech.davidburns.activitytracker.util.FirestoreDatabase
import tech.davidburns.activitytracker.util.NativeDatabase
import java.time.Instant

const val DELAY = 3000L

class SplashScreen : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_screen, container, false)
    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        transitionScreenWithDelay()
        super.onStart()
    }

    private fun transitionScreenWithDelay() = runBlocking {
        launch {
            User.applicationContext = requireActivity().applicationContext
            val before = Instant.now().epochSecond

            //VERY IMPORTANT!!!
            //Sets the database to be proper database in addition to creating directions
            val action = when (loginState()) {
                LoginState.NEW_USER -> SplashScreenDirections.actionSplashScreenToLoginScreen()
                LoginState.LOGGED_IN -> {
                    User.database = FirestoreDatabase(user!!)
                    SplashScreenDirections.actionSplashScreenToActivityViewController()
                }
                LoginState.DENIED_DATABASE -> {
                    User.database = NativeDatabase()
                    SplashScreenDirections.actionSplashScreenToActivityViewController()
                }
            }
            val totalDelay = DELAY - (Instant.now().epochSecond - before)
            delay(if (totalDelay > 0) totalDelay else 0)
            findNavController().navigate(action)
        }
    }

    // Check if authenticated with Google
    // Otherwise, check if they have denied access to the use of an external database
    private fun loginState(): LoginState {
        return if (user != null) {
            LoginState.LOGGED_IN
        } else {
            when (Authentication.isDatabaseEnabled(activity)) {
                true -> LoginState.NEW_USER
                false -> LoginState.DENIED_DATABASE
            }
        }
    }
}
