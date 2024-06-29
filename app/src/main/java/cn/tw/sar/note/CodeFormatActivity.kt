package cn.tw.sar.note

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.CodeFormat
import cn.tw.sar.note.entity.CodeLatterType
import cn.tw.sar.note.pages.MyDialog
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import com.google.gson.Gson
import kotlin.concurrent.thread

class CodeFormatActivity : ComponentActivity() {
    var formats  = mutableStateListOf<CodeFormat>()
    var formatsNum = mutableStateOf(0)
    fun loadCodeFormat(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@CodeFormatActivity)
            val formatDao = database.formatDao()
            formatsNum.value = formatDao.getCount()
            formats.clear()
            val list = formatDao.getAllFormat()
            runOnUiThread {
                formats.addAll(list)
            }
        }
    }

    var showDialog = mutableStateOf(false)

    var deleteID = mutableStateOf(0L)

    @Composable
    @Preview
    fun CodeFormatCard(
        format: CodeFormat = CodeFormat(
            codeFormat = "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]",
            codeLength = 9,
            codeTypes = "[0,0,0,0,0,0,0,0,0]"
        ),
        fontColor: Color = Color.Black,
        subBackgroundColor: Color = Color.White,
        onDelete: (id:Long) -> Unit = {}
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(
                    color = subBackgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(6.dp)
        ) {
            // 根据 format.codeForma 正则 随机生成一个符合的字符串
            var str = ""
            var codes = Gson().fromJson(format.codeTypes, Array<Int>::class.java)
            for (i in 0 until format.codeLength) {
                str += getTextName(codes[i])
            }
            var isMore = remember {
                mutableStateOf(false)
            }

            //  onDismissRequest: () -> Unit = {},
            //    properties: DialogProperties = DialogProperties(),
            //    onAgreeRequest : () -> Unit = {},
            //    showDialog: Boolean = false,
            //    fontColor: Color = Color.Black,
            //    subBackgroundColor: Color = Color.White,
            //    onClose: () -> Unit = {},
            //    content: @Composable () -> Unit = {},

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "示例：$str", color = fontColor,
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = format.codeFormat, color = fontColor,
                        fontSize = 10.sp,
                    )
                }
                IconButton(
                    onClick = {
                        isMore.value = !isMore.value
                    }
                ) {
                    Icon(
                        imageVector = if (isMore.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (isMore.value) "up" else "down",
                        tint = Color.Black,
                    )
                }
            }

           AnimatedVisibility(visible = isMore.value) {
               Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.End,
                   verticalAlignment = Alignment.CenterVertically
               ) {
                    Button(onClick = {
                        if (showDialog.value) {
                            showDialog.value = false
                            deleteID.value = 0
                        } else {
                            showDialog.value = true
                            deleteID.value = format.id
                        }
                    }) {
                        Text(text = "删除", color = fontColor)
                    }
               }
           }

        }
    }

    override fun onResume() {
        super.onResume()
        loadCodeFormat()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        IconButton(
                            onClick = {
                                val intent = Intent(this@CodeFormatActivity,
                                    CodeAddActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(10.dp)
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
                    ) { innerPadding ->
                    var fontColor = getDarkModeTextColor(this@CodeFormatActivity)
                    MyDialog(
                        onDismissRequest = {
                            if (showDialog.value) {
                                showDialog.value = false
                                deleteID.value = 0
                            }
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            usePlatformDefaultWidth = false
                        ),
                        showDialog = showDialog.value,
                        fontColor = fontColor,
                        subBackgroundColor = getDarkModeBackgroundColor(
                            this@CodeFormatActivity, 1
                        ),
                        content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth().padding(10.dp)

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
                                        showDialog.value = false
                                    }) {
                                        Text(text = "取消", color = fontColor)
                                    }
                                    Button(onClick = {
                                        showDialog.value = false
                                        //onDelete(format.id)
                                        thread {
                                             val database: CodeDatabase =
                                                CodeDatabase.getDatabase(this@CodeFormatActivity)
                                            val formatDao = database.formatDao()
                                            formatDao.deleteById(deleteID.value)
                                            runOnUiThread {
                                                loadCodeFormat()
                                                Toast.makeText(this@CodeFormatActivity, "删除成功", Toast.LENGTH_SHORT).show()
                                            }

                                        }
                                    }) {
                                        Text(text = "确定", color = fontColor)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
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
                                    this@CodeFormatActivity, 0
                                )
                            )
                            .padding(
                                20.dp
                            )
                    ) {
                        var fontColor = getDarkModeTextColor(this@CodeFormatActivity)

                        Column {
                            Text(
                                text = "自定义取件码格式", color = fontColor,
                                fontSize = 35.sp,
                                modifier = Modifier.fillMaxWidth(),

                                )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        if (formatsNum.value == 0) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()

                            ) {
                                Image(painter = painterResource(R.mipmap.empty),
                                    contentDescription = "空",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(
                                            10.dp
                                        )
                                )
                                Text(text = "暂无数据", color = fontColor,
                                    modifier = Modifier.padding(
                                        10.dp
                                    ),
                                    fontSize = 20.sp)
                            }
                        } else {
                            for (format in formats) {
                                CodeFormatCard(
                                    format = format,
                                    fontColor = fontColor,
                                    subBackgroundColor = getDarkModeBackgroundColor(
                                        this@CodeFormatActivity,1
                                    )
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}
