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
import androidx.core.content.edit
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import il.kod.movingaverageapplication1.SessionManager
import il.kod.movingaverageapplication1.data.models.AdapterStockIdGson
import il.kod.movingaverageapplication1.utils.Constants

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


@HiltViewModel
class CustomServerDatabaseViewModel @Inject constructor(
    val CSDRepository: CustomServerDatabaseRepository,
    private val sessionManager: SessionManager,
): ViewModel() {
    var fetchedStockFlag = false

    var fetchedClientId = -1

    lateinit var tokensResponse: LiveData<Resource<AuthResponse>>

    var updatedStockPrice: LiveData<Resource<List<Stock>>> =
        MutableLiveData<Resource<List<Stock>>>(Resource.loading(null))

    lateinit var signupResult: LiveData<Resource<AuthResponse>>


    lateinit var newallStocks : LiveData<PagingData<Stock>>


    private var _allStocks: LiveData<PagingData<Stock>> =
        MutableLiveData<PagingData<Stock>>().apply {
            value = PagingData.empty<Stock>()
        }

    //lateinit var allStocks: LiveData<PagingData<Stock>>

    private val _cachedStocks = MediatorLiveData<Resource<PagingData<Stock>>>()
    val cachedStocks: LiveData<Resource<PagingData<Stock>>> get() = _cachedStocks

    /*init {
        _cachedStocks.addSource(_allStocks) { _cachedStocks.value = it }
    }*/


    var AI_Answer: LiveData<Resource<String>> =
        MutableLiveData<Resource<String>>(Resource.loading(null))

    var nbOfStockInRemoteDB: Int = 0
    var fetchedStocksCount: Int = 0

    internal var percentoge: Int = 0


    var lastSymbol: String = "A"





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
     newallStocks=   CSDRepository.getAllStocks()
    }

    /* suspend fun getStocksStartingFromSymbol(symbol: String) {
        Log.d("CustomServerDatabaseViewModel", "Fetching stocks starting from symbol: $symbol")
        _allStocks=CSDRepository.getStocksStartingFromSymbol(symbol, viewModelScope)
    }*/

    fun askAI(stock: Stock, question: String) {
        AI_Answer = CSDRepository.askAI(stock, question)

    }

    fun askAIFollowSet(vararg allStocksName: String, question: String) {
        AI_Answer = CSDRepository.askAIFollowSet(*allStocksName, question = question)

    }

    /*fun testUserFollowsStock(stockSymbol: String, follow: Boolean) {


    CSDRepository.setUserFollowsStock(stockSymbol, follow)


    }*/


    fun getNbOfStocksInRemoteDB() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                nbOfStockInRemoteDB = CSDRepository.getNbOfStocksInRemoteDB()
                Log.d(
                    "CustomServerDatabaseViewModel",
                    "Number of stocks in remote DB: $nbOfStockInRemoteDB"
                )
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

            percentoge = (numberOfStocksInLocalDB * 100) / nbOfStockInRemoteDB
        }

    }


    fun getFollowedStockPrice()
    {
        this.updatedStockPrice = CSDRepository.getFollowedStockPrice()

//function to be observed by the UI to get the followed stock prices
    }

    fun getFollowedMovingAverages() =
        CSDRepository.getFollowedMovingAverages()










}





