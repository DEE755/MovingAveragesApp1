package il.kod.movingaverageapplication1.ui.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.models.AuthResponse
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository
import il.kod.movingaverageapplication1.utils.Resource
import javax.inject.Inject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.utils.Constants

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltViewModel
class CustomServerDatabaseViewModel @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository,
    private val sessionManager: SessionManager,
): ViewModel() {

    lateinit var tokensResponse: LiveData<Resource<AuthResponse>>

    var updatedStockPrice: LiveData<Resource<List<Stock>>> =
        MutableLiveData<Resource<List<Stock>>>(Resource.loading(null))

    lateinit var signupResult: LiveData<Resource<AuthResponse>>


    lateinit var allStocks : LiveData<PagingData<Stock>>


    private val _cachedStocks = MediatorLiveData<Resource<PagingData<Stock>>>()
    val cachedStocks: LiveData<Resource<PagingData<Stock>>> get() = _cachedStocks




    var AI_Answer: LiveData<Resource<String>> =
        MutableLiveData<Resource<String>>(Resource.loading(null))

    var nbOfStockInRemoteDB: Int = 0
    var fetchedStocksCount: Int = 0

    internal var percentage: Int = 0




    fun getAccessTokenFromPreferences(): String? {
        return sessionManager.preferences.getString("access_token", null)
    }


    fun updateCredentialsFromServer(userLogin: String, password: String) {
        sessionManager.setUsername(userLogin)
        tokensResponse = CSDRepository.login(userLogin, password)

    }

    fun signUpNewUserToServer(newEntrySignupName: String, chosenPassword: String) {
        //fetchedClientUsername = username
        signupResult = CSDRepository.signUp(newEntrySignupName, chosenPassword)
    }


    fun getAllStocks() {
        Log.d("CustomServerDatabaseViewModel", "Fetching all stocks")
     allStocks=   CSDRepository.getAllStocks()

    }


    fun askAI(stock: Stock, question: String) {
        AI_Answer = CSDRepository.askAI(stock, question)

    }

    fun askAIFollowSet(vararg allStocksName: String, question: String) {
        AI_Answer = CSDRepository.askAIFollowSet(*allStocksName, question = question)

    }



    fun getNbOfStocksInRemoteDB() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                nbOfStockInRemoteDB = CSDRepository.getNbOfStocksInRemoteDB()
            } catch (e: Exception) {
                Log.e(
                    "CustomServerDatabaseViewModel",
                    "Error fetching number of stocks: ${e.message}"
                )
            }
        }
    }


    fun calculatePercentageFetchStocks(numberOfStocksInLocalDB: Int, override: Boolean) {
        if (nbOfStockInRemoteDB == 0) return
        if (override) {
            nbOfStockInRemoteDB =
                Constants.DATABASE_LIMIT // if we chosed not to use the whole database, we override the value by this value}

            percentage = (numberOfStocksInLocalDB * 100) / nbOfStockInRemoteDB
        }

    }


    fun getFollowedStockPrice()
    {
        this.updatedStockPrice = CSDRepository.getFollowedStockPrice()

    }

    fun getFollowedMovingAverages() =
        CSDRepository.getFollowedMovingAverages()







}





