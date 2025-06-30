package il.kod.movingaverageapplication1.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.databinding.LoginFragmentBinding
import il.kod.movingaverageapplication1.utils.AutoClearedValue
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.decodeJWT
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding by AutoClearedValue<LoginFragmentBinding>(this)
    val binding get() = _binding

    private val CSDviewModel: CustomServerDatabaseViewModel by activityViewModels()

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val accessToken = CSDviewModel.getAccessTokenFromPreferences()
        Log.d("LoginFragment", "Access token: $accessToken")
        if (!accessToken.isNullOrEmpty())
        {
            val decodedToken=decodeJWT(accessToken)
            val expirationDate=decodedToken.getLong("exp")
            val currentTime=System.currentTimeMillis() / 1000

            Log.d("LoginFragment", "decodedToken: ${decodedToken},Current time: $currentTime, Expiration date: $expirationDate")


            if (currentTime < expirationDate)
            {
                Log.d("LoginFragment", "Valid token")
                findNavController().navigate(R.id.action_login_fragment_to_followedStocks)}
        }

            binding.loginButton.setOnClickListener {


                val username = binding.nameInput.text.toString()
                val password = binding.passwordInput.text.toString()


                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.username_password_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("LoginFragment", "Username and password cannot be empty")
                    return@setOnClickListener //recursion to retry with correct values
                }
                    CSDviewModel.updateCredentialsFromServer(username, password)





                CSDviewModel.tokensResponse.observe(viewLifecycleOwner) {
                    Log.d("LoginFragment", "observe called")
                    when (it.status) {
                        is Loading -> {
                            binding.progressBar.isVisible = true
                            binding.loadingText.isVisible = true
                            Log.d("LoginFragment", "Loading state")
                        }

                        is Success -> {

                            it.status.data?.let { data ->
                                Log.d("LoginFragment", "Data received: $data")
                                sessionManager.setUsername(data.username ?: getString(R.string.user_default))
                                sessionManager.setClientId(data.userId ?: -1)
                                sessionManager.saveTokens(
                                    data.accessToken ?: "",
                                    data.refreshToken ?: "",
                                    sessionManager.getUsername(),
                                    sessionManager.getClientId()
                                )




                            Toast.makeText(
                                requireContext(),
                                getString(R.string.welcome_message, data.username),
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_login_fragment_to_followedStocks)
                            Log.d("LoginFragment", "Success state")
                            } ?: Log.d("LoginFragment", "Data is null")
                        }

                        is Error -> {
                            Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT)
                                .show()
                            binding.progressBar.isVisible = false
                            binding.loadingText.isVisible = false
                            Log.d("LoginFragment", "Error state")
                        }
                    }
                }
            }



            binding.signUpText.setOnClickListener {

                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }




    }
}
