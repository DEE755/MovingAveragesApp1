package il.kod.movingaverageapplication1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedStocksViewModel : ViewModel() {

    private val _selectedStList = MutableLiveData<MutableList<Stock>>(mutableListOf())
    val selectedStList: LiveData<MutableList<Stock>> get() = _selectedStList

    fun addStock(stock: Stock?) {
        stock?.let {
            _selectedStList.value?.add(it)
            _selectedStList.value = _selectedStList.value // Trigger LiveData update
        }
    }

    fun removeStock(stock: Stock?) {
        stock?.let {
            _selectedStList.value?.remove(it)
            _selectedStList.value = _selectedStList.value // Trigger LiveData update
        }
    }
}