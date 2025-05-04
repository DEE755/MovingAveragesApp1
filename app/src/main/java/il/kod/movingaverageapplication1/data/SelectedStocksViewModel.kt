package il.kod.movingaverageapplication1.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.repository.StocksRepository

class SelectedStocksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository=StocksRepository(application)
    val _selectedStList=repository._selectedStList
    val selectedStList : LiveData<List<Stock>> = repository.getSelectedStocks()

    fun followStock(stock: Stock) {
        stock.let{stock?.isSelected=true
            repository.updateStock(stock)}
    }

    fun unfollowStock(stock: Stock) {
        stock.let{stock?.isSelected=false
            repository.updateStock(stock)}
    }

    fun onItemClicked(index: Int): Stock? {
        return selectedStList.value?.get(index)
    }


}