package il.kod.movingaverageapplication1.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.data.objectclass.AiQuestion
import javax.inject.Inject

@HiltViewModel
class AiQuestionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel()

{


    val customUserQuestion: String = context.getString(R.string.default_empty_question) //to be set by the user in the UI

    var allQuestions: MutableList<AiQuestion>
    var allQuestionsFollowSet: MutableList<AiQuestion>

    val passResponsibilityWarnings: String = context.getString(R.string.ai_responsibility_warning)


//Hardcoded Questions


    val aiQuestion0: AiQuestion = AiQuestion(
        context.getString(R.string.ai_company_global_information),
        context.getString(R.string.ai_what_about_company),
        context.getString(R.string.default_empty_question),
        null
    )

    val aiQuestion1: AiQuestion = AiQuestion(
        context.getString(R.string.ai_company_global_information),
        context.getString(R.string.ai_should_invest_stock),
        passResponsibilityWarnings,
        null
    )

    val aiQuestion2: AiQuestion =
        AiQuestion(context.getString(R.string.ai_stock_history), context.getString(R.string.ai_what_is_history), context.getString(R.string.default_empty_question), null)


    val aiCustomQuestion: AiQuestion = AiQuestion(context.getString(R.string.ai_custom_question), context.getString(R.string.ai_ask_own_question), context.getString(R.string.default_empty_question), null)


    val aiQuestionFollowSet0: AiQuestion = AiQuestion(context.getString(R.string.ai_followset_global_advising), context.getString(R.string.ai_good_idea_investing),
        passResponsibilityWarnings, null)

    val aiQuestionFollowSet1: AiQuestion = AiQuestion(context.getString(R.string.ai_followset_potential_replacement), context.getString(R.string.ai_should_replace_stocks), passResponsibilityWarnings, null)

    val aiQuestionsFollowSet2: AiQuestion = AiQuestion(context.getString(R.string.ai_followset_history), context.getString(R.string.ai_histories_of_stocks), context.getString(R.string.default_empty_question), null)

    init {
allQuestions = mutableListOf(aiQuestion0, aiQuestion1, aiQuestion2, aiCustomQuestion)
            allQuestionsFollowSet = mutableListOf(aiQuestionFollowSet0, aiQuestionFollowSet1, aiQuestionsFollowSet2, aiCustomQuestion)

}
}