package il.kod.movingaverageapplication1.data.repository

import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.utils.Resource
import kod.il.movingaverageapplication1.utils.performFetchingAndSaving
import kod.il.movingaverageapplication1.utils.performFetchingFromServer
import kod.il.movingaverageapplication1.utils.performFetchingFromServerEveryTenSeconds
import kod.il.movingaverageapplication1.utils.performPostingToServer
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

//we can use that to call both the local and remote data sources in same functions
//for now it seems it's needed only for the remote data source
//HIGERLEVEL OF FUNCTIONALITY

@Singleton
class CustomServerDatabaseRepository @Inject constructor(
    private val remoteDataSource: CustomServerDatabaseRemoteDataSource,
    private val localDataSource: LocalStocksRepository,
){
    fun login(
        username: String,
        password: String
    ) = performFetchingFromServer {
        remoteDataSource.login(username, password) }

    fun signUp(
        username: String,
        password: String
    ): LiveData<Resource<AuthResponse>> = performPostingToServer {
         remoteDataSource.signUp(username, password) }


    fun getAllStocks() =
        performFetchingAndSaving (
            {localDataSource.getAllStocks()},
            {remoteDataSource.getAllStocks()},
            {localDataSource.saveAllStocks(it)}
        )

   /* suspend fun getStocksStartingFromSymbol(symbol: String, scope: CoroutineScope) =
        performFetchingAndSaving (
            {localDataSource.getAllStocks(scope)},
            {remoteDataSource.getStocksStartingFromSymbol(symbol)},
            {localDataSource.saveAllStocks(it)}
        )*/

    suspend fun getNbOfStocksInRemoteDB() = remoteDataSource.getNbOfStocksInRemoteDB()

         fun getFollowedStockPrice()=
             performFetchingFromServerEveryTenSeconds {
                 remoteDataSource.getFollowedStockPrice()
             }

fun getFollowedMovingAverages() =
    performFetchingFromServer {remoteDataSource.getFollowedMovingAverages()}

    suspend fun setUserFollowsStock(stockSymbol: String, follow: Boolean, clientId: Int) =
         remoteDataSource.userFollowsStock(stockSymbol, follow, clientId)


    fun askAI(stock: Stock) = performPostingToServer { remoteDataSource.askAI(stock) }



}





