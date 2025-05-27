package il.kod.movingaverageapplication1

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository

class DetailStockViewModel : ViewModel() {


    private val _clickedStock=MutableLiveData<Stock>()
    val clickedStock get()=_clickedStock

    fun setStock(stock: Stock){
        _clickedStock.value=stock
        Log.d("DetailStockViewModel", "setStock: ${_clickedStock.value}")
    }




}