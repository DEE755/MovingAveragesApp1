package il.kod.movingaverageapplication1.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import il.kod.movingaverageapplication1.data.FollowSet
import il.kod.movingaverageapplication1.data.Stock
import il.kod.movingaverageapplication1.data.local_db.FollowSetDao
import il.kod.movingaverageapplication1.data.local_db.StockDao
import il.kod.movingaverageapplication1.data.local_db.StocksDatabase

class FollowSetRepository(application: Application) {

    private var followSetDao : FollowSetDao


    init {

        val db = StocksDatabase.getDatabase(application.applicationContext)
        followSetDao = db.followSetDao()

    }


   fun getAllFollowSet(): LiveData<List<FollowSet>> {
        return followSetDao.getAllFollowSet()
    }

    fun addStock(followSet: FollowSet) {
        followSetDao.addFollowSet(followSet)
    }

    fun removeStock(followSet: FollowSet) {
        followSetDao.deleteFollowSet(followSet)
    }

    fun updateStock(followSet: FollowSet) {
        followSetDao.updateFollowSet(followSet)
    }


}