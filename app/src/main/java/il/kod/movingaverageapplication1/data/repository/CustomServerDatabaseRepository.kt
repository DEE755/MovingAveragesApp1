package il.kod.movingaverageapplication1.data.repository

import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.utils.Resource
import kod.il.movingaverageapplication1.utils.performFetchingAndSaving
import kod.il.movingaverageapplication1.utils.performFetchingFromServer
import kod.il.movingaverageapplication1.utils.performPostingToServer
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
            {localDataSource.getPagedStocks()},
            {remoteDataSource.getAllStocks()},
            {localDataSource.saveAllStocks(it)}
        )

    suspend fun nbOfStocksInRemoteDB() = remoteDataSource.nbOfStocksInRemoteDB()


    fun setUserFollowsStock(stock: Stock, follow: Boolean) =
        performPostingToServer { remoteDataSource.userFollowsStock(stock, follow) }


    fun askAI(stock: Stock) = performPostingToServer { remoteDataSource.askAI(stock) }



}





