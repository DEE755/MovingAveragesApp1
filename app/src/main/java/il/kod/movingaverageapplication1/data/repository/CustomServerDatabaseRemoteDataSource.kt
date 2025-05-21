package il.kod.movingaverageapplication1.data.repository

import android.util.Log
import il.kod.movingaverageapplication1.data.BaseDataSource
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.models.UserProfileTransitFromGson
import il.kod.movingaverageapplication1.utils.HttpMethod
import il.kod.movingaverageapplication1.utils.Resource
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject



//used to call the cloud database service and envelop the data inside a pattern with success/failure responses
class CustomServerDatabaseRemoteDataSource @Inject constructor(
    private val customCloudDatabaseService: CustomServerDatabaseService
) : BaseDataSource()
{
    suspend fun login(username: String, password: String) =
        getResult({customCloudDatabaseService.login(username, password)})



    suspend fun signUp(username: String, password: String): Resource<String> =
    getResult({
        try {
            Log.d("CustomServerDatabaseRemoteDataSource", "signUp started")
            val response = customCloudDatabaseService.sign_up(username, password)
            Log.d("CustomServerDatabaseRemoteDataSource", "sign_up response: $response")
            if (response.body() == null) {
                Log.e("CustomServerDatabaseRemoteDataSource", "Response body is null")
            } else {
                Log.d("CustomServerDatabaseRemoteDataSource", "sign_up response body: ${response.body()}")
            }
            response
        } catch (e: Exception) {
            Log.e("CustomServerDatabaseRemoteDataSource", "Exception during signUp: ${e.message}", e)
            throw e
        }
    }, HttpMethod.POST)

    suspend fun getAllStocks(): Resource<List<Stock>> =
        getResult({
            customCloudDatabaseService.get_all_stocks()
        })

}

