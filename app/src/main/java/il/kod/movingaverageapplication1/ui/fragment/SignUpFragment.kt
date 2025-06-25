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
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.databinding.SignupFragmentBinding
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import javax.inject.Inject
import kotlin.getValue

class SignUpFragment : Fragment()
{

    var _binding: SignupFragmentBinding? = null
    val binding get() = _binding!!

    private val CSDviewModel: CustomServerDatabaseViewModel by activityViewModels()

    @Inject lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding.signUpButton.setOnClickListener {

                val username = binding.nameInput.text.toString()
                val password = binding.passwordInput.text.toString()
                val passwordConfirmation = binding.passwordConfirmationInput.text.toString()

                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(requireContext(), "Username and password cannot be empty", Toast.LENGTH_LONG).show()
                    return@setOnClickListener //recursion to retry with correct values
                }
                else if (password != passwordConfirmation) {
                    Toast.makeText(requireContext(), "Confirmation and password are not identical", Toast.LENGTH_LONG).show()
                    return@setOnClickListener //recursion to retry with correct values
                }

                //input is valid, sign up
                    CSDviewModel.signUpNewUserToServer(username, password)
                    //Log.d("LoginFragment", "values: ${CSDviewModel.client_username} ${CSDviewModel.client_password}")





                CSDviewModel.signupResult.observe(viewLifecycleOwner) {
                    Log.d("LoginFragment", "observe called")
                    when(it.status){
                        is Loading->{binding.progressBar.isVisible=true
                            binding.loadingText.isVisible=true
                            Log.d("LoginFragment", "Loading state")}

                        is Success->{
                            Toast.makeText(requireContext(),it.status.data?.username?:"unknown user", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_signUpFragment_to_followedStocks)

                            it.status.data?.let { data ->
                                Log.d("SignupFragment", "Data received: $data")
                                sessionManager.setUsername(data.username ?: "user")
                                sessionManager.saveTokens(
                                    data.accessToken ?: "",
                                    data.refreshToken ?: "",
                                    data.username ?: "",
                                    data.userId ?: 0
                                )

                                }


                                binding.progressBar.isVisible=false
                                binding.loadingText.isVisible=false
                            }


                        is Error -> {Toast.makeText(requireContext(), "ERROR, TRY AGAIN", Toast.LENGTH_SHORT).show()
                            binding.progressBar.isVisible=false
                            binding.loadingText.isVisible=false
                            Log.d("LoginFragment", "Error state")}}
                }
            }

    binding.signUpText.setOnClickListener {

    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

}

}


}




