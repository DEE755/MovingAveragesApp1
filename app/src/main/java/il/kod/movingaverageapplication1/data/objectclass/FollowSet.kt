package il.kod.movingaverageapplication1.data.objectclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "follow_set")
data class FollowSet(
    @ColumnInfo(name="name")
    var name: String,
    @ColumnInfo(name="imageUri")
    internal var imageUri: String? =null,
    @ColumnInfo(name="user_comments")
    val userComments: String="",
    @ColumnInfo(name="set_ids")
    val set_ids : List<Int> //not mutable for parcelization purposes)

): Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id : Int =0
    // Update the list (not mutable for parcelization purposes)
    fun updateSet(newSet: List<Int>): FollowSet {
        return this.copy(set_ids = newSet)

    }

    fun extractStocksToIntArray(): IntArray =this.set_ids.toIntArray()
    fun size(): Int = this.set_ids.size
}