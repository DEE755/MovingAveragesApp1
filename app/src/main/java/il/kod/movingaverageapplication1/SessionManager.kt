package il.kod.movingaverageapplication1

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(val preferences: SharedPreferences, val localStocksRepository: LocalStocksRepository) {

    val refreshToken= preferences.getString("refresh_token", null)
    val accessToken = preferences.getString("access_token", null)
    val username = preferences.getString("username", null)
    val clientId = preferences.getInt("client_id", -1)



    var isNotificationsServiceStarted: Boolean=false

    var fetchedStocksFromRemoteDB : Boolean = false

    fun isFirstTimeLaunch(): Boolean {
        val alreadyLaunched = preferences.getBoolean("has_been_launched", false)
        Log.d("SessionManager", "alreadylaunched: $alreadyLaunched")
//if the hase been launched val is set to true, and if the app hasn't been launched the boolean doesn't exist and so val is set to false
        return !alreadyLaunched
        }





    fun logOutClient() {
        preferences.edit { clear() }

}



    suspend fun isCacheEmpty(): Boolean {


       return localStocksRepository.getAvailableStockCount()<0





    }


}