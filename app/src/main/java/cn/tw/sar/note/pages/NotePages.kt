package cn.tw.sar.note.pages

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.SwipeDirection
import androidx.glance.appwidget.CheckBox

import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import cn.tw.sar.note.R
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.entity.CodeAndYz
import cn.tw.sar.note.entity.NoteClass
import cn.tw.sar.note.entity.Notes
import cn.tw.sar.note.utils.expercessToResource
import cn.tw.sar.note.utils.getDarkModeTextColor
import dev.jeziellago.compose.markdowntext.MarkdownText
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
    //val ptimest=1000L*timest
    // 转换为 2024-12-12 12:12
    val sdf =  SimpleDateFormat("yyyy-MM-dd HH:mm");
    val dateStr = sdf.format(Date(timest).time);
    return dateStr;
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotePage(
    backgroundColor : Color = Color.White,
    subBackgroundColor : Color = Color(0xFFFFFAF8),
    fontColor : Color = Color.Black,
    list : List<Notes> = listOf() ,
    classLists : List<NoteClass> = listOf() ,
    onSeleted: (id:Int) -> Unit = { _ -> },
    onAddClick: () -> Unit = {  },
    isagent: Boolean = false,
    getAgent: () -> Unit = { },
    clickItem :  (id:Int)-> Unit = { _ -> },
    loadMore : () -> Unit = {  },
){

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(20.dp)

    ) {
        Column(
            modifier = Modifier.fillMaxHeight(0.25f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Column {
                    Text(text = "笔记", color = fontColor, fontSize = 35.sp)
                }

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
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(visible = !isagent ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .border(1.dp, Color.Gray, MaterialTheme.shapes.medium)
                        .padding(3.dp)
                        .clickable {
                            getAgent()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(3.dp)
                    )
                    // 让文字滚动
                    var offset by remember { mutableStateOf(0f) }
                    Text(text = "您还未曾授权短信及其悬浮窗口权限，无法自动读取短信中的取件码，点击启用取件码自动读取。",
                        fontSize = 12.sp,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scrollable(
                                orientation = Orientation.Horizontal,
                                state = rememberScrollableState { delta ->
                                    offset += delta
                                    delta
                                }
                            ),
                        color = fontColor)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    )
            ) {
                var selected by remember { mutableStateOf(0) }
                for (index in 0 until  classLists.size) {
                    val leadingIcon: @Composable () -> Unit = {
                        Icon(Icons.Default.Check, null) }
                    FilterChip(
                        selected = index == selected,
                        onClick = {
                            selected = index
                            onSeleted(index)
                        },
                        label = { Text(classLists[index].className) },
                        leadingIcon = if (index == selected) leadingIcon else null
                    )
                }

            }
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(0.dp, 20.dp, 0.dp, 0.dp)

        ) {
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
            } else {
               LazyVerticalGrid(columns = GridCells.Fixed(2),
                   modifier = Modifier.fillMaxWidth(),contentPadding = PaddingValues(2.dp),
                   verticalArrangement = Arrangement.spacedBy(2.dp),
                     horizontalArrangement = Arrangement.spacedBy(2.dp),
               ) {
                  items(list.size) { index ->
                      Column(
                          verticalArrangement = Arrangement.SpaceBetween,
                          horizontalAlignment = Alignment.Start,
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(4.dp)
                              .height(180.dp)
                              .clickable {
                                  clickItem(index)
                              }
                              .background(
                                  subBackgroundColor,
                                  shape = MaterialTheme.shapes.medium
                              )
                              .padding(5.dp)
                      ) {
                          Column {
                              Text(text = list[index].noteTitle, color = fontColor, fontSize = 20.sp)
                              Text(text = list[index].noteContent , color = fontColor,
                                  fontSize = 12.sp, maxLines = 5,
                                  overflow = TextOverflow.Ellipsis)

                          }
                          Text(
                              text = getDayChange(list[index].updateTime),
                              color = fontColor,
                              fontSize = 12.sp
                          )

                      }
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