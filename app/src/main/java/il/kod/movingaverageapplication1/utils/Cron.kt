package il.kod.movingaverageapplication1.utils

import android.os.Handler
import android.os.Looper


    fun launchEveryTenSeconds(task: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                task()
                handler.postDelayed(this, 10000) // Schedule again after 10 seconds
            }
        }
        handler.post(runnable) // Start the first execution
    }

