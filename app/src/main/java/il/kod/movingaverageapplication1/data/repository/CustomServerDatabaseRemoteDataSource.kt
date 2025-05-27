package il.kod.movingaverageapplication1.data.repository

import android.util.Log
import il.kod.movingaverageapplication1.data.BaseDataSource
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.utils.HttpMethod
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.formatText
import retrofit2.Response
import javax.inject.Inject



//used to call the cloud database service and envelop the data inside a pattern with success/failure responses
class CustomServerDatabaseRemoteDataSource @Inject constructor(
    private val CSDService: CustomServerDatabaseService
) : BaseDataSource() {
    suspend fun login(username: String, password: String):Resource<AuthResponse> =
        getResult({ CSDService.login(username, password) })


    suspend fun signUp(username: String, password: String): Resource<AuthResponse> =
        getResult({
            try {
                Log.d("CustomServerDatabaseRemoteDataSource", "signUp started")
                val response = CSDService.sign_up(username, password)
                Log.d("CustomServerDatabaseRemoteDataSource", "sign_up response: $response")
                if (response.body() == null) {
                    Log.e("CustomServerDatabaseRemoteDataSource", "Response body is null")
                } else {
                    Log.d(
                        "CustomServerDatabaseRemoteDataSource",
                        "sign_up response body: ${response.body()}"
                    )
                }
                response
            } catch (e: Exception) {
                Log.e(
                    "CustomServerDatabaseRemoteDataSource",
                    "Exception during signUp: ${e.message}",
                    e
                )
                throw e
            }
        }, HttpMethod.POST)

    suspend fun getAllStocks(): Resource<List<Stock>> =
        getResult({
            CSDService.get_all_stocks()
        })

/*    suspend fun getStockPrice(stock: Stock): Resource<String> =
        {return }*/

    suspend fun userFollowsStock(stock: Stock, follow: Boolean): Resource<String> =
        if (follow) {
            getResult({
                CSDService.setUserFollowsStock(stock)
            }, HttpMethod.POST)
        } else {
            getResult({
                CSDService.setUserUnfollowsStock(stock)
            }, HttpMethod.POST)
        }


    suspend fun askAI(stock: Stock): Resource<String> = getResult({
        val question = "What can you tell me about the stock ${stock.name}?"
        val response = CSDService.ask_ai(question)

        val reply = formatText(response.body()?.reply ?: "No reply found")
        Response.success(reply)
    }, HttpMethod.POST)

}

