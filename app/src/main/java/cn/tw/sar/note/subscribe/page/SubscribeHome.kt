package cn.tw.sar.note.subscribe.page

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import cn.tw.sar.note.R
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.entity.CodeAndYz
import cn.tw.sar.note.entity.Subscribe
import cn.tw.sar.note.entity.SubscribeType
import cn.tw.sar.note.utils.expercessToResource
import java.text.SimpleDateFormat
import java.util.Date

private fun getDayChange(timest: Long): String {
    // 获取当前时间戳
    val currentTime = System.currentTimeMillis()
    // 计算时间差
    val time = currentTime - timest
    // 计算天数
    val day = time / (1000 * 60 * 60 * 24)
    // 计算小时数
    if (day > 0) {
        return "${day}天前"
    }else{
        val hour = time / (1000 * 60 * 60)
        if (hour > 0) {
            return "${hour}小时前"
        }else{
            val minute = time / (1000 * 60)
            if (minute > 0) {
                return "${minute}分钟前"
            }else{
                return "刚刚"
            }
        }
    }

}


@SuppressLint("SimpleDateFormat")
private fun timeStempToTime(timest: Long): String {
    val canlender = java.util.Calendar.getInstance()
    canlender.timeInMillis = timest
    val year = canlender.get(java.util.Calendar.YEAR)
    val month = canlender.get(java.util.Calendar.MONTH) + 1
    val day = canlender.get(java.util.Calendar.DAY_OF_MONTH)

    return "${year}年${month}月${day}日";
}
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
private fun SubscribeCard(
    modifier: Modifier = Modifier,
    fontColor : Color = Color.Black,
    card : Subscribe ,
    index:  Int = 0,

    onDeleteDots: (id:Int) -> Unit = { _ -> },
    onEditCode:  (code:Subscribe) -> Unit = { _ -> },
){
    var checked = remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                Log.d("dragAmount", "dragAmount: $dragAmount")
                Log.d("dragAmount", "dragAmount: ${change.positionChanged()}")
                if (change.positionChanged()) {
                    if (dragAmount <-20) {
                        checked.value = true
                    } else if (dragAmount > 20) {
                        checked.value = false
                    }
                }
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(5.dp, 0.dp)
        ) {
            Row(

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment =  Alignment.Start,

                    ){
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(text = card.appName,
                        color = fontColor, fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        style = TextStyle(fontStyle = FontStyle.Normal,
                        )

                    )
                    Text(text = card.name,
                        color = fontColor,
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    )  // 订阅名称
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(text = "上次付费："+ timeStempToTime(card.lastRenewTime), color = fontColor,
                        fontSize = 12.sp,
                        // 添加删除线
                        style = TextStyle(fontStyle = FontStyle.Normal,
                        )
                    )


                }



                if (checked.value) {
                    Row{
                        Text(text = "修改" ,
                            color = fontColor, fontSize = 16.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Normal,
                            ),
                            modifier = Modifier
                                .padding(3.dp)
                                .background(
                                    Color.Green,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable {
                                    onEditCode(card)
                                    checked.value = false
                                }
                                .padding(5.dp)
                        )
                        Text(text = "删除",
                            color = fontColor, fontSize = 16.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Normal,
                            ),
                            modifier = Modifier
                                .padding(3.dp)
                                .background(
                                    Color.Red,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable {
                                    onDeleteDots(index)
                                    checked.value = false
                                }
                                .padding(5.dp)
                        )
                    }
                } else{
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment =  Alignment.End,

                        ) {
                        Text(text = card.amount.toString() + SubscribeType.Convert.toShowString(card.subscribeType),
                            color = fontColor, fontSize = 22.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Normal,
                            )
                        )
                        Text(text = card.currency,
                            color = fontColor, fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Normal,
                            )
                        )
                    }
                }
            }





        }

    }
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SubscribeHomePage(
    backgroundColor : Color = Color.White,
    subBackgroundColor : Color = Color(0xFFFFFAF8),
    fontColor : Color = Color.Black,
    list : List<Subscribe>,
    onSeleted: (id:Int) -> Unit = { _ -> },

    onDeleteDots: (id:Int) -> Unit = { _ -> },
    onEditCode:  (code:Subscribe) -> Unit = { _ -> },
    onAddClick: () -> Unit = {  },
    onselectNum: Int = 0,
    sum:Double = 0.0,
    loadMore: () -> Unit = {  }
){
    val filters = listOf(
        "默认", "日付", "周付","月付", "季付", "年付",
    )
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(20.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(text = "订阅", color = fontColor, fontSize = 35.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

            ){
                AnimatedVisibility(visible = onselectNum > 0) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment =  Alignment.End,
                        modifier = Modifier.clickable {
                            onSeleted((onselectNum)%(filters.size-1)-1)
                        }
                    ) {
                        Text(text = sum.toString() + SubscribeType.Convert.toShowString(SubscribeType.Convert.toSubscribeType(onselectNum-1)),
                            color = fontColor, fontSize = 22.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Normal,
                            )
                        )
                        Text(text = "CNY",
                            color = fontColor, fontSize = 14.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Normal,
                            )
                        )
                    }

                }
                Spacer(modifier = Modifier.width(14.dp))
                IconButton(
                    onClick = {
                        onAddClick()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.extraLarge
                        )

                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "add",
                        tint = Color.White,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var selected by remember { mutableStateOf(0) }
            for (index in 0 until  filters.size) {
                val leadingIcon: @Composable () -> Unit = {
                    Icon(Icons.Default.Check, null) }
                FilterChip(
                    selected = index == selected,
                    onClick = {
                        selected = index
                        onSeleted(index)
                    },
                    label = { Text(filters[index]) },
                    leadingIcon = if (index == selected) leadingIcon else null
                )
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp, 0.dp, 0.dp)

        ) {
            Log.d("SubscribeHomePage", "list.size: ${list.size}")
            if (list.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )

                ) {
                    Image(painter = painterResource(R.mipmap.empty),
                        contentDescription = "空",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(
                                30.dp
                            )
                    )
                    Text(text = "暂无数据", color = fontColor,
                        modifier = Modifier.padding(
                            30.dp
                        ),
                        fontSize = 20.sp)
                }
            }
            else {
                for (index in 0 until list.size) {
                    SubscribeCard(
                        card = list[index],
                        fontColor = fontColor,
                        index = index,
                        onDeleteDots = onDeleteDots,
                        onEditCode = onEditCode
                    )
                    // 如果不是最后一个
                    if (index != list.size - 1) {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 10.dp
                            )
                            .height(1.dp)
                            .background(
                                Color(0xFFE0E0E0)
                            ))
                    }


                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        loadMore()
                    }) {
                        Text(text = "加载更多", color = fontColor, fontSize = 12.sp)
                    }
                }
            }
        }
    }

}