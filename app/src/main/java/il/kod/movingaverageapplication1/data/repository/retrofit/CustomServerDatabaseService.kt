package il.kod.movingaverageapplication1.data.repository.retrofit
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.models.StringAdapterForGson
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.data.models.StringAdapterCount
import il.kod.movingaverageapplication1.data.objectclass.FollowSet

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Query

//functions to communicate with the custom cloud database via retrofit (using the base url to our custom server stored in Constants.kt)


    interface CustomServerDatabaseServiceNoToken {

        //this interface uses the version of retrofit for public url that does not need to check user identity

        //LOGIN/SIGNUP
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
        @GET("/getall_remoteDB_stocks/")
        suspend fun getAllStocks(@Query("limit") limit: Int
        ): Response<List<Stock>>

        @GET("/get_stocks_from_to/")
        suspend fun getStocksStartingFromSymbol(@Query("symbol") symbol: String): Response<List<Stock>>



        @GET("/get_number_of_stocks")
        suspend fun getNbOfStocksInRemoteDB() : Response<StringAdapterCount>


        //AI PERPLEXITY
        //ask a question:
        @FormUrlEncoded
        @POST("/ask")
        suspend fun ask_ai(
            @Field("question") question: String
        ): Response<StringAdapterForGson>


    }

    interface CustomServerDatabaseServiceWithToken {
//this interface uses the version of retrofit that already has implemented the token in the header and the protected base_url /user
        //ALL BASE URL ALREADY HAS /user IN IT and the token is already added to the header

        @FormUrlEncoded
        @POST("follows_stock")
        suspend fun setUserFollowsStock(
            @Field("stockSymbol") stockSymbol: String
        )

        @GET("get_update_for_followed_stocksPR")
        suspend fun getFollowedStockPrice(): Response<List<Stock>>


        @GET("get_update_for_followed_stocksMA")
        suspend fun getFollowedMovingAverages(): Response<List<Stock>>




        //TODO(TO IMPLEMENT)


        @POST("followset/push")
        suspend fun pushFollowSetToRemoteDB(
            @Body followSet: FollowSet) : Response<Any>

        @GET("followset/pull")
        suspend fun pullUserFollowSetFromRemoteDB(): Response<List<FollowSet>>

        @FormUrlEncoded
        @POST("unfollows_stock")
        suspend fun setUserUnfollowsStock(
            @Field("symbol") symbol: String): Response<String>
    }
