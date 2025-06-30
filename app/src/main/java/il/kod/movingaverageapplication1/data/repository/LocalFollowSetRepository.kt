package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.objectclass.FollowSet
import il.kod.movingaverageapplication1.data.local_db.FollowSetDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase
import il.kod.movingaverageapplication1.data.objectclass.Stock
import il.kod.movingaverageapplication1.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LocalFollowSetRepository(application: Application)
{

    private var followSetDao : FollowSetDao


    init {

        val db = StocksDatabase.getDatabase(application.applicationContext)
        followSetDao = db.followSetDao()

    }


    fun getAllUserFollowSet(): LiveData<List<FollowSet>> {
        return followSetDao.getAllUserFollowSet()
    }
    suspend fun getFollowSetWithNotifications() = followSetDao.getFollowSetWithNotifications()

    suspend fun addFollowSet(followSet: FollowSet) {
       followSetDao.addFollowSet(followSet)
    }

    suspend fun deleteFollowSet(followSet: FollowSet) {
        followSetDao.deleteFollowSet(followSet)
    }

    suspend fun deleteFollowSetById(front_id: Int) {
        followSetDao.deleteFollowSetById(front_id)
    }

    suspend fun updateFollowSet(followSet: FollowSet) {
        followSetDao.updateFollowSet(followSet)
    }


    suspend fun saveAllFollowSets(allFollowSets: List<FollowSet>) =
        allFollowSets.forEach {followSet-> followSetDao.addFollowSet(followSet) }


     fun deleteAllFollowSets()=
    CoroutineScope(Dispatchers.IO).launch {followSetDao.deleteAllFollowSets()}

}