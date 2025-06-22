package il.kod.movingaverageapplication1.ui.fragment

import android.graphics.Color
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.databinding.DetailsStockLayoutBinding
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


@AndroidEntryPoint
class DetailsStockFragment: Fragment() {


    @Inject
    lateinit var glide: RequestManager

    var _binding : DetailsStockLayoutBinding?= null
    val binding get()=_binding!!

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailsStockLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val clickedStock : MutableLiveData<Stock> = viewModelDetailStock.clickedStock
        clickedStock.let {stock->
            val selecStock=stock.value
            if (selecStock.isSelected){

                binding.addButton.text = "Currently Following"
                binding.addButton.isEnabled=false

            } else {

                binding.addButton.visibility = View.VISIBLE
            }
            binding.stockSymbol.text =stock.value?.symbol

            if (selecStock.ma_25.isNaN() || selecStock.ma_25 == 0.00) {
                binding.maView.visibility = View.GONE
            } else {
                binding.maView.visibility = View.VISIBLE
                val title="Moving Averages:"
                val ma25="25: ${BigDecimal(selecStock.ma_25).setScale(2, RoundingMode.HALF_UP).toDouble()}"
                val ma50="50: ${BigDecimal(selecStock.ma_50).setScale(2, RoundingMode.HALF_UP).toDouble()}"
                val ma150="150: ${BigDecimal(selecStock.ma_150).setScale(2, RoundingMode.HALF_UP).toDouble()}"
                val ma200="200: ${BigDecimal(selecStock.ma_200).setScale(2, RoundingMode.HALF_UP).toDouble()}"

               val full="$title\n\t\t\t\t\t\t$ma25\n\t\t\t\t\t\t$ma50\n\t\t\t\t\t\t$ma150\n\t\t\t\t\t\t$ma200"

                // Create a SpannableString to style the text
               val spannableString = SpannableString(full)
                spannableString.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0, title.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                spannableString.setSpan(
                    AbsoluteSizeSpan(20, true),
                    0, title.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


                binding.maView.text = spannableString


            }


            binding.stockCompany.text =stock.value?.name?:"N/A"
            binding.stockPrice.text = if (stock.value?.current_price != 0.00) {
                "Live price: ${stock.value?.current_price}"
            } else {
                "Follow to get live updates"
            }
            //binding.numberOfShares.text = clickedStock.value?.movingAverage.toString()
            //Glide.with(requireContext()).load(clickedStock.value?.imageUri?.toUri()).into(binding.itemImage)
            glide.load(stock.value?.logo_url?.toUri()).into(binding.itemImage)


            binding.addButton.setOnClickListener {

                Log.d("DetailsStockFragment", "stoclk clicked: ${stock.value}")
                viewModelAllStocks.setUserFollowsStockData(stock.value!!, true)

                findNavController().popBackStack()
                Toast.makeText(requireContext(), "${stock.value?.name} was Added to selected stocks", Toast.LENGTH_SHORT).show()

            }
            binding.askAi.setOnClickListener {
                findNavController().navigate(R.id.action_detailsItemFragment_to_askAIFragment)
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