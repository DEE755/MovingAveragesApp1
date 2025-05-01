package il.kod.movingaverageapplication1.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SavedStateViewModel(state : SavedStateHandle) : ViewModel()
{
companion object
{
    private val _selectedStList = MutableLiveData<MutableList<Stock>>(mutableListOf())
}

}