package cn.tw.sar.note

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.CheckBox
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.PermissionUtils

class AgentActivity : ComponentActivity() {
    var isHaveFlout = mutableStateOf(false)
    var isHavePermission = mutableStateOf(false)
    var permissionsNow = PermissionUtils.permissions
    var isAgree = mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        isHaveFlout.value = PermissionUtils.checkShowOnLockScreen(this@AgentActivity)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsNow = listOf(
                "android.permission.RECEIVE_SMS",
                "android.permission.READ_SMS",
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.FOREGROUND_SERVICE_SPECIAL_USE",
            )
            for (i in 0 until permissionsNow.size){
                Log.d("MainActivity", "permissionsNow: ${permissionsNow[i]}")
            }
        } else {

        }
        isHavePermission.value = PermissionUtils.checkPermissions(this@AgentActivity, permissionsNow.toTypedArray())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isAllGranted = true
        for (i in 0 until permissions.size) {
            Log.d("MainActivity", "permissions: ${permissions[i]} grantResults: ${grantResults[i]}")
            // 如果所有权限都被授予
            if (grantResults[i] != 0) {
                isAllGranted = false
            }
        }
        if (isAllGranted) {
            // pages.value += 1
            Toast.makeText(this@AgentActivity, "权限已经被授予, 请继续点击确认", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults,deviceId)
        var isAllGranted = true
        for (i in 0 until permissions.size) {
            Log.d("MainActivity", "permissions: ${permissions[i]} grantResults: ${grantResults[i]}")
            // 如果所有权限都被授予
            if (grantResults[i] != 0) {
                isAllGranted = false
            }
        }
        if (isAllGranted) {
            // pages.value += 1
            Toast.makeText(this@AgentActivity, "权限已经被授予, 请继续点击确认", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("init", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .background(
                                Color.White
                            )
                            .fillMaxHeight()
                            .verticalScroll(
                                rememberScrollState()
                            )

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_public_privacy),
                                contentDescription = "public privacy",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "启用取件码读取", color = Color.Black, fontSize = 30.sp, modifier = Modifier.padding(10.dp))
                            Text(text = "我们需要的权限", color = Color.Black, fontSize = 20.sp, modifier = Modifier.padding(10.dp))
                            for (i in 0 until  PermissionUtils.permissions_usages.size){
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .background(
                                            Color(0xFFE0E0E0),
                                            shape = MaterialTheme.shapes.medium
                                        ),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = PermissionUtils.permissions_usages[i],
                                        color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(10.dp))
                                    Text(text = PermissionUtils.permissions_descriptions[i],
                                        color = Color.Black, fontSize = 15.sp,
                                        modifier = Modifier.padding(10.dp))

                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Checkbox(checked = isAgree.value,
                                    onCheckedChange = {
                                        isAgree.value = it
                                    }
                                )
                                val annotatedString3 = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                        append("我已经阅读并同意")
                                    }
                                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = Color.Blue)) {
                                        append("《轻笔记隐私协议》")
                                    }
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                        append("和")
                                    }
                                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = Color.Blue)) {
                                        append("《轻笔记用户协议》")
                                    }

                                    addStringAnnotation(
                                        tag = "URL",
                                        annotation = "file:///android_asset/ysxy.html",
                                        start = 13,
                                        end = 19
                                    )

                                    addStringAnnotation(
                                        tag = "URL",
                                        annotation = "file:///android_asset/yhxy.html",
                                        start = 23,
                                        end = 29
                                    )
                                }
                                ClickableText(text = annotatedString3, onClick = { integer ->
                                    annotatedString3.getStringAnnotations("URL", integer, integer).firstOrNull()?.let {
                                        // annotation.item 为链接的标记数据
                                        // annotation.start 为链接的起始位置

                                        if (it.item == "file:///android_asset/ysxy.html") {
                                            val intent = Intent(this@AgentActivity, PolicyActivity::class.java)
                                            intent.putExtra("urls", "file:///android_asset/ysxy.html")
                                            startActivity(intent)
                                        }
                                        else if (it.item == "file:///android_asset/yhxy.html") {
                                            val intent = Intent(this@AgentActivity, PolicyActivity::class.java)
                                            intent.putExtra("urls", "file:///android_asset/yhxy.html")
                                            startActivity(intent)
                                        }
                                    }
                                })

                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        finish()
                                    }
                            ) {
                                Text("拒绝", color = Color.Blue)
                            }
                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        if (isAgree.value) {
                                            if (!PermissionUtils.checkShowOnLockScreen(this@AgentActivity)) {
                                                // 权限请求成功
                                                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                                                Log.d("MainActivity", "start service")
                                                finish()
                                            }
                                            else {
                                                Log.d("MainActivity", "start service")
                                                if (PermissionUtils.checkPermissions(
                                                        this@AgentActivity,
                                                        permissionsNow.toTypedArray()
                                                    )
                                                ) {
                                                    Log.d("MainActivity", "start service2")
                                                    val editor = sharedPreferences.edit()
                                                    editor.putBoolean("agent", true)
                                                    editor.apply()
                                                    finish()
                                                } else {
                                                    Log.d("MainActivity", "start service3")
                                                    requestPermissions(
                                                        permissionsNow.toTypedArray(),
                                                        1
                                                    )
                                                }
                                            }
                                        } else {
                                            Toast.makeText(this@AgentActivity, "请先同意隐私协议", Toast.LENGTH_SHORT).show()
                                        }

                                    }
                            ) {
                                Text("同意并授权", color = Color.Blue)
                            }

                        }

                    }
                }
            }
        }
    }
}

