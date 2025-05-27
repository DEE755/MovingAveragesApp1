package il.kod.movingaverageapplication1.data.repository
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.StringAdapterForGson
import il.kod.movingaverageapplication1.data.models.AuthResponse

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
   /* @GET("/login_request/")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<List<UserProfileTransitFromGson>>*/

     @GET("/login_request/")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<AuthResponse>


    //sign up
    @FormUrlEncoded
    @POST("/submit")
    suspend fun sign_up(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthResponse>


    //STOCKS

    //get all stocks /run once in a while + button to update stocks + maybe AI that tell you if there is more stocks that should be added
    @GET("/getall_remoteDB_stocks")
    suspend fun get_all_stocks(): Response<List<Stock>>


    @GET("/get_followed_stock_prices")
    suspend fun getFollowedStockPrice(): Response<Double>

    @POST("/user/follows_stock")
    suspend fun setUserFollowsStock(stock: Stock) : Response<String>

    @POST("/user/unfollows_stock")
    suspend fun setUserUnfollowsStock(stock: Stock) : Response<String>

//AI PERPLEXITY
    //ask a question:
    @FormUrlEncoded
    @POST("/ask")
    suspend fun ask_ai(
        @Field("question") question: String
    ): Response<StringAdapterForGson>


}