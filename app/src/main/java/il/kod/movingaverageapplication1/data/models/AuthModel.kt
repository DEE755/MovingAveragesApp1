package il.kod.movingaverageapplication1.data.models

data class AuthRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val message: String?,           // for signup
    val accessToken: String?,       // for login
    val refreshToken: String?,       // for login
    val username: String? //from database -> consider using user email for login/signup instead of username and then get here associated username
)
