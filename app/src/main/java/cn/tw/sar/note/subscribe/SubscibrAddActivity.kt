package cn.tw.sar.note.subscribe

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.Subscribe
import cn.tw.sar.note.entity.SubscribeType
import cn.tw.sar.note.subscribe.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.PickupCodeUtils
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import java.util.Calendar
import kotlin.concurrent.thread

class SubscibrAddActivity : ComponentActivity() {
    var monthList = mutableListOf<String>(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
    )
    var dayList = mutableListOf<String>(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
    )
    var yearList = mutableListOf<String>(

    )
    var hourList = mutableListOf<String>(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "20", "21", "22", "23"
    )
    var minuteList = mutableListOf<String>(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
        "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
        "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
        "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
    )
    var subscribeTypes = mutableListOf<SubscribeType>(
        SubscribeType.Daily, SubscribeType.Weekly, SubscribeType.Monthly,
        SubscribeType.Quarterly, SubscribeType.Yearly
    )
    var subscribeTypeStr = mutableListOf<String>(
        "日付", "周付", "月付", "季付", "年付"
    )
    var currIndex = mutableStateOf(0)

    var currYear = mutableStateOf("2024")
    var currMonth = mutableStateOf("12")
    var currDay = mutableStateOf("12")
    var currHour = mutableStateOf("12")
    var currMinute = mutableStateOf("12")


    var currType = mutableStateOf(SubscribeType.Daily)
    var currPrice = mutableStateOf("")
    var currHuoBi = mutableStateOf("CNY")
    var currName = mutableStateOf("")
    var currDesc = mutableStateOf("")
    var currAppName = mutableStateOf("")


