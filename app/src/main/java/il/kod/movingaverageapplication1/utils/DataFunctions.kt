package kod.il.movingaverageapplication1.utils

import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.liveData
import il.kod.movingaverageapplication1.utils.Resource
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error //IMPORTANT BECAUSE IT THINKS IT IS kotlin.error if not !!!

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.dependencyinjectionhilt.MovingAverageApplication1

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionManagerEntryPoint {
    fun getSessionManager(): SessionManager
}

// Lazy initialization of SessionManager using Hilt
val sessionManager: SessionManager by lazy {
    val context = MovingAverageApplication1.instance.applicationContext
    EntryPointAccessors.fromApplication(context, SessionManagerEntryPoint::class.java).getSessionManager()
}


@OptIn(ExperimentalPagingApi::class)
fun <T : Any, A> performFetchingAndSavingPaging(
    localDbFetch: suspend () -> List<T>,
    remoteDbFetch: suspend () -> Resource<A>,
    localDbSave: suspend (A) -> Unit,
    pagingSourceFactory: () -> PagingSource<Int, T>
): LiveData<PagingData<T>> {

    val mediator = object : RemoteMediator<Int, T>() {
        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, T>
        ): MediatorResult {
            return try {
                // Check local first
                val localData = localDbFetch()
                if (localData.isNotEmpty() && loadType == LoadType.REFRESH) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                // Fetch from remote
                val fetchResource = remoteDbFetch()

                when (fetchResource.status) {
                    is Success -> {
                        localDbSave(fetchResource.status.data!!)
                        MediatorResult.Success(endOfPaginationReached = true)
                    }
                    is Error -> MediatorResult.Error(Exception(fetchResource.status.message))
                    else -> MediatorResult.Success(endOfPaginationReached = true)
                }
            } catch (e: Exception) {
                MediatorResult.Error(e)
            }
        }
    }

    return Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = mediator,
        pagingSourceFactory = pagingSourceFactory
    ).liveData
}

fun <T,A> performFetchingAndSaving(localDbFetch: () -> LiveData<T>,
                                    remoteDbFetch: suspend () ->Resource<A>,
                                    localDbSave: suspend (A) -> Unit) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        Log.d("performFetchingAndSaving","called")
        emit(Resource.loading())//tell the live data observer that we are loading

        val source = localDbFetch().map { Resource.success("Success",it) }
        emitSource(source) //set the new source to the live data

        val fetchResource = remoteDbFetch()

        if (fetchResource.status is Success) {
            Log.d("performFetchingAndSaving", "RemoteDbSuccess")
            val data = fetchResource.status.data
            if (data != null) {
                localDbSave(data) // Save the list of FollowSet to the local database
            } else {
                emit(Resource.error("Remote data is null"))
            }

       } else if(fetchResource.status is Error){
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
        }
        if (fetchResource.status is Success) {
        val data = fetchResource.status.data
        if (data != null) {
            emit(Resource.success("Success", data))
        } else {
            emit(Resource.error("Data is null"))
        }
    }
    }



fun <T> performFetchingFromServerEveryTenSeconds(remoteDbFetch: suspend () -> Resource<T>): LiveData<Resource<T>> =
        liveData(Dispatchers.IO) {
            while (coroutineContext.isActive) {
                emit(Resource.loading())
                val fetchResource = remoteDbFetch()
                if (fetchResource.status is Error) {
                    emit(Resource.error(fetchResource.status.message ?: "Unknown error"))
                } else if (fetchResource.status is Success) {
                    val data = fetchResource.status.data
                    if (data != null) {
                        emit(Resource.success("Success", data))
                    } else {
                        emit(Resource.error("Data is null"))
                    }
                } else {
                    emit(Resource.error("Unknown status"))
                }
                delay(10000)
            }
        }






fun <T> performPostingToServer2(remoteDbPost: suspend () ->Resource<T>) : LiveData<Resource<T>> =

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

fun <T> performPostingToServer(remoteDbPost: suspend () ->Resource<T>) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        Log.d("performPostingToServer","called")
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
            //emit(Resource.success("Success", receivedResource.status.data!!))
        }
    }



@OptIn(ExperimentalPagingApi::class)
fun <T : Any, A> performFetchingAndSavingPaging(
    pagingSourceFactory: () -> PagingSource<Int, T>,
    remoteDbFetch: suspend () -> Resource<A>,
    localDbSave: suspend (A) -> Unit,
): LiveData<PagingData<T>> {

    val mediator = object : RemoteMediator<Int, T>() {
        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, T>
        ): MediatorResult {
            return try {
                if (loadType == LoadType.REFRESH) {
                    val fetchResource = remoteDbFetch()
                    if (fetchResource.status is Success) {
                        fetchResource.status.data?.let { localDbSave(it) }

                        sessionManager.preferences.edit { putBoolean("all_stocks_pack_fetch", true) } // Mark that all stocks has been fetched

                    } else if (fetchResource.status is Error) {
                        return MediatorResult.Error(Exception(fetchResource.status.message))
                    }
                }

                MediatorResult.Success(endOfPaginationReached = true)
            } catch (e: Exception) {
                MediatorResult.Error(e)
            }
        }
    }

    return Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = mediator,
        pagingSourceFactory = pagingSourceFactory
    ).liveData

}


    @OptIn(ExperimentalPagingApi::class)
    fun <T : Any> FetchFromLocalPaging(
        pagingSourceFactory: () -> PagingSource<Int, T>
    ): LiveData<PagingData<T>> {

        val mediator = object : RemoteMediator<Int, T>() {
            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, T>
            ): MediatorResult {
                return try {
                    MediatorResult.Success(endOfPaginationReached = true)
                } catch (e: Exception) {
                    MediatorResult.Error(e)
                }
            }
        }

        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = mediator,
            pagingSourceFactory = pagingSourceFactory
        ).liveData

    }


fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    val wrapper = object : Observer<T> {
        override fun onChanged(t: T) {
            removeObserver(this)
            observer.onChanged(t)
        }
    }
    observeForever(wrapper)
}


fun <T> toLiveData(value: T): LiveData<T> {
    val liveData = MutableLiveData<T>()
    liveData.value = value
    return liveData
}


fun <T> setObservingSourceOfMediator(
    newSource: LiveData<T>,
    targetSource: MediatorLiveData<T>,
    toRemove: LiveData<*>? = null
) {
    toRemove?.let { targetSource.removeSource(it) }
    targetSource.addSource(newSource) { value ->
        targetSource.value = value
    }
}