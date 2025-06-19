package il.kod.movingaverageapplication1.ui.fragment

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.databinding.FragmentInsideFollowSetBinding
import il.kod.movingaverageapplication1.utils.showThresholdInputDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.ui.viewmodel.FollowSetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class InsideFollowSetFragment : Fragment() {
    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentInsideFollowSetBinding? = null
    private val binding get() = _binding!!

    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val viewModelFollowSet : FollowSetViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModelFollowSet.bindService()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsideFollowSetBinding.inflate(inflater, container, false)
        Log.d("InsideFollowSetFragment", "onCreateView called")
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)


        viewModelFollowSet.clickedFollowSet.value.let { followSet ->
            Log.d("InsideFollowSetFragment", "FollowSet: $followSet")
            lifecycleScope.launch {
                val extractedIds = followSet?.extractStocksToIntArray()

                val extractedStocks = withContext(Dispatchers.IO) {
                    viewModelAllStocks.getStocksByIds(*extractedIds ?: intArrayOf())
                }

                /* binding.recyclerView.layoutManager= GridLayoutManager(requireContext(), 4)*/
                binding.recyclerView.adapter = StockRecyclerAdapterFragment(
                    extractedStocks,
                    callBack = object : StockRecyclerAdapterFragment.ItemListener {
                        override fun onItemClicked(index: Int) {
                            val clickedStock = extractedStocks[index]
                            clickedStock.let {
                                viewModelDetailStock.setStock(clickedStock)
                                findNavController().navigate(
                                    R.id.action_insideFollowSetFragment_to_detailsItemFragment
                                )
                            }
                        }

                        override fun onItemLongClicked(index: Int) {}
                    },
                    glide = glide
                )
            }

            binding.textView.text = followSet?.name

            binding.setThresholdButton.setOnClickListener {
                val items = listOf(
                    "Set an Alert for this FollowSet",
                    "Ask AI adviser about this followset",
                    "Item 3"
                )
                val dialog = RecyclerDialogFragment(items) { selectedItem ->
                    if (selectedItem == items[0]) {
                        showThresholdInputDialog(
                            requireContext(),
                            {
                                viewModelFollowSet.addNotification(followSet!!, it)

                                Toast.makeText(
                                    context,
                                    getString(R.string.alert_threshold_reached, it.toString()),
                                    Toast.LENGTH_LONG
                                ).show()

                                binding.textView2.text =
                                    getString(R.string.alert_set_at, it.toString())
                            },
                            getString(R.string.set_threshold_alert, followSet?.name),
                            getString(R.string.alert_threshold_message)
                        )
                    } else if (selectedItem == items[1]) {
                        findNavController().navigate(
                            R.id.action_insideFollowSetFragment_to_followSetAskAIFragment
                        )
                    }
                }
                dialog.show(parentFragmentManager, "RecyclerDialog")


            }


            binding.backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            return binding.root
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.following_set_title, binding.textView.text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lifecycleScope.launch {
            viewModelFollowSet.unbindService()
        }
    }
}