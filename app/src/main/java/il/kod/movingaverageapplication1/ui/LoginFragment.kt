package il.kod.movingaverageapplication1.ui

import AllStocksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.SelectedStocksViewModel
import il.kod.movingaverageapplication1.databinding.LoginFragmentBinding
import kotlin.getValue

class LoginFragment : Fragment() {

    //shared viewmodel
    private val viewModelSelectedStock: SelectedStocksViewModel by activityViewModels()

    //not shared viewmodel
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()

    //binding

    var _binding : LoginFragmentBinding?= null
    val binding get()=_binding!!


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

            val username=binding.nameInput.text.toString()
            val password=binding.passwordInput.text.toString()

            Toast.makeText(requireContext(), "Welcome: $username !", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_navigation_graph_to_selectedStocks2)
        }
    }
}