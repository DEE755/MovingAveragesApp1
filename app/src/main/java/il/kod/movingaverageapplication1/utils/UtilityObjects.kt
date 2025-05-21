package il.kod.movingaverageapplication1.utils

enum class HttpMethod(val isPost: Boolean) {
    POST(true),
    GET(false)
}


data class DummySuccess(val message: String = "Dummy Success Response")

// Dummy response function
fun getDummyResponse(): DummySuccess {
    return DummySuccess()
}