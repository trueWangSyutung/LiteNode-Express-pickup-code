package cn.tw.sar.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.note.pages.AboutItem
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor

class AboutActivity : ComponentActivity() {

    var list = listOf<AboutItem>(

    )

    override fun onResume() {
        super.onResume()
        var systemVersion =  packageManager.getPackageInfo(packageName, 0).versionName
        var versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
        list = listOf(
            AboutItem("版本",   "v$systemVersion($versionCode)"),
            AboutItem("作者", "爱写BUG的小王"),
            AboutItem("邮箱", "wangxudong@yjut.edu.kg"),
            AboutItem("网站", "github.com/trueWangsyutung"),
            AboutItem("Github", "LiteNode-Express-pickup-code"),
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var fontColor = getDarkModeTextColor(this@AboutActivity)
                    var subBackgroundColor = getDarkModeBackgroundColor(context = this@AboutActivity, level = 1)
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = subBackgroundColor,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(10.dp)
                        ) {
                            list.forEach {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            0.dp, 15.dp
                                        )
                                        .clickable {
                                            if (it.title == "用户协议") {

                                            } else if (it.title == "隐私政策") {

                                            }
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = it.title, color = fontColor, fontSize = 20.sp)
                                    Text(text = it.content, color = fontColor, fontSize = 15.sp)
                                }
                                //如果不是最后一个元素，添加分割线
                                if (it != list.last()) {
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(0.5.dp)
                                        .background(
                                            color = Color.LightGray
                                        ))
                                }
                            }

                        }



                        // 时间选择器



                    }
                }
            }
        }
    }
}
