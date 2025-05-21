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
    Log.d("BaseDataSource", "entered getResult")
        try {
            Log.d("BaseDataSource", "entered try")
            val result  = call()
            val message_: String
            Log.d("BaseDataSource", "after call")
            Log.d("BaseDataSource", "getResult: ${result}")
            if(result.isSuccessful) {
                message_= result.message()?:"Success"
                Log.d("BaseDataSource", "entered isSuccessful")
                val body = result.body()
                if( method == HttpMethod.POST) return Resource.success(message_, body?:getDummyResponse()as T)
                else if(method == HttpMethod.GET && body != null) return  Resource.success(message_,body)

            }
            Log.d("BaseDataSource", "entered isNOTSuccessful")
            //Log.d("BaseDataSource", "getResult: ${result.body()}")
            return Resource.error("Network call has failed for the following reason: " +
                    "${result.message()} ${result.code()}")
        }catch (e : Exception) {
            Log.d("BaseDataSource", "entered catch")
            return Resource.error("Network call has failed for the following reason: "
             + (e.localizedMessage ?: e.toString()))
        }
    }
}