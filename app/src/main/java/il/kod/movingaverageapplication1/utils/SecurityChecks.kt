package il.kod.movingaverageapplication1.utils

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import android.util.Base64



fun decodeJWT(token: String): JSONObject {
    val payload = String(Base64.decode(token.split('.')[1], Base64.DEFAULT))
    return JSONObject(payload)
}