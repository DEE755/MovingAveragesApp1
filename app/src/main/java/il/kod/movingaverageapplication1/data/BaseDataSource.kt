package il.kod.movingaverageapplication1.data

import android.util.Log
import il.kod.movingaverageapplication1.utils.HttpMethod
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.getDummyResponse

import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T> getResult(
        call: suspend () -> Response<T>,
        method: HttpMethod = HttpMethod.GET
    ): Resource<T> {
        try {
            Log.d("BaseDataSource", "entered try")
            val result = call()
            val message_: String
            if (result.isSuccessful) {
                message_ = result.message() ?: "Success"
                val body = result.body()
                if (method == HttpMethod.POST) {
                    // For POST requests, use a dummy response if the body is null
                    return Resource.success(message_, body ?: getDummyResponse() as T)
                } else if (method == HttpMethod.GET) {
                    // For GET requests, check if the body is null
                    return if (body != null) {
                        Resource.success(message_, body) // Success case
                    } else {
                        Resource.error("Response body is null for GET request") // Null body case
                    }
                }
            }
            // Handle unsuccessful responses
            return Resource.error("Network call has failed for the following reason: " +
                    "${result.message()} ${result.code()}")
        } catch (e: Exception) {
            Log.d("BaseDataSource", "entered catch")
            // Handle exceptions during the network call
            return Resource.error("Network call has failed for the following reason: " +
                    (e.localizedMessage ?: e.toString()))
        }
    }
}