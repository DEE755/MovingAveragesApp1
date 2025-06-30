package il.kod.movingaverageapplication1.data.repository

import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.models.AdapterBackIDForGson
import il.kod.movingaverageapplication1.data.models.AdapterStockIdGson
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.utils.Loading
import il.kod.movingaverageapplication1.utils.Success
import il.kod.movingaverageapplication1.utils.Error
import il.kod.movingaverageapplication1.utils.Resource
import kod.il.movingaverageapplication1.utils.performPostingToRemoteAndSavingToLocalDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SyncManagementRepository @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository,
    val localStockRepository: LocalStocksRepository,
    val sessionManager: SessionManager,
    val localFollowSetRepository: LocalFollowSetRepository
) {


    //Functionalities for syncing data between local and remote databases, all functions use both local and remote repositories
    lateinit var setUserAnswer: String

    lateinit var responseIdInsert: LiveData<Resource<AdapterStockIdGson>>

    fun setUserFollowsStockData(stock: Stock, follow: Boolean) {
        //local Update:
        CoroutineScope(Dispatchers.IO).launch {
            localStockRepository.setUserFollowsStock(stock, follow)
            //remote Update:
            CSDRepository.setUserFollowsStock(stock.symbol, follow)
        }
    }

    fun setUserUnfollowsFollowSet(followSet: FollowSet) {
        CoroutineScope(Dispatchers.IO).launch {
            //LOCAL DELETE
            localFollowSetRepository.deleteFollowSet(followSet)
            //REMOTE UNFOLLOW
            CSDRepository.setUserUnfollowsFollowSet(followSet)
        }
    }

    fun pushFollowSetToRemoteDB(createdFollowSet: FollowSet) : LiveData<Resource<AdapterBackIDForGson>> =


        performPostingToRemoteAndSavingToLocalDB(createdFollowSet,
            //add followset to local DB
            {localFollowSetRepository.addFollowSet(createdFollowSet)},

            //add followset to remote DB
            {CSDRepository.pushFollowSetToRemoteDB(createdFollowSet)}
        )




    fun pullUserFollowSetsFromToRemoteDB(): LiveData<Resource<List<FollowSet>>> =

        CSDRepository.pullUserFollowSetsFromToRemoteDB()


    fun pullAndConvertFollowedStocksFromRemote(scope: CoroutineScope) {

        val idsToConvert = CSDRepository.pullUserFollowedStockFromRemoteDB()


        Log.d("SyncManager", "idsToConvert: ${idsToConvert.value?.status?.data ?: "null"}")

        idsToConvert.observeForever {
            when (it.status) {
                is Success -> {
                    Log.d("SyncManager", "Successfully pulled followed stock IDs from remote DB")
                    if (it.status.data.isNullOrEmpty()) {
                        Log.d("SyncManager", "No followed stock IDs found in remote DB")

                        sessionManager.preferences.edit {
                            putBoolean(
                                "user_followed_stocks_retrieved",
                                true
                            )
                        } // no stocks to retrieve, so we set the flag to true

                    } else scope.launch(Dispatchers.IO) {
                        sessionManager.userHasFollowedStocksinRemoteDB = true
                        Log.d(
                            "SyncManager",
                            "Converting followed stock IDs to Stock objects, items: ${it.status.data ?: "null"}"
                        )
                        // Convert the list of AdapterStockIdGson to a list of Int
                        val intArrayIds = it.status.data?.map { idData -> idData.stock_id }

                        Log.d(
                            "SyncManager",
                            "Converted followed stock IDs to Int array: $intArrayIds"
                        )

                        val stocks: List<Stock> =
                            localStockRepository.getStocksByIds(*(intArrayIds!!.toIntArray()))

                        Log.d(
                            "SyncManager",
                            "Converted followed stock IDs to Stock objects: $stocks"
                        )
                        stocks.forEach {
                            localStockRepository.setUserFollowsStock(it, true)
                            Log.d(
                                "SyncManager",
                                "Added followed stock: ${it.name} (${it.symbol}) to followed stocks"
                            )
                        }
                    }
                    sessionManager.preferences.edit {
                        putBoolean(
                            "user_followed_stocks_retrieved",
                            true
                        ) // finished retrieving stocks, so we set the flag to true
                    }
                }

                is Error -> {
                    Log.e("SyncManager", "Error pulling followed stock IDs: ${it.status.message}")
                }

                is Loading -> {
                    Log.d("SyncManager", "Pulling and adding followed stock IDs from remote DB...")
                }
            }
        }

    }
}

