package il.kod.movingaverageapplication1

import AllStocksViewModel
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import il.kod.movingaverageapplication1.data.repository.StocksRepository
import kotlin.getValue


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


    }



}