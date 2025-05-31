package il.kod.movingaverageapplication1.ui.fragment


import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.databinding.AskAiLayoutBinding
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class AskAIFragment: Fragment() {
    @Inject
    lateinit var glide: RequestManager

    var _binding : AskAiLayoutBinding?= null
    val binding get()=_binding!!

    //private val viewModelAllStocks: il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val CSDviewModel: CustomServerDatabaseViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AskAiLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CSDviewModel.askAI(viewModelDetailStock.clickedStock.value!!)

        val clickedStock : MutableLiveData<Stock> = viewModelDetailStock.clickedStock
        clickedStock.let {stock->

            binding.stockSymbol.text ="${stock.value?.symbol}"

            binding.stockCompany.text =stock.value?.name?:"N/A"
            binding.stockPrice.text = if (stock.value?.current_price != null) {
                "Current prices: ${stock.value?.current_price}"
            } else {
                "Follow to get live updates"
            }

            glide.load(stock.value?.logo_url?.toUri()).into(binding.itemImage)

            CSDviewModel.AI_Answer.observe(viewLifecycleOwner)
            {
                when (it.status) {
                    is Loading -> {
                        binding.progressBar.isVisible = true
                        binding.aiAnswerText.text = "Loading..."

                    }

                    is Error -> {
                        binding.aiAnswerText.text = it.status.message
                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.isVisible = false
                    }

                    is Success -> {
                        binding.progressBar.isVisible = false
                        binding.aiAnswerText.let { AIText ->
                            AIText.text = it.status.data.toString()

                            AIText.movementMethod =
                                ScrollingMovementMethod.getInstance()
                            AIText.isVerticalScrollBarEnabled = true
                        }
                    }

                }
            }



            binding.returnButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
