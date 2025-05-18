package il.kod.movingaverageapplication1.data.repository

import android.util.Log
import il.kod.movingaverageapplication1.data.BaseDataSource
import il.kod.movingaverageapplication1.data.models.UserProfileTransitFromGson
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
        getResult { Log.d("CustomServerDatabaseRemoteDataSource", "login called with username: $username and password: $password")
            customCloudDatabaseService.login(username, password) }



    suspend fun signUp(username: String, password: String) : Resource<String> =
        getResult { customCloudDatabaseService.sign_up(username, password) }

}