package cn.tw.sar.note.pages


import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.note.R

data class AboutItem(
    val title: String,
    val content: String
)


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun AboutPage(
    backgroundColor : Color = Color.White,
    subBackgroundColor : Color = Color(0xFFFFFAF8),
    fontColor : Color = Color.Black,
    context: Context = LocalContext.current,
    click : () -> Unit = {},
    click2 : () -> Unit = {},
    click3 : () -> Unit = {},
    click4 : () -> Unit = {},
    click5 : () -> Unit = {},
    formatsNum : Int = 0,
    clickYh : () -> Unit = {},
    clickYs : () -> Unit = {},
    show : Boolean = true,
    aboutMe : () -> Unit = {}
) {
    var systemVersion =  context.packageManager.getPackageInfo(context.packageName, 0).versionName
    var versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode

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
        var code = remember {
            mutableStateOf("")
        }
        var express = remember {
            mutableStateOf("")
        }
        Column {
            Text(text = "关于我们", color = fontColor,
                fontSize = 35.sp,
                modifier = Modifier.fillMaxWidth(),

            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(
                id = R.drawable.dd),
                contentDescription = "logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(0.dp, 15.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth().background(
                color = subBackgroundColor,
                shape = MaterialTheme.shapes.medium
            ).padding(10.dp)
        ) {
            AnimatedVisibility(visible = show) {
                Column {
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                click()
                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "自定义取件码格式", color = fontColor, fontSize = 20.sp)
                            Text(text = "$formatsNum 个", color = fontColor, fontSize = 15.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                click2()
                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "驿站列表", color = fontColor, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                click3()
                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "自定义快递", color = fontColor, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                click4()

                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "更多设置", color = fontColor, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                click5()

                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "检查更新", color = fontColor, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                clickYh()

                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "用户协议", color = fontColor, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                clickYs()

                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "隐私政策", color = fontColor, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(
                        color = Color.LightGray
                    ))
                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = subBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(
                                0.dp, 15.dp
                            ).clickable {
                                aboutMe()

                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "关于我们", color = fontColor, fontSize = 20.sp)
                        }
                    }
                }
            }
            AnimatedVisibility(visible = !show) {
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
                    Text(text = "预览模式不支持自定义设置", color = fontColor,
                        modifier = Modifier.padding(
                            10.dp
                        ),
                        fontSize = 20.sp)
                }
            }
        }


        // 时间选择器



    }
}