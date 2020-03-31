package tech.davidburns.activitytracker.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import tech.davidburns.activitytracker.R
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.enums.LoginState
import tech.davidburns.activitytracker.util.Authentication
import tech.davidburns.activitytracker.util.FirestoreDatabase
import tech.davidburns.activitytracker.util.NativeDatabase

const val DELAY = 3000L

class SplashScreen : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_screen, container, false)
    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        transitionScreenWithDelay()
        super.onStart()
    }

    private fun transitionScreenWithDelay() {
        Handler().postDelayed({
            val action = when (loginState()) {
                LoginState.NEW_USER -> SplashScreenDirections.actionSplashScreenToLoginScreen()
                LoginState.LOGGED_IN -> {
                    User.database = FirestoreDatabase(auth.currentUser!!, requireActivity().applicationContext)
                    SplashScreenDirections.actionSplashScreenToActivityViewController()
                }
                LoginState.DENIED_DATABASE -> {
                    User.database = NativeDatabase(requireActivity().applicationContext)
                    SplashScreenDirections.actionSplashScreenToActivityViewController()
                }
            }
            findNavController().navigate(action)
        }, DELAY)
    }

    // Check if authenticated with Google
    // Otherwise, check if they have denied access to the use of an external database
    private fun loginState(): LoginState {
        val user = auth.currentUser
        return if (user != null) {
            User.authenticate = user
            LoginState.LOGGED_IN
        } else {
            when (Authentication.isDatabaseEnabled(activity)) {
                true -> LoginState.NEW_USER
                false -> LoginState.DENIED_DATABASE
            }
        }
    }
}
