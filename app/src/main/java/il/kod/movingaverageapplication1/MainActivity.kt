package il.kod.movingaverageapplication1

import il.kod.movingaverageapplication1.ui.viewmodel.AllStocksViewModel

import android.os.Bundle
import android.util.Log

import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject lateinit var gson : Gson



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.isLoggable("OkHttp", Log.ASSERT)//mute okhttp logs

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(R.string.menu_title)
    }


    }


@GlideModule
class AppGlideModule_ : AppGlideModule() {


}