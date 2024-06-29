package cn.tw.sar.note

import UpdateData
import UpdateDataBased
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.glance.Button
import cn.tw.sar.note.dao.UpdateDao
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class CheckUpdateActivity : ComponentActivity() {
    var verCode = mutableStateOf(0)
    var verName = mutableStateOf("0.0.0")
    var versionInfo = mutableStateOf<UpdateData?>(null)

    fun onLoadingVersion(context: Context){
        // 获取版本code
        val versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        verCode.value = versionCode
        // 获取版本name
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        if (versionName != null) {
            verName.value = versionName
        }
        //构建Retrofit实例
        val mRetrofit =  Retrofit.Builder()
            //设置网络请求BaseUrl地址
            .baseUrl("https://api.wxd2zrx.top/")
            //设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        thread {
            val api: UpdateDao = mRetrofit.create(UpdateDao::class.java)
            val dataCall: Call<UpdateDataBased>? = api.getUpdateInfo("note",versionCode);
            if (dataCall != null) {
                val response: Response<UpdateDataBased> = dataCall.execute()
                if (response.isSuccessful) {
                    val data: UpdateDataBased? = response.body()
                    if (data != null) {
                        if (data.data!=null){
                            // 发送通知,提示用户更新
                            runOnUiThread {
                                versionInfo.value = data.data
                            }

                        }
                    }
                }
            }
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLoadingVersion(this)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .background(
                                color = Color.Transparent
                            )
                            .padding(
                                25.dp
                            )
                    ) {
                        var fontColor = getDarkModeTextColor(this@CheckUpdateActivity)
                        var subBackgroundColor = getDarkModeBackgroundColor(
                            context = this@CheckUpdateActivity, level = 1)

                        Column {
                            Column {
                                Text(text = "检查更新", color = fontColor,
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
                            var showDetail = remember {
                                mutableStateOf(false)
                            }
                            if (versionInfo.value != null) {

                                if (showDetail.value) {
                                    if (versionInfo.value != null) {
                                        Text(text = "v${versionInfo.value!!.apk_size}", color = fontColor,
                                            fontSize = 20.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)
                                        Spacer(modifier = Modifier.height(15.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = subBackgroundColor,
                                                    shape = MaterialTheme.shapes.medium
                                                ).clickable {
                                                    showDetail.value = false
                                                }
                                                .padding(10.dp)
                                        ) {
                                            Text(text = versionInfo.value!!.version_info, color = fontColor,
                                                fontSize = 15.sp,
                                                modifier = Modifier.fillMaxWidth(),
                                                fontWeight = FontWeight.Black,
                                                textAlign = TextAlign.Start
                                            )

                                        }
                                    }


                                } else{

                                    Text(text = "v${verName.value}", color = fontColor,
                                        fontSize = 20.sp,
                                        modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(text = "检查到新版本", color = Color.Blue,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                showDetail.value = true
                                            },
                                        fontWeight = FontWeight.Black, textAlign = TextAlign.Center
                                    )
                                }

                            }else{
                                Text(text = "v${verName.value}", color = fontColor,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(text = "当前已是最新版本", color = Color.Blue,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = FontWeight.Black, textAlign = TextAlign.Center
                                )


                            }
                        }
                        // 时间选择器
                        if (versionInfo.value != null){
                            androidx.compose.material3.Button(onClick = {
                                // 打开浏览器
                                val intent = Intent()
                                intent.action = "android.intent.action.VIEW"
                                intent.data = android.net.Uri.parse(versionInfo.value!!.apk_url)
                                startActivity(intent)

                            }) {
                                Text(text = "去更新", color = Color.White,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = FontWeight.Black, textAlign = TextAlign.Center
                                )
                            }
                        }else{
                            androidx.compose.material3.Button(onClick = {
                                onLoadingVersion(this@CheckUpdateActivity)
                            }) {
                                Text(text = "检查更新", color = Color.White,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = FontWeight.Black, textAlign = TextAlign.Center
                                )
                            }
                        }



                    }
                }
            }
        }
    }
}

