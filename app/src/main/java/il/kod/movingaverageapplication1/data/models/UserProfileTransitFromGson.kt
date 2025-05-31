package il.kod.movingaverageapplication1.data.models


import android.icu.text.IDNA.Info
import com.google.gson.annotations.SerializedName
import il.kod.movingaverageapplication1.data.objectclass.UserProfile
data class UserProfileTransitFromGson(
    val id: Int,
    val user_id: Int,
    val created_at: String,
    val username: String,
    val password: String
)
{


}