    fun init(){
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        for (i in -5..10) {
            yearList.add((year + i).toString())
        }
        currYear.value = year.toString()
        var month = calendar.get(Calendar.MONTH) + 1
        currMonth.value = month.toString()
        if (month == 2){
            // 如果是2月，判断是否是闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0){
                // 闰年
                dayList.remove("30")
                dayList.remove("31")
            } else {
                // 平年
                dayList.remove("29")
                dayList.remove("30")
                dayList.remove("31")
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11){
            // 如果是4、6、9、11月
            dayList.remove("31")
        }
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        currDay.value = day.toString()

        // 初始化时间
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)
        currHour.value = hour.toString()
        currMinute.value = minute.toString()

    }

    override fun onResume() {
        super.onResume()
        init()

    }

    var isEdit = mutableStateOf(false)
    var item = mutableStateOf(0L)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isEdit.value = intent.getBooleanExtra("isEdit", false)
        item.value = intent.getLongExtra("item", 0L)
        if (isEdit.value) {
            val database: CodeDatabase = CodeDatabase.getDatabase(this)
            val subscribeDao = database.subscribeDao()
            thread {
                val item = subscribeDao.getById(item.value)
                runOnUiThread {
                    if (item != null) {
                        currName.value = item.name
                        currDesc.value = item.desp
                        currAppName.value = item.appName
                        currIndex.value = item.subscribeType.value
                        currPrice.value = item.amount.toString()
                        currHuoBi.value = item.currency
                        var calendar = Calendar.getInstance()
                        calendar.timeInMillis = item.lastRenewTime
                        currYear.value = calendar.get(Calendar.YEAR).toString()
                        currMonth.value = (calendar.get(Calendar.MONTH) + 1).toString()
                        currDay.value = calendar.get(Calendar.DAY_OF_MONTH).toString()
                    }
                }
            }
        }
        var showTitle = if (isEdit.value) {
            "编辑"
        } else {
            "添加"
        }
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val fontColor = getDarkModeTextColor(this@SubscibrAddActivity)
                    val subback = getDarkModeBackgroundColor(
                        this@SubscibrAddActivity, 1
                    )
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@SubscibrAddActivity, 0
                                )
                            )
                            .padding(
                                20.dp
                            )
                    ) {


                        Text(
                            text = "${showTitle}订阅",
                            color = fontColor,
                            fontSize = 35.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(20.dp))
                        // 两位小数

                        TextField(
                            value = currPrice.value,
                            onValueChange = {
                                try {
                                    currPrice.value = it
                                } catch (e: Exception) {
                                    return@TextField
                                }
                            },
                            placeholder = {
                                Text("请输入价格")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 40.sp,

                                ),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                disabledTextColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape =  MaterialTheme.shapes.medium


                        )
                        Spacer(modifier = Modifier.height(15.dp))

                        TextField(
                            value = currName.value,
                            onValueChange = {
                                currName.value = it
                            },
                            placeholder = {
                                Text("请输入订阅内容")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                disabledTextColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape =  MaterialTheme.shapes.medium


                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = currAppName.value,
                            onValueChange = {
                                currAppName.value = it
                            },
                            placeholder = {
                                Text("请输入订阅的应用名称")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                disabledTextColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            shape =  MaterialTheme.shapes.medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        var typeClick = remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(
                                    subback, shape = MaterialTheme.shapes.medium
                                ).padding(
                                    20.dp
                                ),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "订阅类型：",modifier = Modifier.fillMaxWidth(0.3f))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            typeClick.value = !typeClick.value
                                        }
                                ){
                                    Text(text = subscribeTypeStr[currIndex.value])
                                }
                            }
                            DropdownMenu(
                                expanded = typeClick.value,
                                modifier = Modifier.width(200.dp),
                                onDismissRequest = {
                                    typeClick.value = !typeClick.value

                                },
                                content = {
                                    subscribeTypeStr.forEach {
                                        DropdownMenuItem(
                                            onClick = {
                                                typeClick.value = !typeClick.value
                                                currIndex.value = subscribeTypeStr.indexOf(it)
                                            },
                                            text = {
                                                Text(text = it)
                                            }
                                        )
                                    }
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        var yearClick = remember { mutableStateOf(false) }
                        var monthClick = remember { mutableStateOf(false) }
                        var dayClick = remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(
                                    subback, shape = MaterialTheme.shapes.medium
                                ).padding(
                                    20.dp
                                ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "上次续费：",modifier = Modifier)
                                Row{
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                yearClick.value = !yearClick.value
                                            }
                                    ){
                                        Text(text = currYear.value+"年")
                                    }
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                monthClick.value = !monthClick.value
                                            }
                                    ){
                                        Text(text = currMonth.value+"月")
                                    }
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                dayClick.value = !dayClick.value
                                            }
                                    ){
                                        Text(text = currDay.value+"日")
                                    }
                                }
                            }
                            DropdownMenu(
                                expanded = yearClick.value,
                                modifier = Modifier.fillMaxWidth().padding(20.dp).height(200.dp).background(
                                    subback, shape = MaterialTheme.shapes.medium
                                ),
                                offset =  DpOffset(Dp(0f),Dp(0f)),

                                onDismissRequest = {
                                    yearClick.value = !yearClick.value

                                },
                                content = {
                                    yearList.forEach {
                                        DropdownMenuItem(
                                            onClick = {
                                                yearClick.value = !yearClick.value
                                                currYear.value = it
                                            },
                                            text = {
                                                Text(text = it)
                                            }
                                        )
                                    }
                                },
                            )
                            DropdownMenu(
                                expanded = dayClick.value,
                                modifier = Modifier.fillMaxWidth().padding(20.dp).height(200.dp).background(
                                    subback, shape = MaterialTheme.shapes.medium
                                ),
                                offset =  DpOffset(Dp(0f),Dp(0f)),
                                onDismissRequest = {
                                    dayClick.value = !dayClick.value

                                },
                                content = {
                                    dayList.forEach {
                                        DropdownMenuItem(
                                            onClick = {
                                                dayClick.value = !dayClick.value
                                                currDay.value = it
                                            },
                                            text = {
                                                Text(text = it)
                                            }
                                        )
                                    }
                                },
                            )
                            DropdownMenu(
                                expanded = monthClick.value,
                                modifier = Modifier.fillMaxWidth().padding(20.dp).height(200.dp).background(
                                    subback, shape = MaterialTheme.shapes.medium
                                ),
                                offset =  DpOffset(Dp(0f),Dp(0f)),
                                onDismissRequest = {
                                    monthClick.value = !monthClick.value

                                },
                                content = {
                                    monthList.forEach {
                                        DropdownMenuItem(
                                            onClick = {
                                                monthClick.value = !monthClick.value
                                                currMonth.value = it
                                            },
                                            text = {
                                                Text(text = it)
                                            }
                                        )
                                    }
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = currDesc.value,
                            onValueChange = {
                                currDesc.value = it
                            },
                            placeholder = {
                                Text("请输入备注")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            maxLines = 4,
                            colors = TextFieldDefaults.textFieldColors(
                                disabledTextColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape =  MaterialTheme.shapes.medium

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                // 检验所有参     var currYear = mutableStateOf("2024")
                                //    var currMonth = mutableStateOf("12")
                                //    var currDay = mutableStateOf("12")
                                //    var currHour = mutableStateOf("12")
                                //    var currMinute = mutableStateOf("12")
                                //
                                //
                                //    var currType = mutableStateOf(SubscribeType.Daily)
                                //    var currPrice = mutableStateOf(0.00)
                                //    var currHuoBi = mutableStateOf("CNY")
                                //    var currName = mutableStateOf("")
                                //    var currDesc = mutableStateOf("")
                                //    var currAppName = mutableStateOf("")
                                if (currName.value==""){
                                    Toast.makeText(this@SubscibrAddActivity,"内容不全",Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (currPrice.value==""){
                                    Toast.makeText(this@SubscibrAddActivity,"内容不全",Toast.LENGTH_SHORT).show()
                                    return@Button

                                }

                                if (currAppName.value==""){
                                    Toast.makeText(this@SubscibrAddActivity,"内容不全",Toast.LENGTH_SHORT).show()
                                    return@Button

                                }
                                var calendar = Calendar.getInstance()
                                calendar.set(
                                    currYear.value.toInt(),
                                    currMonth.value.toInt() - 1,
                                    currDay.value.toInt(),
                                    0,
                                    0,
                                    0
                               )
                                try{
                                    if (isEdit.value){
                                        var i2 = Subscribe(
                                            item.value,
                                            currName.value,
                                            currDesc.value,
                                            currAppName.value,
                                            SubscribeType.Convert.toSubscribeType(currIndex.value),
                                            calendar.timeInMillis,
                                            currPrice.value.toDouble(),
                                            currHuoBi.value,
                                            System.currentTimeMillis()
                                        )
                                        val database: CodeDatabase =
                                            CodeDatabase.getDatabase(this@SubscibrAddActivity)
                                        val subscribeDao = database.subscribeDao()
                                        thread{
                                            subscribeDao.update(i2)
                                            runOnUiThread {
                                                Toast.makeText(this@SubscibrAddActivity,"成功",Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        }
                                    } else {
                                        var i2 = Subscribe(
                                            0,
                                            currName.value,
                                            currDesc.value,
                                            currAppName.value,
                                            SubscribeType.Convert.toSubscribeType(currIndex.value),
                                            calendar.timeInMillis,
                                            currPrice.value.toDouble(),
                                            currHuoBi.value,
                                            System.currentTimeMillis()
                                        )
                                        val database: CodeDatabase =
                                            CodeDatabase.getDatabase(this@SubscibrAddActivity)
                                        val subscribeDao = database.subscribeDao()
                                        thread{
                                            subscribeDao.insert(i2)
                                            runOnUiThread {
                                                Toast.makeText(this@SubscibrAddActivity,"添加成功",Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        }
                                    }
                                } catch (e: Exception){
                                    Toast.makeText(this@SubscibrAddActivity,"价格格式错误",Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                           },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = showTitle)
                        }

                    }

                }
            }
        }
    }
}

