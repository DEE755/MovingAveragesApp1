package il.kod.movingaverageapplication1.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository
import il.kod.movingaverageapplication1.utils.Resource
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class CustomServerDatabaseViewModel @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository, private val encryptedPreferences: SharedPreferences
): ViewModel()
{
    lateinit var fetchedClientUsername :String

    lateinit var tokensResponse : LiveData<Resource<AuthResponse>>

    lateinit var signupResult : LiveData<Resource<AuthResponse>>

    lateinit var http_response : LiveData<Resource<String>>

    lateinit var allStocks : LiveData<Resource<List<Stock>>>

    lateinit var AI_Answer : LiveData<Resource<String>>




    fun saveTokens(token: String, refresh: String) {
        encryptedPreferences.edit {
            putString("access_token", token)
                .putString("refresh_token", refresh)
        }
    }

    fun getAccessTokenFromPreferences(): String? {
        return encryptedPreferences.getString("access_token", null)
    }





    fun updateCredentialsFromServer(userLogin: String, password: String) {
        fetchedClientUsername = userLogin
        tokensResponse = CSDRepository.login( userLogin, password)

    }

    fun signUpNewUserToServer(newEntrySignupName: String, chosenPassword: String)  {
        //fetchedClientUsername = username
        signupResult = CSDRepository.signUp(newEntrySignupName,chosenPassword)
    }



    fun getAllStocks() {
        allStocks = CSDRepository.getAllStocks()
    }

    fun askAI(stock: Stock)  {
        AI_Answer = CSDRepository.askAI(stock)

    }



}