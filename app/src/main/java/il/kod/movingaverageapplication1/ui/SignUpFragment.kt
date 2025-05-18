package il.kod.movingaverageapplication1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.databinding.SignupFragmentBinding

class SignUpFragment : Fragment()
{

    var _binding: SignupFragmentBinding? = null
    val binding get() = _binding!!
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

    binding.signUpText.setOnClickListener {

    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

}

}


}




