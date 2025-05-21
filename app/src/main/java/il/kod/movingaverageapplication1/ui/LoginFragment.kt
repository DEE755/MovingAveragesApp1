package il.kod.movingaverageapplication1.ui

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
import il.kod.movingaverageapplication1.data.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.data.UserProfile
import il.kod.movingaverageapplication1.databinding.LoginFragmentBinding
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import kotlin.collections.get

import il.kod.movingaverageapplication1.data.models.UserProfileTransitFromGson

@AndroidEntryPoint
class LoginFragment : Fragment() {

    var _binding: LoginFragmentBinding? = null
    val binding get() = _binding!!

    private val CSDviewModel: CustomServerDatabaseViewModel by activityViewModels()


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

            binding.loginButton.setOnClickListener {

                Log.d("LoginFragment", "Login button clicked")
                val username = binding.nameInput.text.toString()
                val password = binding.passwordInput.text.toString()
                Log.d("LoginFragment", "Username_entered: $username, Password_entered: $password")

                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        "Username and password cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("LoginFragment", "Username and password cannot be empty")
                } else {
                    CSDviewModel.updateCredentialsfromServer(username, password)
                    //Log.d("LoginFragment", "values: ${CSDviewModel.client_username} ${CSDviewModel.client_password}")
                }




                CSDviewModel.credentials.observe(viewLifecycleOwner) {
                    Log.d("LoginFragment", "observe called")
                    when (it.status) {
                        is Loading -> {
                            binding.progressBar.isVisible = true
                            binding.loadingText.isVisible = true
                            Log.d("LoginFragment", "Loading state")
                        }

                        is Success -> {

                            //verify if the data wrapper is not null
                            it.status.data?.let { gsonWrappedObject ->//list of one UserProfileTransitFromGson
                                //verify if the object in the data is not null (list of only one UserProfile) and extract the final USERPROFILE object to the viewmodel
                                gsonWrappedObject[0].let {fetchedUserProfile ->
                                    CSDviewModel.loggedUser = fetchedUserProfile
                                }
                            }

                            Toast.makeText(
                                requireContext(),
                                getString(R.string.welcome_message, CSDviewModel.loggedUser.username),
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_navigation_graph_to_selectedStocks2)
                            Log.d("LoginFragment", "Success state")
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


            //val response: Response<UserProfileTransitFromGson> =

            //TODO()

            binding.signUpText.setOnClickListener {

                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }




    }
}
