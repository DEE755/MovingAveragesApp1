package kod.il.movingaverageapplication1.utils

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

        emit(Resource.loading())//tell the lave data observer that we are loading

        val source = localDbFetch().map { Resource.success(it) }
        emitSource(source) //set the new source to the live data

        val fetchResource = remoteDbFetch()

        if(fetchResource.status is Success)
            localDbSave(fetchResource.status.data!!)

        else if(fetchResource.status is Error){
            emit(Resource.error(fetchResource.status.message))
            emitSource(source)
        }
    }


fun <T> performFetchingFromServer(remoteDbFetch: suspend () ->Resource<T>) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        emit(Resource.loading())//tell the lave data observer that we are loading

        val fetchResource = remoteDbFetch()

        if (fetchResource.status is Error) {
            emit(Resource.error(fetchResource.status.message))
        } else {
            emit(Resource.success(fetchResource.status.data!!))
        }
    }