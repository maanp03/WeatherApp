//Maan Patel
package com.maan.weatherapp

import ApiInterface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.maan.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchData("Toronto")
        searchCity()
    }

    private fun searchCity() {
        val search = binding.searchView
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null) {
                fetchData(query)
           }
                return true
        }

            override fun onQueryTextChange(text: String?): Boolean {
            return true
            }
        })

    }

    private fun fetchData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "248cae56cd152e4e658efe2af816ac5c", "metric")
        response.enqueue(object: Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    val temperature = Math.round(responseBody.main.temp).toInt()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val sea = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = Math.round(responseBody.main.temp_max).toInt()
                    val minTemp = Math.round(responseBody.main.temp_min).toInt()

                    binding.textViewTemp.text = "$temperature °C"
                    binding.textViewWeather.text = condition
                    binding.textViewMax.text = "Max Temp: $maxTemp °C"
                    binding.textViewMin.text = "Min Temp: $minTemp °C"
                    binding.textViewHumidity.text = "$humidity %"
                    binding.textViewWind.text = "$windSpeed m/s"
                    binding.textViewSunrise.text = "${time(sunRise)}"
                    binding.textViewSunset.text = "${time(sunSet)}"
                    binding.textViewSea.text = "$sea hPa"
                    binding.textViewSunny.text = condition
                    binding.textViewDay.text = day(System.currentTimeMillis())
                    binding.textViewDate.text = date()
                    binding.textViewCityName.text = "$cityName"

                    // Changing the image according to weather condition
                    changingImage(condition)





                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })



    }

    private fun changingImage(conditions: String) {
        when(conditions){
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            "Clear Sky", "Sunny", "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }

        else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }

    private fun day(timeStamp: Long):String {

        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        return simpleDateFormat.format(Date())

    }

    private fun time(timeStamp: Long):String {

        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return simpleDateFormat.format(Date(timeStamp * 1000))

    }
    private fun date():String {

        val simpleDateFormat = SimpleDateFormat("dd MMM YYYY", Locale.getDefault())

        return simpleDateFormat.format(Date())

    }
}