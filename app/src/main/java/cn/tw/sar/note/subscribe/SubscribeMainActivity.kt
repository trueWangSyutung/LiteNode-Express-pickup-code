package cn.tw.sar.note.subscribe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.Subscribe
import cn.tw.sar.note.entity.SubscribeType
import cn.tw.sar.note.pages.MyDialog
import cn.tw.sar.note.subscribe.page.SubscribeHomePage
import cn.tw.sar.note.subscribe.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import kotlin.concurrent.thread

class SubscribeMainActivity : ComponentActivity() {
    val list = mutableStateListOf<Subscribe>()
    val curr = mutableStateOf(0)
    val page = mutableStateOf(1)
    val pageSize = 10
    val sum = mutableStateOf(0.0)
    val deleteShow = mutableStateOf(false)
    val deleteId = mutableStateOf(0L)


    @SuppressLint("DefaultLocale")
    fun loadingList() {
        val database: CodeDatabase =
            CodeDatabase.getDatabase(this@SubscribeMainActivity)
        val subscribeDao = database.subscribeDao()
        // val filters = listOf(
        //             "默认", "日付", "周付","月付", "季付", "年付",
        //    )
        Log.d("MainActivity", "onSeleted ${curr.value}")

        page.value = 1
        sum.value = 0.0

        if (curr.value == 0) {
            thread {
                list.clear()
                val lists = subscribeDao.getAll(
                    (page.value-1) * pageSize,
                    pageSize
                )
                lists.forEach { item ->
                    // 判断有没有距离当前时间超过一个付款周期的
                    var duractionShow : Long = when(item.subscribeType){
                        SubscribeType.Daily -> (1*24*60*60*1000L)
                        SubscribeType.Weekly -> 7*24*60*60*1000L
                        SubscribeType.Monthly -> 31*24*60*60*1000L
                        SubscribeType.Quarterly -> 93*24*60*60*1000L
                        SubscribeType.Yearly -> 366*24*60*60*1000L
                    }
                    Log.d("MainActivity", "onSeleted ${item.lastRenewTime} ${duractionShow}")

                    while (System.currentTimeMillis() - item.lastRenewTime >= duractionShow){
                        // 如果超过一个付款周期，提示
                        // 更新
                        item.lastRenewTime += duractionShow
                    }
                    subscribeDao.update(item)

                }

                runOnUiThread {
                    lists.forEach { item ->
                        list.add(item)
                        item.amount = String.format("%.2f", item.amount).toDouble()
                        sum.value += item.amount
                        // 格式化
                    }
                    sum.value = String.format("%.2f", sum.value ).toDouble()
                    Log.d("MainActivity", "onSeleted ${list}")

                }
            }
        } else  if (curr.value == 1) {
            thread {
                list.clear()
                var lists = subscribeDao.getAll(
                    (page.value-1) * pageSize,
                    pageSize
                )
                lists.forEach { item ->
                    // 判断是否是日付
                    if (item.subscribeType == SubscribeType.Daily) {
                        // 如果是日付，无操作
                    } else if (item.subscribeType == SubscribeType.Weekly) {
                        // 如果是周付，计算周付的金额
                        item.amount /= 7
                    } else if (item.subscribeType == SubscribeType.Monthly) {
                        // 如果是月付，计算月付的金额
                        item.amount /= 31
                    } else if (item.subscribeType == SubscribeType.Quarterly) {
                        // 如果是季付，计算季付的金额
                        item.amount /= 93
                    } else if (item.subscribeType == SubscribeType.Yearly) {
                        // 如果是年付，计算年付的金额
                        item.amount /= 366
                    }
                    item.subscribeType = SubscribeType.Daily
                    // 保留2位小数
                    item.amount = String.format("%.2f", item.amount).toDouble()
                    sum.value += item.amount
                    // 格式化
                }
                sum.value = String.format("%.2f", sum.value ).toDouble()

                runOnUiThread {
                    list.addAll(lists)
                }
            }
        } else  if (curr.value == 2) {
            thread {
                list.clear()
                var lists = subscribeDao.getAll(
                    (page.value-1) * pageSize,
                    pageSize
                )
                lists.forEach { item ->
                    // 判断是否是周付
                    if (item.subscribeType == SubscribeType.Daily) {
                        // 如果是日付，计算周付的金额
                        item.amount *= 7
                    } else if (item.subscribeType == SubscribeType.Weekly) {
                        // 如果是周付，计算周付的金额
                    } else if (item.subscribeType == SubscribeType.Monthly) {
                        // 如果是月付，计算月付的金额
                        item.amount /= 4
                    } else if (item.subscribeType == SubscribeType.Quarterly) {
                        // 如果是季付，计算季付的金额
                        item.amount /= 12
                    } else if (item.subscribeType == SubscribeType.Yearly) {
                        // 如果是年付，计算年付的金额
                        item.amount /= 52
                    }
                    item.subscribeType = SubscribeType.Weekly
                    // 保留2位小数
                    item.amount = String.format("%.2f", item.amount).toDouble()
                    sum.value += item.amount
                    // 格式化
                }
                sum.value = String.format("%.2f", sum.value ).toDouble()
                runOnUiThread {
                    list.addAll(lists)
                }
            }
        } else  if (curr.value == 3) {
            thread {
                list.clear()
                var lists = subscribeDao.getAll(
                    (page.value-1) * pageSize,
                    pageSize
                )
                lists.forEach { item ->
                    // 判断是否是月付
                    if (item.subscribeType == SubscribeType.Daily) {
                        // 如果是日付，计算周付的金额
                        item.amount *= 31
                    } else if (item.subscribeType == SubscribeType.Weekly) {
                        // 如果是周付，计算周付的金额
                        item.amount *= 4
                    } else if (item.subscribeType == SubscribeType.Monthly) {
                        // 如果是月付，计算月付的金额

                    } else if (item.subscribeType == SubscribeType.Quarterly) {
                        // 如果是季付，计算季付的金额
                        item.amount /= 3
                    } else if (item.subscribeType == SubscribeType.Yearly) {
                        // 如果是年付，计算年付的金额
                        item.amount /= 12
                    }
                    item.subscribeType = SubscribeType.Monthly
                    // 保留2位小数
                    item.amount = String.format("%.2f", item.amount).toDouble()
                    sum.value += item.amount
                    // 格式化
                }
                sum.value = String.format("%.2f", sum.value ).toDouble()
                runOnUiThread {
                    list.addAll(lists)
                }
            }
        } else  if (curr.value == 4) {
            thread {
                list.clear()
                var lists = subscribeDao.getAll(
                    (page.value-1) * pageSize,
                    pageSize
                )
                lists.forEach { item ->
                    // 判断是否是季付
                    if (item.subscribeType == SubscribeType.Daily) {
                        // 如果是日付，计算周付的金额
                        item.amount *= 93
                    } else if (item.subscribeType == SubscribeType.Weekly) {
                        // 如果是周付，计算周付的金额
                        item.amount *= 12
                    } else if (item.subscribeType == SubscribeType.Monthly) {
                        // 如果是月付，计算月付的金额
                        item.amount *= 3
                    } else if (item.subscribeType == SubscribeType.Quarterly) {
                        // 如果是季付，计算季付的金额
                    } else if (item.subscribeType == SubscribeType.Yearly) {
                        // 如果是年付，计算年付的金额
                        item.amount /= 4
                    }
                    item.subscribeType = SubscribeType.Quarterly
                    // 保留2位小数
                    item.amount = String.format("%.2f", item.amount).toDouble()
                    sum.value += item.amount
                    // 格式化
                }
                sum.value = String.format("%.2f", sum.value ).toDouble()
                runOnUiThread {
                    list.addAll(lists)
                }
            }
        }
          else  if (curr.value == 5) {
            thread {
                list.clear()
                var lists = subscribeDao.getAll(
                    (page.value-1) * pageSize,
                    pageSize
                )
                lists.forEach { item ->
                    // 判断是否是季付
                    if (item.subscribeType == SubscribeType.Daily) {
                        // 如果是日付，计算周付的金额
                        item.amount *= 366
                    } else if (item.subscribeType == SubscribeType.Weekly) {
                        // 如果是周付，计算周付的金额
                        item.amount *= 52
                    } else if (item.subscribeType == SubscribeType.Monthly) {
                        // 如果是月付，计算月付的金额
                        item.amount *= 12
                    } else if (item.subscribeType == SubscribeType.Quarterly) {
                        // 如果是季付，计算季付的金额
                        item.amount *= 4
                    } else if (item.subscribeType == SubscribeType.Yearly) {
                        // 如果是年付，计算年付的金额
                    }
                    item.subscribeType = SubscribeType.Yearly
                    // 保留2位小数
                    item.amount = String.format("%.2f", item.amount).toDouble()

                    sum.value += item.amount
                    // 格式化
                }
                sum.value = String.format("%.2f", sum.value ).toDouble()
                runOnUiThread {
                    list.addAll(lists)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        loadingList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(

                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        val fontColor = getDarkModeTextColor(this@SubscribeMainActivity)
                        MyDialog(
                            onDismissRequest = {
                                if (deleteShow.value) {
                                    deleteShow.value = false
                                    deleteId.value = 0
                                }
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                usePlatformDefaultWidth = false
                            ),
                            showDialog = deleteShow.value,
                            fontColor = fontColor,
                            subBackgroundColor = getDarkModeBackgroundColor(
                                this@SubscribeMainActivity, 1
                            ),
                            content = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)

                                ) {
                                    Text(
                                        text = "确定删除吗？", color = fontColor,
                                        fontSize = 20.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "删除后不可恢复", color = fontColor,
                                        fontSize = 15.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(onClick = {
                                            deleteShow.value = false
                                        }) {
                                            Text(text = "取消", color = fontColor)
                                        }
                                        Button(onClick = {
                                            deleteShow.value = false
                                            //onDelete(format.id)
                                            if (deleteId.value != 0L) {
                                                thread {
                                                    val database: CodeDatabase =
                                                        CodeDatabase.getDatabase(this@SubscribeMainActivity)
                                                    val codeDao = database.subscribeDao()
                                                    codeDao.deleteById(deleteId.value)
                                                    page.value = 1
                                                    runOnUiThread {
                                                        loadingList()
                                                        deleteId.value = 0L
                                                        Toast.makeText(this@SubscribeMainActivity, "删除成功", Toast.LENGTH_SHORT).show()

                                                    }
                                                }
                                            } else {
                                                Toast.makeText(this@SubscribeMainActivity, "删除失败", Toast.LENGTH_SHORT).show()
                                            }

                                        }) {
                                            Text(text = "确定", color = fontColor)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        SubscribeHomePage(
                            backgroundColor = getDarkModeBackgroundColor(
                                this@SubscribeMainActivity, 0
                            ),
                            subBackgroundColor = getDarkModeBackgroundColor(
                                this@SubscribeMainActivity, 1
                            ),
                            fontColor = getDarkModeTextColor(this@SubscribeMainActivity),
                            list = list,
                            onSeleted = { id ->
                                Log.d("MainActivity", "onSeleted $id")
                                curr.value = id
                                loadingList()
                            },
                            onDeleteDots = { index->
                                deleteShow.value = true
                                deleteId.value = list[index].id
                            },
                            onselectNum = curr.value,
                            onEditCode = {
                                val intent = Intent(
                                    this@SubscribeMainActivity,
                                    SubscibrAddActivity::class.java
                                )
                                intent.putExtra("item", it.id)
                                intent.putExtra("isEdit", true)
                                startActivity(intent)
                            },
                            onAddClick = {
                                startActivity(
                                    Intent(
                                        this@SubscribeMainActivity,SubscibrAddActivity::class.java
                                    )
                                )
                            },
                            sum = sum.value
                        )
                    }
                }
            }
        }
    }
}
