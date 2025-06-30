
package il.kod.movingaverageapplication1.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import il.kod.movingaverageapplication1.data.objectclass.FollowSet



@Dao
interface FollowSetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFollowSet(followSet: FollowSet)

    @Delete
    suspend fun deleteFollowSet(vararg followSet: FollowSet)

    @Query("DELETE FROM follow_set WHERE followset_id = :front_id")
    suspend fun deleteFollowSetById(front_id: Int)

    @Update
    suspend fun updateFollowSet(followSet: FollowSet)

    @Query("SELECT * FROM follow_set ORDER BY name ASC")
    fun getAllUserFollowSet() : LiveData<List<FollowSet>>


    @Query("SELECT * FROM follow_set WHERE notifications_prices IS NOT NULL AND notifications_prices > -1")
    suspend fun getFollowSetWithNotifications(): List<FollowSet>//NOT LIVE DATA BECAUSE MEANT TO USE IN A SERVICE

    @Query("DELETE FROM follow_set")
    suspend fun deleteAllFollowSets()

}