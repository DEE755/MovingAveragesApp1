package il.kod.movingaverageapplication1.data.objectclass

class AiQuestion(
    var name: String,
    var question: String,
    val hiddenAdditionalPrompt: String="",
    var image: String? = null,
)