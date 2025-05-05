
package il.kod.movingaverageapplication1.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import il.kod.movingaverageapplication1.data.FollowSet



@Dao
interface FollowSetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFollowSet(followSet: FollowSet)

    @Delete
    fun deleteFollowSet(vararg followSet: FollowSet)

    @Update
    fun updateFollowSet(followSet: FollowSet)

    @Query("SELECT * FROM follow_set ORDER BY name ASC")
    fun getAllFollowSet() : LiveData<List<FollowSet>>


}