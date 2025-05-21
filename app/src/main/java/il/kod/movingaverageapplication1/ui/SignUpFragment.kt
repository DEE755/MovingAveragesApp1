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
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.databinding.SignupFragmentBinding
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import kotlin.getValue

class SignUpFragment : Fragment()
{

    var _binding: SignupFragmentBinding? = null
    val binding get() = _binding!!

    private val CSDviewModel: CustomServerDatabaseViewModel by activityViewModels()


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





                CSDviewModel.http_response.observe(viewLifecycleOwner) {
                    Log.d("LoginFragment", "observe called")
                    when(it.status){
                        is Loading->{binding.progressBar.isVisible=true
                            binding.loadingText.isVisible=true
                            Log.d("LoginFragment", "Loading state")}

                        is Success->{Toast.makeText(requireContext(),it.status.data, Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_signUpFragment_to_followedStocks)
                            Log.d("LoginFragment", "Success state")
                        //if 404 is in http_response, then show error message
                            //if (CSDviewModel.http_response.value?.data.toString().contains("404")) {
                                //Toast.makeText(requireContext(), "Username already exists", Toast.LENGTH_SHORT).show()

                            //} else {
                                //Toast.makeText(requireContext(), "Successfully Signed up as ${CSD.client_username}", Toast.LENGTH_SHORT).show()


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




