package il.kod.movingaverageapplication1.data.repository

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
    private val remoteDataSource: CustomServerDatabaseRemoteDataSource
    // ,private val localDataSource: CustomServerDatabaseLocalDataSource
){
    fun login(
        username: String,
        password: String
    ) = performFetchingFromServer {
        remoteDataSource.login(username, password) }

    fun signUp(
        username: String,
        password: String
    ) = performPostingToServer {
        remoteDataSource.signUp(username, password) }
}




