package il.kod.movingaverageapplication1.data.models

import android.icu.text.IDNA.Info
import il.kod.movingaverageapplication1.data.UserProfile
data class UserProfileTransitFromGson(

        val info:Info,
        val result: List<UserProfile>
    )
{


}