package il.kod.movingaverageapplication1.ui.fragment


import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.AiQuestion
import il.kod.movingaverageapplication1.ui.viewmodel.DetailStockViewModel
import il.kod.movingaverageapplication1.ui.viewmodel.CustomServerDatabaseViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.databinding.AskAiLayoutBinding
import il.kod.movingaverageapplication1.ui.viewmodel.AiQuestionsViewModel
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.formatHTML
import il.kod.movingaverageapplication1.utils.showConfirmationDialog
import il.kod.movingaverageapplication1.utils.showCustomQuestionInputDialog
import kotlinx.coroutines.launch
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
    private val questionsViewModel: AiQuestionsViewModel by viewModels()



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

        val clickedStock  = viewModelDetailStock.clickedStock.value
        clickedStock.let {stock->

            binding.stockSymbol.text ="${stock?.symbol}"

            binding.stockCompany.text =stock?.name
            binding.stockPrice.text = if (stock?.current_price != null) {
                "Current prices: ${stock.current_price}"
            } else {
                "Follow to get live updates"
            }

            glide.load(stock?.logo_url?.toUri()).into(binding.itemImage)




binding.questionRecyclerView.layoutManager=LinearLayoutManager(requireContext())
            binding.questionRecyclerView.adapter = QuestionRecyclerAdapterFragment(
                questions = questionsViewModel.allQuestions,
                callBack = object : QuestionRecyclerAdapterFragment.QuestionListener {

                    override fun onItemClicked(index: Int) {
                        lifecycleScope.launch {
                            val question = if (index == questionsViewModel.allQuestions.size - 1) {
                                val input = showCustomQuestionInputDialog(
                                    title = "Ask a Custom Question",
                                    message = "Write your question here",
                                    context = requireContext()
                                )
                                input?.let { it + "\n" + questionsViewModel.passResponsibilityWarnings } ?: ""
                            } else {
                                questionsViewModel.allQuestions[index].question
                            }

                            if (question.isNotBlank() && question.length > 3) {
                                CSDviewModel.askAI(clickedStock, question)
                            } else {
                                Toast.makeText(requireContext(), "Please enter a valid question", Toast.LENGTH_SHORT).show()
                            }

                            CSDviewModel.AI_Answer.observe(viewLifecycleOwner) {
                                when (it.status) {
                                    is Loading -> {
                                        binding.progressBar.isVisible = true
                                        binding.questionRecyclerView.isVisible = false
                                    }
                                    is Error -> {
                                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                                        binding.progressBar.isVisible = false
                                    }
                                    is Success -> {
                                        binding.progressBar.isVisible = false
                                        val aiAnswer = it.status.data.toString()
                                        val htmlContent = formatHTML(aiAnswer)
                                        binding.answerHtml.loadData(htmlContent, "text/html", "UTF-8")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
