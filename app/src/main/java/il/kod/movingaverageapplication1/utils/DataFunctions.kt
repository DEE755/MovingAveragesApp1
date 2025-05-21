package kod.il.movingaverageapplication1.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error //IMPORTANT BECAUSE IT THINKS IT IS kotlin.error if not !!!

import kotlinx.coroutines.Dispatchers

fun <T,A> performFetchingAndSaving(localDbFetch: () -> LiveData<T>,
                                    remoteDbFetch: suspend () ->Resource<A>,
                                    localDbSave: suspend (A) -> Unit) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        Log.d("performFetchingAndSaving","called")
        emit(Resource.loading())//tell the live data observer that we are loading

        val source = localDbFetch().map { Resource.success("Success",it) }
        emitSource(source) //set the new source to the live data

        val fetchResource = remoteDbFetch()

        if(fetchResource.status is Success){
            Log.d("performFetchingAndSaving","RemoteDbSuccess")
            localDbSave(fetchResource.status.data!!)}

        else if(fetchResource.status is Error){
            emit(Resource.error(fetchResource.status.message))
            emitSource(source)
            Log.d("performFetchingAndSaving","RemoteDbFailure: ${fetchResource.status.message}")
        }
    }


fun <T> performFetchingFromServer(remoteDbFetch: suspend () ->Resource<T>) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        emit(Resource.loading())//tell the lave data observer that we are loading

        val fetchResource = remoteDbFetch()

        if (fetchResource.status is Error) {
            emit(Resource.error(fetchResource.status.message))
        } else {
            emit(Resource.success("Success", fetchResource.status.data!!))
        }
    }


fun <T> performPostingToServer(remoteDbPost: suspend () ->Resource<T>) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        emit(Resource.loading())//tell the lave data observer that we are loading

        val receivedResource = remoteDbPost()

        if (receivedResource.status is Error) {
            emit(Resource.error(receivedResource.status.message))
        } else {
            if (receivedResource.status is Success && "500" in receivedResource.status.message) {
                emit(Resource.error("Server error: ${receivedResource.status.message}"))
            } else {
                emit(Resource.success("Success", receivedResource.status.data!!))
            }
            emit(Resource.success("Success", receivedResource.status.data!!))
        }
    }
