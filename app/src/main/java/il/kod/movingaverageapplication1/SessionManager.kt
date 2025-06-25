package il.kod.movingaverageapplication1

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(val preferences: SharedPreferences, val localStocksRepository: LocalStocksRepository) {




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
                .putString("client_username", username)
                .putInt("client_id", clientId)
        }
    }



    fun isFirstTimeLaunch(): Boolean {
        val alreadyLaunched = preferences.getBoolean("has_been_launched", false)
        Log.d("SessionManager", "alreadylaunched: $alreadyLaunched")

        return !alreadyLaunched
        }


    fun isFirstTimeEntryInFollowset(): Boolean {
        val alreadyLaunched = preferences.getBoolean("follow_set_has_been_launch", false)
        Log.d("SessionManager", "alreadylaunched: $alreadyLaunched")

        return !alreadyLaunched
    }

    fun setFollowsetFragmentHasBeenLaunched() {
        preferences.edit { putBoolean("follow_set_has_been_launch", true) }
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

    fun setUserFollowedStocksHaveBeenRetrievedOrNone() {
        preferences.edit { putBoolean("user_followed_stocks_retrieved", true) }
    }
    //////////////////////////


    fun userFollowSetsHaveBeenRetrievedOrNone(): Boolean {
        val alreadyFetched = preferences.getBoolean("user_followedSets_retrieved", false)
        return alreadyFetched

    }


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
        preferences.edit { clear() }

}



    suspend fun isCacheEmpty(): Boolean {


       return localStocksRepository.getAvailableStockCount()<0





    }

    fun setUserhasFollowedFollowSetsInRemoteDB(bool: Boolean) {

        userHasFollowedFollowSetsInRemoteDB = bool


    }


}