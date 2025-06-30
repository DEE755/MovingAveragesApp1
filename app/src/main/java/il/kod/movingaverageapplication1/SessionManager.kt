package il.kod.movingaverageapplication1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import il.kod.movingaverageapplication1.data.repository.LocalFollowSetRepository
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(val preferences: SharedPreferences, val localStocksRepository: LocalStocksRepository, val localFollowSetRepository: LocalFollowSetRepository, @ApplicationContext val context: Context) {


    fun getUsername()= preferences.getString("username", null) ?: "user"

    fun setUsername(username: String) {
        preferences.edit { putString("username", username) }
    }

    fun setClientId(id: Int) {
        preferences.edit { putInt("client_id", id) }
    }

    fun getClientId(id : Int = -1): Int {
        return preferences.getInt("client_id", id)
    }





    //flags
    var isNotificationsServiceStarted: Boolean=false

    var fetchedStocksFromRemoteDB : Boolean = false

    var userHasFollowedStocksinRemoteDB : Boolean = false

    var userHasFollowedFollowSetsInRemoteDB : Boolean = false



    //var somethingIsDirty : Boolean = false

      //  fun setSomethingIsDirty(value: Boolean) {
           // somethingIsDirty = value
        //}





    fun saveTokens(
        token: String,
        refresh: String,
        username: String,
        clientId: Int
    ) {//insert the tokens into the encrypted shared preferences
        preferences.edit {
            putString("access_token", token)
                .putString("refresh_token", refresh)
                .putString("username", username)
                .putInt("client_id", clientId)
        }
    }



    fun isFirstTimeLaunch(): Boolean {
        val alreadyLaunched = preferences.getBoolean("has_been_launched", false)
        Log.d("SessionManager", "alreadylaunched: $alreadyLaunched")

        return !alreadyLaunched
        }





    fun allStocksPackHaveBeenFetch(): Boolean {
        val alreadyFetch = preferences.getBoolean("all_stocks_pack_fetch", false)
        return alreadyFetch
    }


    //FOLLOWED STOCKS RETRIEVAL
    fun userFollowedStocksHaveBeenRetrievedOrNone(): Boolean {
        val alreadyFetched = preferences.getBoolean("user_followed_stocks_retrieved", false)
        return alreadyFetched

    }

    fun setUserFollowedStocksHaveBeenRetrievedOrNone(bool: Boolean) {
        preferences.edit { putBoolean("user_followed_stocks_retrieved", bool) }
    }
    //////////////////////////




    //FOLLOWED SETS RETRIEVAL
    fun setUserFollowSetsHaveBeenRetrievedOrNone(bool: Boolean) =
         preferences.edit{putBoolean("user_followedSets_retrieved", bool)}


    fun userFollowSetsHaveBeenRetrievedOrNone(): Boolean =
       preferences.getBoolean("user_followedSets_retrieved", false)




    //AUTO UPDATE FOLLOWED STOCKS DIALOG

    fun restoredFollowedStocksDialogHasBeenShown(): Boolean {
        val alreadyShown = preferences.getBoolean("user_followed_stocks_retrieved_dialog_shown", false)

    //if the dialog has been shown val is set to true, and if the app hasn't been launched the boolean doesn't exist and so val is set to false
        return if (alreadyShown) {
            true
        } else {
            preferences.edit { putBoolean("user_followed_stocks_retrieved_dialog_shown", true) }
            false
        }
    }

    fun notificationHasBeenExplained(): Boolean {
        val alreadyShown = preferences.getBoolean("notification_explained", false)

        //if the dialog has been shown val is set to true, and if the app hasn't been launched the boolean doesn't exist and so val is set to false
        return if (alreadyShown) {
            true
        } else {
            preferences.edit { putBoolean("notification_explained", true) }
            false
        }
    }


    fun tutorialFollowSetHasBeenShown(): Boolean {
        val alreadyShown = preferences.getBoolean("tutorial_followset_shown", false)


        return if (alreadyShown) {
            true
        } else {
            preferences.edit { putBoolean("tutorial_followset_shown", true) }
            false
        }
    }






    fun logOutClient() {
        //remove all user related data but keep application and device related data (like stocks pack and tutorials)
        setUserFollowSetsHaveBeenRetrievedOrNone(false)
        localFollowSetRepository.deleteAllFollowSets()
        localStocksRepository.resetSelectedStocks()
        clearAppCache(context)

        preferences.edit {
            remove("access_token")
                .remove("refresh_token")
                .remove("client_id")
                .remove("username")
                .remove("user_followed_stocks_retrieved")
                .remove("user_followedSets_retrieved")
                .remove("user_followed_stocks_retrieved_dialog_shown")
        }

    }



    suspend fun isCacheEmpty(): Boolean {


       return localStocksRepository.getAvailableStockCount()<0





    }

    fun setUserhasFollowedFollowSetsInRemoteDB() {

        userHasFollowedFollowSetsInRemoteDB = true
    }



    fun clearAppCache(context: Context) {
        try {
            context.cacheDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startNotificationsServiceifGranted() {
        isNotificationsServiceStarted = true
    }

    fun NotificationsServiceIsGranted() =
        preferences.getBoolean("notifications_service_granted", false)

    fun setNotificationsServiceGranted(bool: Boolean) =
        preferences.edit { putBoolean("notifications_service_granted", bool) }


}