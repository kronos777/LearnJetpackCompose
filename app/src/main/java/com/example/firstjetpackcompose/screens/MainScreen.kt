package com.example.firstjetpackcompose.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.firstjetpackcompose.R
import com.example.firstjetpackcompose.data.Hour
import com.example.firstjetpackcompose.data.WeatherModel
import com.example.firstjetpackcompose.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            list
        ) { _, item ->
            listItem(item, currentDay)
        }
    }
}

@Composable
fun mainCard(currentDay: MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit) {

    Column(
        modifier = Modifier
            .padding(5.dp)

    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueLight),
            //backgroundColor = BlueLight,
            //elevation = 10,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueLight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                            text = currentDay.value.time,
                            style = TextStyle(fontSize = 15.sp),
                            color = Color.White
                        )
                        AsyncImage(
                            model = "https:${currentDay.value.icon}",
                            contentDescription = "im2",
                            modifier = Modifier
                                .size(35.dp)
                                .padding(top = 3.dp, end = 8.dp)
                        )
                    }

                    Text(
                        text = currentDay.value.city,
                        style = TextStyle(fontSize = 24.sp),
                        color = Color.White
                    )

                    Text(
                        text = currentDay.value.currentTemp,
                        style = TextStyle(fontSize = 65.sp),
                        color = Color.White
                    )

                    Text(
                        text = currentDay.value.condition,
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            onClickSearch.invoke()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                tint = Color.White,
                                contentDescription = "seach"
                            )
                        }
                        Text(
                            text = "${currentDay.value.maxTemp} C / ${currentDay.value.minTemp} C",
                            style = TextStyle(fontSize = 16.sp),
                            color = Color.White
                        )
                        IconButton(onClick = {
                            onClickSync.invoke()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_sync_24),
                                tint = Color.White,
                                contentDescription = "seach"
                            )
                        }
                    }

                }


            }
        }
    }


}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun tabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {

    val tabList = listOf("Days", "Hours")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .background(BlueLight)
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.background(BlueLight)
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = text, color = Color.White)
                    },
                    modifier = Modifier.background(BlueLight)
                )
            }
        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->
            val list = when(index){
                0 -> daysList.value
                1 -> getWeatherByHours(currentDay.value.hours)
                else -> daysList.value
            }
            MainList(list, currentDay)
        }
    }
}

fun getWeatherByHours(hours: List<Hour>): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    for (item in hours){

        list.add(
            WeatherModel(
                "",
                item.time,
                item.temp_c.toFloat().toInt().toString() + "ºC",
                item.condition.text,
                item.condition.icon,
                item.temp_c.toFloat().toInt().toString(),
                "",
                listOf()
            )
        )
    }
    return list
}

