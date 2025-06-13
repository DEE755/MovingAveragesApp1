package il.kod.movingaverageapplication1.utils

fun formatText(input: String): String {
    return input
        .replace("## (.*?)\\n".toRegex()) { matchResult ->
            "\n\n${matchResult.groupValues[1].uppercase()}\n\n"
        }
        .replace("### (.*?)\\n".toRegex()) { matchResult ->
            "- ${matchResult.groupValues[1]}"
        }
        .replace("\\*\\*(.*?)\\*\\*".toRegex()) { matchResult ->
            matchResult.groupValues[1].uppercase()
        }
        .replace("\\[\\d+\\]".toRegex(), "") // Remove reference markers like [1], [2]
        .trim()
}

fun formatHTML(input: String): String =
    input.replace("## ", "<h2 style='color: #4CAF50;'>")
        .replace("\n\n", "</h2><p>")
        .replace("**", "<b>")
        .replace("\n", "<br>")