package il.kod.movingaverageapplication1.data.repository

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.models.AdapterStockIdGson
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.ui.viewmodel.SyncManagementViewModel
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Resource
import kod.il.movingaverageapplication1.utils.observeOnce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.max


class SyncManagementRepository @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository,
    val localStockRepository: LocalStocksRepository,
    val sessionManager: SessionManager,
    val localFollowSetRepository: LocalFollowSetRepository
)
{



    //Functionalities for syncing data between local and remote databases, all functions use both local and remote repositories

    lateinit var setUserAnswer: String

    lateinit var setUserFollowsStockAnswer: String

    fun setUserFollowsStockData(stock: Stock, follow: Boolean)
        {
        //local Update:
       CoroutineScope(Dispatchers.IO).launch {
           localStockRepository.setUserFollowsStock(stock, follow)
           //remote Update:
          CSDRepository.setUserFollowsStock(stock.symbol, follow, sessionManager.clientId)
       }
    }

     fun pushFollowSetToRemoteDB(createdFollowSet: FollowSet, lifecycleOwner: LifecycleOwner)
    {
        CoroutineScope(Dispatchers.IO).launch {
            //add followset to local DB
            localFollowSetRepository.addFollowSet(createdFollowSet)
        }
            //add followset to remote DB
             CSDRepository.pushFollowSetToRemoteDB(createdFollowSet).observe(lifecycleOwner)
             { result ->
                 when (result.status) {
                     is Success -> {
                         setUserFollowsStockAnswer = result.status.message


                     }
                     is Error -> {
                         setUserFollowsStockAnswer = "Error adding follow set: ${result.status.message}"
                         Log.e("SyncManager", result.status.message)

                     }
                     is Loading -> {
                         setUserFollowsStockAnswer = "Adding follow set..."

                     }
                 }
                 Log.d("SyncManager", setUserFollowsStockAnswer)

             }

    }


    /*fun pullAndConvertFollowedStocksFromRemote(coroutineContext: CoroutineContext): LiveData<List<Stock>> {

        val idsToConvert : LiveData<Resource<List<Int>>> = CSDRepository.pullUserFollowedStockFromRemoteDB()

        val liveDataToReturn : MutableLiveData<List<Stock>> = MutableLiveData()

        idsToConvert.observeOnce {
            when( it.status){
                is Success -> {
                    Log.d("SyncManager", "Successfully pulled followed stock IDs from remote DB")
                    if (it.status.data.isNullOrEmpty()) {
                        Log.d("SyncManager", "No followed stock IDs found in remote DB")
                    }
                    else CoroutineScope(coroutineContext).launch {
                        val stocks = localStockRepository.getStocksByIdsLive(*(it.status.data)!!.toIntArray())
                        stocks.observeOnce { liveDataToReturn.postValue(it) }
                    }
                }
                is Error -> {
                    Log.e("SyncManager", "Error pulling followed stock IDs: ${it.status.message}")
                }
                is Loading -> {
                    Log.d("SyncManager", "Pulling followed stock IDs from remote DB...")
                }
            }
        }

        return liveDataToReturn
    }*/

    fun pullAndConvertFollowedStocksFromRemote(scope: CoroutineScope) {

        val idsToConvert = CSDRepository.pullUserFollowedStockFromRemoteDB()


        Log.d("SyncManager", "idsToConvert: ${idsToConvert.value?.status?.data?: "null"}")

        idsToConvert.observeForever{
            when( it.status){
                is Success -> {
                    Log.d("SyncManager", "Successfully pulled followed stock IDs from remote DB")
                    if (it.status.data.isNullOrEmpty()) {
                        Log.d("SyncManager", "No followed stock IDs found in remote DB")
                    }
                    else scope.launch(Dispatchers.IO) {
                        Log.d("SyncManager", "Converting followed stock IDs to Stock objects, items: ${it.status.data?: "null"}")
                        // Convert the list of AdapterStockIdGson to a list of Int
                        val intArrayIds = it.status.data?.map{idData -> idData.stock_id}

                        Log.d("SyncManager", "Converted followed stock IDs to Int array: $intArrayIds")

                        val stocks : List<Stock> = localStockRepository.getStocksByIds(*(intArrayIds!!.toIntArray()))

                        Log.d("SyncManager", "Converted followed stock IDs to Stock objects: $stocks")
                        stocks.forEach { localStockRepository.setUserFollowsStock(it, true)
                            Log.d("SyncManager", "Added followed stock: ${it.name} (${it.symbol}) to followed stocks")
                        }
                    }
                }
                is Error -> {
                    Log.e("SyncManager", "Error pulling followed stock IDs: ${it.status.message}")
                }
                is Loading ->
                    {
                    Log.d("SyncManager", "Pulling and adding followed stock IDs from remote DB...")
                }
            }
        }

    }

}