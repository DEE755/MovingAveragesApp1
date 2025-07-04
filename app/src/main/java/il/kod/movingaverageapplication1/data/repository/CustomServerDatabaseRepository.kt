package il.kod.movingaverageapplication1.data.repository

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.models.AdapterStockIdGson
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.utils.Resource
import kod.il.movingaverageapplication1.utils.FetchFromLocalPaging
import kod.il.movingaverageapplication1.utils.performFetchingAndSaving
import kod.il.movingaverageapplication1.utils.performFetchingAndSavingPaging
import kod.il.movingaverageapplication1.utils.performFetchingFromServer
import kod.il.movingaverageapplication1.utils.performFetchingFromServerEveryTenSeconds
import kod.il.movingaverageapplication1.utils.performPostingToServer
import javax.inject.Inject
import javax.inject.Singleton

import il.kod.movingaverageapplication1.utils.lockOrientation

//we can use that to call both the local and remote data sources in same functions
//for now it seems it's needed only for the remote data source
//HIGERLEVEL OF FUNCTIONALITY

@Singleton
class CustomServerDatabaseRepository @Inject constructor(
    private val remoteDataSource: CustomServerDatabaseRemoteDataSource,
    private val localDataSource: LocalStocksRepository, //consider to move to sync management with all its functions
    private val localFollowSetRepository: LocalFollowSetRepository,
    private val sessionManager: SessionManager

) {

    fun login(
        username: String,
        password: String
    ) = performFetchingFromServer {
        remoteDataSource.login(username, password)
    }

    fun signUp(
        username: String,
        password: String
    ): LiveData<Resource<AuthResponse>> = performPostingToServer {
        remoteDataSource.signUp(username, password)
    }


    fun getAllStocks(): LiveData<PagingData<Stock>> =
        if (!sessionManager.allStocksPackHaveBeenFetch()) {

            performFetchingAndSavingPaging(
                { localDataSource.getAllStocks() }, // return LiveData<PagingData<Stock>>
                { remoteDataSource.getAllStocks() },
                { allStocks -> localDataSource.saveAllStocks(allStocks)  }
            )
        } else {
            FetchFromLocalPaging { localDataSource.getAllStocks() }
        }


    /* suspend fun getStocksStartingFromSymbol(symbol: String, scope: CoroutineScope) =
        performFetchingAndSaving (
            {localDataSource.getAllStocks(scope)},
            {remoteDataSource.getStocksStartingFromSymbol(symbol)},
            {localDataSource.saveAllStocks(it)}
        )*/

    suspend fun getNbOfStocksInRemoteDB() = remoteDataSource.getNbOfStocksInRemoteDB()

    fun getFollowedStockPrice() =
        performFetchingFromServerEveryTenSeconds {
            remoteDataSource.getFollowedStockPrice()
        }

    fun getFollowedMovingAverages() : LiveData<Resource<List<Stock>>> =
        performFetchingFromServer { remoteDataSource.getFollowedMovingAverages() }

    suspend fun setUserFollowsStock(stockSymbol: String, follow: Boolean) =
        remoteDataSource.userFollowsStock(stockSymbol, follow)


    suspend fun setUserUnfollowsFollowSet(followSet: FollowSet) =
        remoteDataSource.setUserUnfollowsFollowSet(followSet)

    fun askAI(stock: Stock, question: String) =
        performPostingToServer { remoteDataSource.askAI(stock, question) }


    fun askAIFollowSet(vararg allStocksName: String, question: String) =
        performPostingToServer {
            remoteDataSource.askAIFollowSet(
                *allStocksName,
                question = question
            )
        }


    suspend fun pushFollowSetToRemoteDB(createdFollowSet: FollowSet)   =
        remoteDataSource.pushFollowSetToRemoteDB(createdFollowSet)


    fun pullUserFollowSetsFromToRemoteDB() : LiveData<Resource<List<FollowSet>>> =
        performFetchingAndSaving (
            localDbFetch={ localFollowSetRepository.getAllUserFollowSet()},
            remoteDbFetch = {remoteDataSource.pullUserFollowSetsFromToRemoteDB()},
           localDbSave =  {followSetList->localFollowSetRepository.saveAllFollowSets(followSetList)})



    fun pullUserFollowedStockFromRemoteDB(): LiveData<Resource<List<AdapterStockIdGson>>> =
        performFetchingFromServer {
            remoteDataSource.pullUserFollowedStockFromRemoteDB()
        }


}
