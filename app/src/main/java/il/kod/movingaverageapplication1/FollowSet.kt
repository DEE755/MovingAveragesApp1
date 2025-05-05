package il.kod.movingaverageapplication1

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import il.kod.movingaverageapplication1.data.Stock
import kotlinx.parcelize.Parcelize


    @Parcelize
    @Entity(tableName = "follow_set")
    data class FollowSet(
        @ColumnInfo(name="name")
        val name: String,
        @ColumnInfo(name="imageUri")
        internal var imageUri: String? =null,
        @ColumnInfo(name="user_comments")
        val userComments: String="",
        @ColumnInfo(name="set")
        val set :List<Stock> =listOf() //not mutable for parcelization purposes)

    ): Parcelable {

        @PrimaryKey(autoGenerate = true)

        // Update the list (not mutable for parcelization purposes)
        fun updateSet(newSet: List<Stock>): FollowSet {
            return this.copy(set = newSet)


        }
    }





