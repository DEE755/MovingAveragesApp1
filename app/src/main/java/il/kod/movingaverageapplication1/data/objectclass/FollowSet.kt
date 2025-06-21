package il.kod.movingaverageapplication1.data.objectclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import il.kod.movingaverageapplication1.NotificationService
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "follow_set")
data class FollowSet(
    @ColumnInfo(name="name") @Expose
    var name: String,
    @ColumnInfo(name="imageUri") @Expose
    internal var imageUri: String? =null,
    @ColumnInfo(name="user_comments") @Expose
    val userComments: String="",
    @ColumnInfo(name="set_ids") @Expose(serialize = false, deserialize = false)
    val set_ids : List<Int>, //(not mutable for parcelization purposes --> handled via type converter)

    @ColumnInfo(name="notifications_prices") @Expose
    var notificationsPriceThreeshold : Double,

    @ColumnInfo(name="isDirty")@Expose(serialize = false, deserialize = false)
    var isDirty: Boolean = false

): Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id : Int =0
    // Update the list (not mutable for parcelization purposes)
    fun updateSet(newSet: List<Int>): FollowSet {
        return this.copy(set_ids = newSet)
    }


    //@typeconverter
    fun extractStocksToIntArray(): IntArray =this.set_ids.toIntArray()

    fun size(): Int = this.set_ids.size



}