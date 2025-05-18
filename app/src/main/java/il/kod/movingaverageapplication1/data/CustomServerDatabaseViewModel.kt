package il.kod.movingaverageapplication1.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import il.kod.movingaverageapplication1.data.models.UserProfileTransitFromGson
import il.kod.movingaverageapplication1.data.repository.CustomServerDatabaseRepository
import il.kod.movingaverageapplication1.utils.Resource
import javax.inject.Inject

@HiltViewModel
class CustomServerDatabaseViewModel @Inject constructor(
    val customServerDatabaseRepository: CustomServerDatabaseRepository
):ViewModel()
{
    var client_username :String=""


    var client_password :String=""


    lateinit var credentials : LiveData<Resource<List<UserProfileTransitFromGson>>>

    fun updateCredentials(username: String, password: String) {
        client_username = username
        client_password = password
        credentials = customServerDatabaseRepository.login( client_username, client_password)
    }



}