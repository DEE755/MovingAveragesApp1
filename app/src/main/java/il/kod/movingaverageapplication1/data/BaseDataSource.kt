package il.kod.movingaverageapplication1.data

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import il.kod.movingaverageapplication1.R
import il.kod.movingaverageapplication1.utils.HttpMethod
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.getDummyResponse

import retrofit2.Response
import javax.inject.Inject

abstract class BaseDataSource constructor(
    private val context: Context
) {

    protected suspend fun <T>
            getResult( call : suspend () -> Response<T>, method: HttpMethod = HttpMethod.GET) : Resource<T> {
        try {
            Log.d("BaseDataSource", "entered try")
            val result  = call()
            val message_: String

            if(result.isSuccessful) {
                Log.d("BaseDataSource", "Response is successful")
                message_= result.message() ?: context.getString(R.string.success_response)
                val body  = result.body()
                if( method == HttpMethod.POST) return Resource.success(message_, body?:getDummyResponse()as T)
                else if(method == HttpMethod.GET && body != null) return  Resource.success(message_,body)
            }

            val adaptedResult: String = when (result.code()) {
                400 -> context.getString(R.string.bad_request)
                401 -> context.getString(R.string.wrong_credentials)
                403 -> context.getString(R.string.forbidden)
                404 -> context.getString(R.string.not_found)
                500 -> context.getString(R.string.internal_server_error)
                else -> context.getString(R.string.unknown_error)
            }

            Log.d("BaseDataSource", "Response: ${result.code()}, Message: ${adaptedResult}, Body: ${result.body()}")
            return Resource.error(context.getString(R.string.network_error, adaptedResult))


        }catch (e : Exception) {
            Log.d("BaseDataSource", "entered catch , error: ${e.message}")
            return Resource.error(context.getString(R.string.network_call_failed, 
             e.localizedMessage ?: e.toString()))
        }
    }



}