package il.kod.movingaverageapplication1.data.repository
import android.util.Log

import il.kod.movingaverageapplication1.data.models.UserProfileTransitFromGson

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Query

//functions to communicate with the custom cloud database via retrofit (using the base url to our custom server stored in Constants.kt)
interface CustomServerDatabaseService {
    @GET("/login_request/")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<List<UserProfileTransitFromGson>>


    @FormUrlEncoded
    @POST("/submit")
    suspend fun sign_up(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Void>

}