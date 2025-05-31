package il.kod.movingaverageapplication1.ui.viewmodel


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository
import il.kod.movingaverageapplication1.utils.Resource
import javax.inject.Inject
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.launch

@HiltViewModel
class CustomServerDatabaseViewModel @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository, private val encryptedPreferences: SharedPreferences
): ViewModel()
{
    var fetchedStockFlag=false

    lateinit var fetchedClientUsername :String

    lateinit var tokensResponse : LiveData<Resource<AuthResponse>>

    lateinit var signupResult : LiveData<Resource<AuthResponse>>


    private var _allStocks: LiveData<Resource<PagingData<Stock>>> = MutableLiveData<Resource<PagingData<Stock>>>(Resource.loading(null))
    val allStocks: LiveData<Resource<PagingData<Stock>>> get() = _allStocks

    lateinit var AI_Answer : LiveData<Resource<String>>

    var nbOfStockInRemoteDB : Int = 0


    private var _percentage = MutableLiveData<Int>()
    val percentage: LiveData<Int> get() = _percentage




    val limit = 100
    var lastSymbol : String = "A"


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
        _allStocks = CSDRepository.getAllStocks()
    }

    fun askAI(stock: Stock)  {
        AI_Answer = CSDRepository.askAI(stock)

    }

    fun getNbOfStocksInRemoteDB() {
        viewModelScope.launch {
            try {
                nbOfStockInRemoteDB= CSDRepository.nbOfStocksInRemoteDB()
                Log.d("CustomServerDatabaseViewModel", "Number of stocks in remote DB: $nbOfStockInRemoteDB")
            } catch (e: Exception) {
                Log.e("CustomServerDatabaseViewModel", "Error fetching number of stocks: ${e.message}")
            }
        }
    }



    fun calculatePercentageFetchStocks(numberOfStocksInLocalDB: Int) {
        if (nbOfStockInRemoteDB==0) return

        _percentage.value = (numberOfStocksInLocalDB * 100) / nbOfStockInRemoteDB
    }





}



