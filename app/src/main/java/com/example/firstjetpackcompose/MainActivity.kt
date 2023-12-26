package com.example.firstjetpackcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.firstjetpackcompose.data.Forecastday
import com.example.firstjetpackcompose.data.WeatherModel
import com.example.firstjetpackcompose.data.WeatherResponse
import com.example.firstjetpackcompose.screens.dialogSearch
import com.example.firstjetpackcompose.screens.mainCard
import com.example.firstjetpackcompose.screens.tabLayout
import com.google.gson.Gson
import org.json.JSONObject


private const val API_KEY = "3365ba381672482dad3131825231812"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           // getDataDays("London", this)
            val dialogState = remember {
                mutableStateOf(false)
            }

            val daysList = remember {
                mutableStateOf(listOf<WeatherModel>())
            }
            var currentDay = remember {
                mutableStateOf(WeatherModel("","","","","","","", listOf()))
            }
            getDataDays("Moscow", this, daysList, currentDay)
            if (dialogState.value) {
                dialogSearch(dialogState, onSubmit = {
                    getDataDays(it, this@MainActivity, daysList, currentDay)
                })
            }

            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = "bg",
                modifier = Modifier
                    .fillMaxSize(),
                  //  .alpha(0.5f),
                contentScale = ContentScale.FillBounds
            )
            Column {
                mainCard(currentDay, onClickSync = {
                    getDataDays("Moscow", this@MainActivity, daysList, currentDay)
                }, onClickSearch = {
                    dialogState.value = true
                })
                tabLayout(daysList, currentDay)
            }
        }
    }
}

@Composable
private fun Greeting(name: String, context: Context) {
    val state = remember {
        mutableStateOf("Unknown")
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(),
           // .background(color = Color.Red),
            contentAlignment = Alignment.Center
        ){
            Text(text = "Temp in $name = ${state.value}")
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
           // .background(color = Color.Blue),
            contentAlignment = Alignment.BottomStart
        ) {
            Button(
                onClick = {
                    //getResult(name, state, context)
                   // getDataDays(name, context)
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),

                ) {
                Text(text = "Refresh")
            }
        }
    }
}

private fun getResult(city: String, state: MutableState<String>, context: Context) {
    val url = "https://api.weatherapi.com/v1/current.json" +
            "?key=$API_KEY&" +
            "q=$city" +
            "&aqi=no"

    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        com.android.volley.Request.Method.GET,
        url,
        {
            response ->
            val obj = JSONObject(response)
            state.value = obj.getJSONObject("current").getString("temp_c")
        },
        {
            error ->
            Log.d("myLog", error.toString())
        }
    )
    queue.add(stringRequest)

}

private fun getDataDays(city: String, context: Context, daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>)  {

    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        com.android.volley.Request.Method.GET,
        url,
        {
            response ->
            val gson = Gson()
            val model = gson.fromJson(response, WeatherResponse::class.java)
            val responseWeatherByDays = getWeatherByDaysList(city, model.forecast.forecastday, model.current.temp_c.toFloat().toInt().toString())
            daysList.value = responseWeatherByDays
            currentDay.value = responseWeatherByDays[0]
            //Log.d("MyLog currentDay", responseWeatherByDays[0].toString())
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


private fun getWeatherByDaysList(city: String, listData: List<Forecastday>, currentTemp: String): List<WeatherModel> {
    if(listData.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    listData.forEach{
        list.add(
            WeatherModel(
                city,
                it.date,
                currentTemp,
                it.day.condition.text,
                it.day.condition.icon,
                it.day.maxtemp_c.toString(),
                it.day.mintemp_c.toString(),
                it.hour
            )
        )
    }
    return list
}


