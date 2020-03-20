package tech.davidburns.activitytracker.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import tech.davidburns.activitytracker.R

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
                LoginState.LOGGED_IN, LoginState.DENIED_DATABASE -> TODO() // Need to implement starting screen
            }
            findNavController().navigate(action)
        }, DELAY)
    }

    // Check if authenticated with Google
    // Otherwise, check if they have denied access to the use of a Google
    private fun loginState(): LoginState {
        if (auth.currentUser != null) {
            return LoginState.LOGGED_IN
        }
        val sharedPref =
            activity?.getPreferences(Context.MODE_PRIVATE) ?: return LoginState.NEW_USER
        return LoginState.valueOf(
            sharedPref.getString(
                getString(R.string.login_key),
                null
            ) ?: LoginState.NEW_USER.name
        )
    }
}

enum class LoginState {
    LOGGED_IN, DENIED_DATABASE, NEW_USER
}
