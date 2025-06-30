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
import il.kod.movingaverageapplication1.databinding.FragmentInsideFollowSetBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.ui.viewmodel.DialogViewModel
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
    private val dialogViewModel: DialogViewModel by activityViewModels()


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
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)//TODO(LINEAR)


        viewModelFollowSet.clickedFollowSet.value.let { currentFollowSet ->
            Log.d("InsideFollowSetFragment", "FollowSet: $currentFollowSet")
            lifecycleScope.launch {
                val extractedIds = currentFollowSet?.extractStocksToIntArray()

                val extractedStocks = withContext(Dispatchers.IO) {
                    viewModelAllStocks.getStocksByIds(*extractedIds ?: intArrayOf())
                }

                 //binding.recyclerView.layoutManager= GridLayoutManager(requireContext(), 4)

                binding.recyclerView.adapter = StockRecyclerAdapterFragment(
                    extractedStocks,
                    callBack = object : StockRecyclerAdapterFragment.SearchedStockClickListener {
                        override fun onSearchedStockClicked(index: Int) {
                            val clickedStock = extractedStocks[index]
                            clickedStock.let {
                                viewModelDetailStock.setStock(clickedStock)
                                findNavController().navigate(
                                    R.id.action_insideFollowSetFragment_to_detailsItemFragment
                                )
                            }
                        }

                        override fun onSearchedStockLongClicked(index: Int) {}
                    },
                    glide = glide
                )
            }

            //binding.textView.text = currentFollowSet?.name

            binding.setMoreActionsButton.setOnClickListener {
                val items = listOf(
                    getString(R.string.set_alert_followset),
                    getString(R.string.ask_ai_advisor_followset),
                    getString(R.string.write_note_followset)
                )
                val dialog = RecyclerDialogFragment(items) { selectedItem, dialogInstance ->
                    if (selectedItem == items[0]) {
                       dialogViewModel.showThresholdInputDialogVM(requireContext(),
                           function = {thresholdValue ->
                               viewModelFollowSet.addNotification(currentFollowSet!!, priceThreshold = thresholdValue)
                                   Toast.makeText(
                                   requireContext(),
                           getString(R.string.alert_threshold_reached, thresholdValue.toString()),
                           Toast.LENGTH_LONG
                       ).show()

                        _binding?.textView2?.text =
                            getString(R.string.alert_set_at, thresholdValue.toString())

                                      },
                            followSetName = currentFollowSet!!.name


                       )

                    } else if (selectedItem == items[1]) {
                        findNavController().navigate(
                            R.id.action_insideFollowSetFragment_to_followSetAskAIFragment
                        )
                    }

                    else if (selectedItem == items[2])
                    {
                        dialogViewModel.showDescriptionInputDialog(context = requireContext(),
                            onDescriptionEntered = {
                                description ->currentFollowSet?.userComments = description
                                currentFollowSet?.isDirty = true
                                //sessionManager.sync.setSomethingisDirty(true)
                                binding.seeCommentButton.isEnabled
                                binding.seeCommentButton.alpha=1f
                                                   },

                            object1 = currentFollowSet)

                    }
                    dialogInstance.dismiss() // Dismiss the dialog
                }
                dialog.show(parentFragmentManager, "Select an Action for ${currentFollowSet?.name ?: "this"} FollowSet")


            }

            if (currentFollowSet?.userComments.isNullOrEmpty()) {
                binding.seeCommentButton.isEnabled=false
                binding.seeCommentButton.alpha=.5f // Make it transparent to indicate it's disabled
            } else {
                binding.seeCommentButton.visibility = View.VISIBLE
            }

            binding.seeCommentButton.setOnClickListener { dialogViewModel.showDescriptionDialog(requireContext(), currentFollowSet!! ) }}


            binding.backButton.setOnClickListener {
                findNavController().popBackStack()
            }


            return binding.root
        }


    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.following_set_title, viewModelFollowSet.clickedFollowSet.value?.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lifecycleScope.launch {
            viewModelFollowSet.unbindService()
        }
    }
}