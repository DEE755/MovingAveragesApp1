package il.kod.movingaverageapplication1.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Constructor
import javax.inject.Inject

@HiltViewModel
class AllStocksViewModel @Inject constructor(
    private val localRepository: LocalStocksRepository,
    private val syncManagementRepository: SyncManagementRepository
) : ViewModel() {



    val unselectedStock : LiveData<List<Stock>> = localRepository.getUnselectedStocks()
    val followedStocks : LiveData<List<Stock>> = localRepository.getSelectedStocks()
    val filteredStocksList: MutableLiveData<List<Stock>> = MutableLiveData()

        //for the future API, for now the initial stock data is hardcoded into the database
    fun addStock(stock: Stock){
            viewModelScope.launch {  localRepository.addStock(stock)}}
    fun removeStock(stock: Stock) {viewModelScope.launch{localRepository.removeStock(stock)}}


    fun followStock(stock: Stock, follow: Boolean) =
        viewModelScope.launch{syncManagementRepository.setUserFollowsStock(stock, follow) }



    fun onItemClicked(index: Int): Stock? {
        return followedStocks.value?.get(index)
    }

    fun getStocksAt(vararg index: Int): List<Stock> {
        val stocks = mutableListOf<Stock>()
        index.forEach{
            followedStocks.value?.get(it)?.let {
                stocks.add(it)
            }
        }
        return stocks
    }

    suspend fun getStocksByIds(vararg ids: Int): List<Stock> {
        return withContext(Dispatchers.IO) { localRepository.getStocksByIds(*ids) }
    }


    fun filterStocksByName(name_part: String?) {

        viewModelScope.launch {
            filteredStocksList.value = localRepository.filterStocksByName(name_part?:"")
        }

    }

    fun getSelectedStocks(): LiveData<List<Stock>> {
        return localRepository.getSelectedStocks()
    }

}