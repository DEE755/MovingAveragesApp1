package il.kod.movingaverageapplication1

import AllStocksViewModel

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import il.kod.movingaverageapplication1.data.repository.StocksRepository
import kotlin.getValue


class MainActivity : AppCompatActivity() {

    private lateinit var viewModelAllStocks: AllStocksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Menu"
    }

    }





