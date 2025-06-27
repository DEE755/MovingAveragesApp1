package il.kod.movingaverageapplication1.data.models

import com.google.gson.annotations.SerializedName

data class AdapterBackIDForGson(@SerializedName("success") val idExists: Boolean, val message: String, val back_id: Int)
{}