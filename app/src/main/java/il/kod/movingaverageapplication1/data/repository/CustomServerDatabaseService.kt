package il.kod.movingaverageapplication1.data.repository
import android.util.Log
import il.kod.movingaverageapplication1.data.Stock

import il.kod.movingaverageapplication1.data.models.UserProfileTransitFromGson

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Query

//functions to communicate with the custom cloud database via retrofit (using the base url to our custom server stored in Constants.kt)
interface CustomServerDatabaseService {

    //LOGIN/SIGNUP

    //login
    @GET("/login_request/")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<List<UserProfileTransitFromGson>>


    //sign up
    @FormUrlEncoded
    @POST("/submit")
    suspend fun sign_up(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<String>


    //STOCKS

    //get all stocks /run once in a while + button to update stocks + maybe AI that tell you if there is more stocks that should be added
    @GET("/getall_remoteDB_stocks")
    suspend fun get_all_stocks(): Response<List<Stock>>



//AI PERPLEXITY
    //ask a question:
    @FormUrlEncoded
    @POST("/ask")
    suspend fun ask_question_ai(
        @Field("question") question: String
    ): Response<String>
}