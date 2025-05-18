package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.FollowSet
import il.kod.movingaverageapplication1.data.local_db.FollowSetDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase


class LocalFollowSetRepository(application: Application)
{

    private var followSetDao : FollowSetDao


    init {

        val db = StocksDatabase.getDatabase(application.applicationContext)
        followSetDao = db.followSetDao()

    }


    fun getAllFollowSet(): LiveData<List<FollowSet>> {
        return followSetDao.getAllFollowSet()
    }

    suspend fun addFollowSet(followSet: FollowSet) {
       followSetDao.addFollowSet(followSet)
    }

    suspend fun deleteFollowSet(followSet: FollowSet) {
        followSetDao.deleteFollowSet(followSet)
    }

    suspend fun updateStock(followSet: FollowSet) {
        followSetDao.updateFollowSet(followSet)
    }


}