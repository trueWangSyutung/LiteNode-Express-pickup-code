package cn.tw.sar.note

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.CodeFormat
import cn.tw.sar.note.entity.CodeLatterType
import cn.tw.sar.note.entity.PostStation
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import com.google.gson.Gson
import kotlin.concurrent.thread

class PortsAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

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
                                    this@PortsAddActivity, 0
                                )
                            )
                            .padding(
                                20.dp
                            )
                    ) {

                        var name = remember {
                            mutableStateOf<String>("")
                        }
                        val fontColor = getDarkModeTextColor(this@PortsAddActivity)
                        var address = remember {
                            mutableStateOf("")
                        }

                        var showDialog = remember { mutableStateOf(false) }

                        Column {
                            Text(
                                text = "添加驿站", color = fontColor,
                                fontSize = 35.sp,
                                modifier = Modifier.fillMaxWidth(),

                                )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        // 纯数字、输入取件码位数
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = name.value.toString(),
                                onValueChange = {
                                    name.value = it
                                },
                                label = { Text("驿站名称") },
                                modifier = Modifier.fillMaxWidth(1f),
                                maxLines = 1,
                            )

                        }

                        Text(text = "请输入驿站名称，" +
                                "驿站名称必须是收到的取件短信中的驿站名称，" +
                                "要求：必须一字不差。才能正确识别", color = fontColor,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(value = address.value,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("输入驿站地址")
                                },
                                onValueChange = {
                                    address.value = it
                                }
                            )
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 20.dp),
                            onClick = {
                                if (name.value.isEmpty()) {
                                    Toast.makeText(this@PortsAddActivity,
                                        "请输入驿站名称",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    if (address.value.isEmpty()) {
                                        Toast.makeText(this@PortsAddActivity,
                                            "请输入驿站地质",
                                            Toast.LENGTH_SHORT).show()
                                    } else {
                                        thread {
                                            val codeDatabase =
                                                CodeDatabase.getDatabase(this@PortsAddActivity)
                                            val codeFormat = PostStation(
                                                name = name.value,
                                                address = address.value,
                                                insertTime = System.currentTimeMillis()
                                            )
                                            codeDatabase.portsDao().insertPost(codeFormat)
                                            runOnUiThread {
                                                Toast.makeText(this@PortsAddActivity,
                                                    "保存成功",
                                                    Toast.LENGTH_SHORT).show()
                                                finish()

                                            }
                                        }
                                    }
                                }
                            }) {
                            Text("保存")
                        }


                    }
                }
            }
        }
    }
}
