package il.kod.movingaverageapplication1.ui.fragment.followset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.databinding.AskAiFollowSetBinding
import il.kod.movingaverageapplication1.ui.fragment.QuestionRecyclerAdapterFragment
import il.kod.movingaverageapplication1.ui.viewmodel.AiQuestionsViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.FollowSetViewModel
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.formatHTML
import il.kod.movingaverageapplication1.utils.showCustomQuestionInputDialog
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FollowSetAskAIFragment: Fragment() {
    @Inject
    lateinit var glide: RequestManager

    var _binding : AskAiFollowSetBinding?= null
    val binding get()=_binding!!

    //private val viewModelAllStocks: il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel by activityViewModels()
    private val viewModelDetailStock: DetailStockViewModel by activityViewModels()
    private val CSDviewModel: CustomServerDatabaseViewModel by activityViewModels()
    private val questionsViewModel: AiQuestionsViewModel by viewModels()
    private val viewModelAllStocks: AllStocksViewModel by activityViewModels()
    private val viewModelFollowSet : FollowSetViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AskAiFollowSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFollowSet.clickedFollowSet.value.let { followSet ->
            binding.followSetTitle.text = followSet?.name
            var completeSymbols: String = ""


           lifecycleScope.launch {

                val stocks = viewModelAllStocks.getStocksByIds(*followSet.set_ids.toIntArray())


                stocks.forEach {
                    completeSymbols += "${it.symbol}, "
                    //TODO(LIMIT TO 4 and add ... or "and more" if more than 4)}
                }


                binding.stockSymbols.text = completeSymbols


                //glide.load(stock?.logo_url?.toUri()).into(binding.itemImage)


                binding.questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.questionRecyclerView.adapter = QuestionRecyclerAdapterFragment(
                    questions = questionsViewModel.allQuestionsFollowSet,
                    callBack = object : QuestionRecyclerAdapterFragment.QuestionListener {

                        override fun onItemClicked(index: Int) {
                            lifecycleScope.launch {
                                val question =
                                    if (index == questionsViewModel.allQuestionsFollowSet.size - 1) {
                                        val input = showCustomQuestionInputDialog(
                                            title = "Ask a Custom Question about those stocks",
                                            message = "Write your question here",
                                            context = requireContext()
                                        )
                                        input?.let { it + "\n" + questionsViewModel.passResponsibilityWarnings }
                                            ?: ""
                                    } else {
                                        questionsViewModel.allQuestionsFollowSet[index].question
                                    }




                                if (question.isNotBlank() && question.length > 3) {

                                    val stockNames = stocks.map { it.name }.toTypedArray()
                                    CSDviewModel.askAIFollowSet(*stockNames, question = question)

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please enter a valid question",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                CSDviewModel.AI_Answer.observe(viewLifecycleOwner) {
                                    when (it.status) {
                                        is Loading -> {
                                            binding.progressBar.isVisible = true
                                            binding.questionRecyclerView.isVisible = false
                                        }

                                        is Error -> {
                                            Toast.makeText(
                                                requireContext(),
                                                it.status.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            binding.progressBar.isVisible = false
                                        }

                                        is Success -> {
                                            binding.progressBar.isVisible = false
                                            val aiAnswer = it.status.data.toString()
                                            val htmlContent = formatHTML(aiAnswer)
                                            binding.answerHtml.loadData(
                                                htmlContent,
                                                "text/html",
                                                "UTF-8"
                                            )
                                            binding.answerHtml.isVisible = true
                                            binding.warningText.isVisible = true
                                        }
                                    }
                                }
                            }
                        }

                        override fun onItemLongClicked(index: Int) {}
                    },
                    glide = glide
                )


                binding.returnButton.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}