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

    lateinit var allQuestions: MutableList<AiQuestion>

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
        AiQuestion("Company Global Information", "What is the history of this stock?", "", null)


    val aiCustomQuestion: AiQuestion = AiQuestion("Custom Question", "Ask your own question", "", null)

        init {
allQuestions = mutableListOf(aiQuestion0, aiQuestion1, aiQuestion2, aiCustomQuestion)

}
}