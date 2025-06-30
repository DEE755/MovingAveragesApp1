package il.kod.movingaverageapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.objectclass.AiQuestion
import javax.inject.Inject

@HiltViewModel
class AiQuestionsViewModel @Inject constructor(

) : ViewModel()

{


    val customUserQuestion: String = "" //to be set by the user in the UI

    var allQuestions: MutableList<AiQuestion>
    var allQuestionsFollowSet: MutableList<AiQuestion>

    val passResponsibilityWarnings: String =
        "I acknowledge that you are not responsible for any of my decisions and certify I won't take your responsibility for any of your answers, this is just a simple knowledge question."


//Hardcoded Questions


    val aiQuestion0: AiQuestion = AiQuestion(
        "Company Global Information",
        "What can you tell me about this company?",
        "",
        null
    )

    val aiQuestion1: AiQuestion = AiQuestion(
        "Company Global Information",
        "Should I invest this stock? ",
        passResponsibilityWarnings,
        null
    )

    val aiQuestion2: AiQuestion =
        AiQuestion("Stock History", "What is the history of this stock?", "", null)


    val aiCustomQuestion: AiQuestion = AiQuestion("Custom Question", "Ask your own question", "", null)


    val aiQuestionFollowSet0: AiQuestion = AiQuestion("Follow Set Global Advising", "Is it a good idea investing those stocks altogether?",
        passResponsibilityWarnings, null)

    val aiQuestionFollowSet1: AiQuestion = AiQuestion("Follow Set Potential Replacement", "Should I replace one of more of those stocks if investing in this set ?", passResponsibilityWarnings, null)

    val aiQuestionsFollowSet2: AiQuestion = AiQuestion("Follow Set History", "What are the histories of those stocks?", "", null)

    init {
allQuestions = mutableListOf(aiQuestion0, aiQuestion1, aiQuestion2, aiCustomQuestion)
            allQuestionsFollowSet = mutableListOf(aiQuestionFollowSet0, aiQuestionFollowSet1, aiQuestionsFollowSet2, aiCustomQuestion)

}
}