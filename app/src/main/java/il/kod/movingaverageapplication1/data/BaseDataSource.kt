package il.kod.movingaverageapplication1.data

import android.util.Log
import il.kod.movingaverageapplication1.utils.HttpMethod
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.getDummyResponse

import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T>
            getResult( call : suspend () -> Response<T>, method: HttpMethod = HttpMethod.GET) : Resource<T> {
        try {
            Log.d("BaseDataSource", "entered try")
            val result  = call()
            val message_: String

            if(result.isSuccessful) {
                Log.d("BaseDataSource", "Response is successful")
                message_= result.message()?:"Success"
                val body  = result.body()
                if( method == HttpMethod.POST) return Resource.success(message_, body?:getDummyResponse()as T)
                else if(method == HttpMethod.GET && body != null) return  Resource.success(message_,body)
            }

            val adaptedResult: String = when (result.code()) {
                400 -> "Bad Request"
                401 -> "Wrong Credentials Please Try Again"
                403 -> "Forbidden"
                404 -> "Not Found"
                500 -> "Internal Server Error"
                else -> "Unknown Error"
            }

            Log.d("BaseDataSource", "Response: ${result.code()}, Message: ${adaptedResult}, Body: ${result.body()}")
            return Resource.error("Network Error: $adaptedResult")


        }catch (e : Exception) {
            Log.d("BaseDataSource", "entered catch , error: ${e.message}")
            return Resource.error("Network call has failed for the following reason: "
             + (e.localizedMessage ?: e.toString()))
        }
    }



}