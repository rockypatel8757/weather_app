package com.rockypatel.weatherapp

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.rockypatel.weatherapp.databinding.ActivityMainBinding
import com.rockypatel.weatherapp.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadcard.visibility= View.VISIBLE


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getWeatherReport(query.toString())
                binding.loadcard.visibility= View.VISIBLE
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })
        binddate()
        CoroutineScope(Dispatchers.IO).launch {
            getWeatherReport("hyderabad")

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun binddate() {
        val cd = LocalDate.now()
        val fomatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        binding.datetext.text = cd.format(fomatter).toString()
    }

    private fun getWeatherReport(givecity: String) {
        val builder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(mInterface::class.java)

        val Wresponse = builder.getdata(givecity, "6cfb18afa4928e93c73dcd9211726510", "metric")

        Wresponse.enqueue(object : Callback<Weather?> {
            override fun onResponse(call: Call<Weather?>?, response: Response<Weather?>?) {
                val wbody = response?.body()

                if (response != null) {
                    if (response.isSuccessful && wbody!=null) {
                        val data = wbody.main.humidity
                        val temp = wbody.main.temp
                        binding.loadcard.visibility= View.GONE
                        binding.degreeCelcius.text = temp.toString()
                        binding.locationtxt.text = wbody.name
                        binding.humidity.text = wbody.main.humidity.toString()
                        binding.windspeed.text = wbody.wind.speed.toString()
                        binding.condition.text = wbody.weather[0].description
                        binding.sunrise.text = wbody.sys.sunrise.toString()
                        binding.sunset.text = wbody.sys.sunset.toString()
                        binding.sea.text = wbody.sys.id.toString()
                        binding.mintxt.text = "MIN: " + wbody.main.temp_min.toString()
                        binding.maxtxt.text = "MAX: " + wbody.main.temp_max.toString()

                        Log.d("myTAG", "Humidity: $data")
                    }else{
                        binding.loadcard.visibility= View.GONE
                        Toast.makeText(applicationContext, " You have entered Invalid City.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }else {
                    binding.loadcard.visibility= View.GONE
                    Toast.makeText(
                        applicationContext,
                        " You have entered Invalid City",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<Weather?>?, t: Throwable?) {
                if (t != null) {
                    Log.d("errorTAG", "onFailure: ${t.localizedMessage}")
                }
            }
        })


    }
}