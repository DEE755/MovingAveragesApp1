package il.kod.movingaverageapplication1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.data.repository.LocalStocksRepository
import il.kod.movingaverageapplication1.data.repository.SyncManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AllStocksViewModel @Inject constructor(
    private val localRepository: LocalStocksRepository,
    private val syncManagementRepository: SyncManagementRepository
) : ViewModel() {



    val unselectedStock : LiveData<List<Stock>> = localRepository.getUnselectedStocks()
    val followedStocks : LiveData<List<Stock>> = localRepository.getSelectedStocks()
    val filteredStocksList: MutableLiveData<List<Stock>> = MutableLiveData()
    var availableStockCount: LiveData<Int> = MutableLiveData()
    var searchStockCount: Int = 0



    lateinit var pagedStocks: LiveData<PagingData<Stock>>

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
            searchStockCount= filteredStocksList.value?.size ?: 0
        }

    }

    fun getAvailableStockCount()= viewModelScope.launch {
        withContext(Dispatchers.IO) {
            availableStockCount=localRepository.getAvailableStockCount()
        }
    }

    fun getLastSymbol(): String? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                localRepository.getLastSymbol()
            }
        }
    }

    fun getSelectedStocks(): LiveData<List<Stock>> {
        return localRepository.getSelectedStocks()
    }

    fun getAllStocks() {
        pagedStocks=localRepository.getPagedStocks().cachedIn(viewModelScope)
    }


}