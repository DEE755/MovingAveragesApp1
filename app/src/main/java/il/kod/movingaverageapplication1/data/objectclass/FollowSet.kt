package il.kod.movingaverageapplication1.data.objectclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "follow_set")
data class FollowSet(
    @ColumnInfo(name="name") @Expose
    var name: String,
    @ColumnInfo(name="image_uri") @Expose
    internal var imageUri: String? =null,
    @ColumnInfo(name="user_description") @Expose
    val userComments: String="",
    @ColumnInfo(name="set_ids")@Expose
    val set_ids : List<Int>, //(not mutable for parcelization purposes --> handled via type converter)

    @ColumnInfo(name="notifications_prices") @Expose
    var notificationsPriceThreeshold : Double,

    @ColumnInfo(name="isDirty")@Expose(serialize = false, deserialize = false)
    var isDirty: Boolean = false

): Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id_front : Int =0
    // Update the list (not mutable for parcelization purposes)
    fun updateSet(newSet: List<Int>): FollowSet {
        return this.copy(set_ids = newSet)
    }


    //@typeconverter
    fun extractStocksToIntArray(): IntArray =this.set_ids.toIntArray()

    fun size(): Int = this.set_ids.size



}