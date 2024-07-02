package cn.tw.sar.note

import UpdateDataBased
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.icu.text.IDNA
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.wear.compose.material.Picker
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.CItem
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.entity.CodeAndYz
import cn.tw.sar.note.entity.CodeFormat
import cn.tw.sar.note.pages.AboutItem
import cn.tw.sar.note.pages.AboutPage
import cn.tw.sar.note.pages.HomePage
import cn.tw.sar.note.pages.MyDialog
import cn.tw.sar.note.pages.OverPage
import cn.tw.sar.note.pages.SettingsPage
import cn.tw.sar.note.service.MessageService
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.PermissionUtils
import cn.tw.sar.note.utils.PickupCodeUtils
import cn.tw.sar.note.utils.demoAllNum
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import cn.tw.sar.note.utils.randomColor
import cn.tw.sar.note.utils.timeStempToTime
import kg.edu.yjut.litenote.miui.devicesSDK.isLandscape
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.line.LineChartData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread
import androidx.datastore.preferences.protobuf.Api
import cn.tw.sar.note.dao.UpdateDao
import android.icu.text.IDNA.Info
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.core.app.NotificationCompat
import cn.tw.sar.note.entity.NoteClass
import cn.tw.sar.note.entity.Notes
import cn.tw.sar.note.pages.MorePage
import cn.tw.sar.note.pages.NotePage
import cn.tw.sar.note.subscribe.SubscribeMainActivity
import cn.tw.sar.note.utils.LogUpdate
import cn.tw.sar.note.utils.isDarkMode
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import retrofit2.Call
import retrofit2.Response


class MainActivity : ComponentActivity() {
    var list  = mutableStateListOf<CodeAndYz>()
    var noteList = mutableStateListOf<Notes>()
    var notePage = mutableStateOf(1)
    var notePageSize = 15
    var noteClassList = mutableStateListOf<NoteClass>()
    var noteCurrClass = mutableStateOf(0L)

    var isAgent = mutableStateOf(false)

    var selectIndex =  mutableStateOf(0)
    var page : MutableState<Int> = mutableIntStateOf(1)
    var pageSize = 5
    var currMode = mutableStateOf(0)

    var expressNames = mutableStateListOf(
        "中通",
        "圆通",
        "百世",
        "申通",
        "邮政",
        "德邦",
        "韵达",
        "极兔",
        "京东",
        "顺丰",
    )

    var yzNames = mutableStateListOf<String>(
        "未知驿站"
    )
    var isShowYz = mutableStateOf(false)
    var editCode = mutableStateOf<Code?>(null)
    var isShowAdd = mutableStateOf(false)




    var labelList = listOf(
        "笔记",
        "取件码",
        "总览",
        "更多",
    )
    var iconList = listOf(
        Icons.Filled.Home,
        Icons.Filled.Notifications,
        Icons.Filled.DateRange,
        Icons.Filled.Menu,
    )
    fun loadNoteClass(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val noteDao = database.noteDao()
            val q = noteDao.getAllClass()
            Log.d("MainActivity", "noteClassList: $q")
            runOnUiThread {
                noteClassList.clear()
                noteClassList.addAll(q)
            }
        }
    }
    fun loadNote(

    ){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val noteDao = database.noteDao()
            val q = noteDao.getAllNotes((notePage.value-1)*notePageSize,notePageSize,noteCurrClass.value)
            Log.d("MainActivity", "noteList: $q")
            runOnUiThread {
                noteList.clear()
                // 简化内容为 100个字符
                for (item in q){
                    if (item.noteContent.length>100){
                        item.noteContent = item.noteContent.substring(0,100)
                    }
                    noteList.add(item)
                }

            }
        }
    }

    fun loadMoreNote(

    ){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val noteDao = database.noteDao()

            val q = noteDao.getAllNotes((notePage.value-1)*notePageSize,notePageSize,noteCurrClass.value)
            Log.d("MainActivity", "noteList: $q")
            runOnUiThread {
                if (q.size>0){
                    noteList.clear()
                    // 简化内容为 100个字符
                    for (item in q){
                        if (item.noteContent.length>100){
                            item.noteContent = item.noteContent.substring(0,100)
                        }
                        noteList.add(item)
                    }
                }else{
                    Toast.makeText(this@MainActivity, "没有更多了", Toast.LENGTH_SHORT).show()
                    if (notePage.value > 1) {
                        notePage.value -= 1
                    }
                }

            }
        }
    }
    fun  initDatabase(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val noteDao = database.noteDao()
            val q = noteDao.getClassCount(0L)
            if (q == 0){
                noteDao.insertClass(NoteClass(0,"默认分类",0,System.currentTimeMillis()))
            }

        }
    }
    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("init", MODE_PRIVATE)
        var isFirst = sharedPreferences.getBoolean("agent", false)
        val permissionsNow = listOf(
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_SMS",
            "android.permission.POST_NOTIFICATIONS",
            "android.permission.FOREGROUND_SERVICE_SPECIAL_USE",
        )
        // 判断是否有悬浮窗权限
        if (!PermissionUtils.checkShowOnLockScreen(this@MainActivity)) {
            // 权限请求成功
            Log.d("MainActivity", "start service")
        } else {
            Log.d("MainActivity", "start service")
            if (PermissionUtils.checkPermissions(
                    this@MainActivity,
                    permissionsNow.toTypedArray()
                )
            ) {
                // 请求成功
                isFirst = true
                sharedPreferences.edit().putBoolean("agent", true).apply()
            }
        }
        initDatabase()
        Log.d("MainActivity onResume", "isFirst: $isFirst")
        if (!isFirst) {
            isAgent.value = false
            Toast.makeText(this,
                "您未授权，目前无法自动识别短信中的验证码。",
                Toast.LENGTH_LONG).show()
        }else{
            isAgent.value = true
            var intent = Intent(this@MainActivity, MessageService::class.java)
            startForegroundService(intent)
            Log.d("MainActivity", "start service")
        }

        if (selectIndex.value == 1) {
            loadingList()
            loadingBar()
            loadCodeFormat()
            loadingStrList()
        } else if (selectIndex.value == 0) {
            loadNoteClass()
            loadNote()
        } else if (selectIndex.value == 2) {
            loadingBar()
            loadCodeFormat()
        } else if (selectIndex.value == 3) {
            loadingStrList()
            loadCodeFormat()
        }

    }
    fun loadingStrList(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val expressDao = database.expressDao()
            val formatDao = database.portsDao()

            val q = formatDao.getAllPostNames()
            val r = expressDao.getAllExpressStr()
            runOnUiThread {
                // 如果r的内容不在yzNames中，就添加
                r.forEach {
                    if (!expressNames.contains(it)){
                        expressNames.add(it)
                    }
                }

                q.forEach {
                    if (!yzNames.contains(it)){
                        yzNames.add(it)
                    }
                }
            }
        }
    }

    var allNums = mutableStateOf(0)
    var dqj = mutableStateOf(0) // 待取件
    var yqj = mutableStateOf(0) // 已取件
    var barPoint  = mutableStateListOf<CItem>()
    fun loadingBar(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val codeDao = database.codeDao()
            allNums.value = codeDao.count()
            dqj.value = codeDao.countByStatus(0)
            yqj.value = codeDao.countByStatus(1)
            barPoint.clear()
            // 统计30天内的数据
            val time = System.currentTimeMillis()
            val count = codeDao.countByTimeGroupByDay()
            runOnUiThread {
                barPoint.addAll(count)
            }

        }
    }
    fun loadingList(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val codeDao = database.codeDao()

            page.value = 1
            if (currMode.value == 0) {
                //(page.value - 1) * pageSize, pageSize
                val codeBins = codeDao.getAllByPosts()
                list.clear()
                for (item in codeBins) {

                    val yz = item.first
                    val num = item.second
                    var codes = codeDao.getAllsAndYz(
                        (page.value - 1) * pageSize, pageSize,yz
                    )
                    runOnUiThread {
                        list.add(CodeAndYz(yz = yz, num = num, codes = codes))
                    }
                }

            }else if (currMode.value == 1) {
                val codeBins = codeDao.getAllByPostsAndStatus(1)
                list.clear()
                for (item in codeBins) {
                    val yz = item.first
                    val num = item.second
                    var codes = codeDao.getAllByStatusAndYz(
                        1,(page.value - 1) * pageSize, pageSize,yz
                    )
                    runOnUiThread {
                        list.add(CodeAndYz(yz = yz, num = num, codes = codes))
                    }
                }
            } else if (currMode.value == 2) {
                val codeBins = codeDao.getAllByPostsAndStatus(0)

                list.clear()
                for (item in codeBins) {
                    val yz = item.first
                    val num = item.second
                    var codes = codeDao.getAllByStatusAndYz(
                        0,(page.value - 1) * pageSize, pageSize,yz
                    )
                    runOnUiThread {
                        list.add(CodeAndYz(yz = yz, num = num, codes = codes))
                    }
                }
            }
        }
    }

    fun loadingMoreList(page:Int,yz:String){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val codeDao = database.codeDao()
            var newList = mutableListOf<CodeAndYz>()
            if (currMode.value == 0) {
                //(page.value - 1) * pageSize, pageSize
                for (item in list) {
                    val cyz = item.yz
                    if (cyz === yz){
                        val num = item.num
                        var codes = codeDao.getAllsAndYz(
                            (page - 1) * pageSize, pageSize,yz
                        )
                        item.codes.addAll(codes)
                        runOnUiThread {
                            newList.add(CodeAndYz(yz = yz, num = num, codes = item.codes))
                        }
                    }else{
                        runOnUiThread {
                            newList.add(item)
                        }
                    }
                }

            }
            else if (currMode.value == 1) {
                for (item in list) {
                    val cyz = item.yz
                    if (cyz === yz){
                        val num = item.num
                        var codes = codeDao.getAllByStatusAndYz(
                           1, (page - 1) * pageSize, pageSize,yz
                        )
                        item.codes.addAll(codes)
                        runOnUiThread {
                            newList.add(CodeAndYz(yz = yz, num = num, codes = item.codes))
                        }
                    }else{
                        runOnUiThread {
                            newList.add(item)
                        }
                    }
                }
            }
            else if (currMode.value == 2) {
                for (item in list) {
                    val cyz = item.yz
                    if (cyz === yz){
                        val num = item.num
                        var codes = codeDao.getAllByStatusAndYz(
                            0, (page - 1) * pageSize, pageSize,yz
                        )
                        item.codes.addAll(codes)
                        runOnUiThread {
                            newList.add(CodeAndYz(yz = yz, num = num, codes = item.codes))
                        }
                    }else{
                        runOnUiThread {
                            newList.add(item)
                        }
                    }
                }
            }
            runOnUiThread {
                list.clear()
                list.addAll(newList)
            }
        }
    }
    var formatsNum = mutableStateOf(0)
    fun loadCodeFormat(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@MainActivity)
            val formatDao = database.formatDao()
            val q = formatDao.getCount()
            runOnUiThread {
                formatsNum.value = q
            }
        }
    }


    var showDialog = mutableStateOf(false)
    var deleteID = mutableStateOf(0L)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 请求网络权限
        val netList = arrayOf(
            "android.permission.INTERNET",
        )
        if (!PermissionUtils.checkPermissions(this, netList)){
            PermissionUtils.requestPermissions(this, netList,99)
        }


        enableEdgeToEdge()
        setContent {
            rememberSystemUiController().run {
                setNavigationBarColor(Color.Transparent,
                    darkIcons = isDarkMode(this@MainActivity),
                    navigationBarContrastEnforced = false
                )
                setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !isDarkMode(this@MainActivity),
                    isNavigationBarContrastEnforced = false
                )
            }
            LiteNoteTheme {
                Column {
                    var fontColor = getDarkModeTextColor(this@MainActivity)
                    var widthDp = Resources.getSystem().displayMetrics.heightPixels / Resources.getSystem().displayMetrics.density

                    MyDialog(
                        onDismissRequest = {
                            if (isShowAdd.value) {
                                isShowAdd.value = false
                            }
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            usePlatformDefaultWidth = false
                        ),
                        showDialog = isShowAdd.value,
                        fontColor = fontColor,
                        subBackgroundColor = getDarkModeBackgroundColor(
                            this@MainActivity, 1
                        ),
                        content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(
                                        (widthDp * 3 / 4).dp
                                    )
                                    .padding(10.dp)

                            ) {
                                Text(
                                    text = "添加", color = fontColor,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "添加取件码", color = fontColor,
                                    fontSize = 15.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                val isClick = remember { mutableStateOf(false) }
                                val selectType = remember { mutableStateOf( "") }
                                val selectType2 = remember { mutableStateOf( "") }
                                val isClick2 = remember { mutableStateOf(false) }
                                val edit  = remember { mutableStateOf( "") }
                                val code  = remember { mutableStateOf( "") }

                                TextField(
                                    value = edit.value,
                                    onValueChange = {
                                        edit.value = it
                                    },
                                    placeholder = {
                                        Text("请输入短信内容")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    maxLines = 4
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (edit.value!=""){
                                            var code2 = PickupCodeUtils.getOnePickedCode(edit.value.toString(),this@MainActivity)
                                            var company = PickupCodeUtils.getPickupCompany(edit.value.toString(),this@MainActivity)
                                            var yz = PickupCodeUtils.getPickupYz(edit.value.toString(),this@MainActivity)
                                            if (code2!=""){
                                                code.value = code2
                                                selectType2.value = company
                                                selectType.value = yz
                                            }else{
                                                Toast.makeText(this@MainActivity,"未解析到取件码", Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            Toast.makeText(this@MainActivity,"请先输入短信", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(text = "解析")
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                TextField(
                                    value = code.value,
                                    onValueChange = {
                                        code.value = it
                                    },
                                    placeholder = {
                                        Text("请输入取件码内容")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "快递驿站：")
                                        Row(
                                            modifier = Modifier.clickable {
                                                isClick.value = !isClick.value
                                            }
                                        ){
                                            Text(text = selectType.value)
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = isClick.value,
                                        modifier = Modifier.width(200.dp),
                                        onDismissRequest = {
                                            isClick.value = !isClick.value

                                        },
                                        content = {
                                            yzNames.forEach {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        isClick.value = !isClick.value
                                                        selectType.value = it
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
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "快递公司：")
                                        Row(
                                            modifier = Modifier.clickable {
                                                isClick2.value = !isClick2.value
                                            }
                                        ){
                                            Text(text = selectType2.value)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = isClick2.value,
                                        modifier = Modifier.width(200.dp),
                                        onDismissRequest = {
                                            isClick2.value = !isClick2.value
                                        },
                                        content = {
                                            expressNames.forEach {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        isClick2.value = !isClick2.value
                                                        selectType2.value = it
                                                    },
                                                    text = {
                                                        Text(text = it)
                                                    }
                                                )
                                            }
                                        },
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        val database: CodeDatabase =
                                            CodeDatabase.getDatabase(this@MainActivity)
                                        val codeDao = database.codeDao()
                                        thread{
                                            val codeBin = codeDao.getByCode(code.value)
                                            Log.d("codeBin", codeBin.toString())
                                            if (codeBin == null) {
                                                var strDay = System.currentTimeMillis()
                                                val yzStr = if (selectType.value=="") "未知驿站" else selectType.value
                                                val kdStr = if (selectType2.value=="") "未知" else selectType2.value
                                                codeDao.insert(
                                                    Code(
                                                        0,
                                                        code.value,
                                                        yz = yzStr,
                                                        kd = kdStr,
                                                        0,
                                                        0,
                                                        0,
                                                        strDay,
                                                        cn.tw.sar.note.utils.timeStempToTime(strDay,2),
                                                        cn.tw.sar.note.utils.timeStempToTime(strDay,1),
                                                        cn.tw.sar.note.utils.timeStempToTime(strDay,3)

                                                    )
                                                )
                                                runOnUiThread {
                                                    loadingList()
                                                    isShowAdd.value = false

                                                }
                                            }
                                        }


                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(text = "保存")

                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    MyDialog(
                        onDismissRequest = {
                            if (isShowYz.value) {
                                isShowYz.value = false
                                editCode.value = null
                            }
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            usePlatformDefaultWidth = false
                        ),
                        showDialog = isShowYz.value,
                        fontColor = fontColor,
                        subBackgroundColor = getDarkModeBackgroundColor(
                            this@MainActivity, 1
                        ),
                        content = {
                            if (editCode.value!=null){
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)

                                ) {
                                    Text(
                                        text = "修改", color = fontColor,
                                        fontSize = 20.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "修改"+editCode.value!!.code+"", color = fontColor,
                                        fontSize = 15.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val isClick = remember { mutableStateOf(false) }
                                    val selectType = remember { mutableStateOf( editCode.value!!.yz) }
                                    val selectType2 = remember { mutableStateOf( editCode.value!!.kd) }
                                    val isClick2 = remember { mutableStateOf(false) }
                                    val code  = remember { mutableStateOf( editCode.value!!.code) }

                                    TextField(
                                        value =  code.value,
                                        onValueChange = {
                                            code.value = it
                                            editCode.value!!.code = it
                                        },
                                        placeholder = {
                                            Text("请输入取件码内容")
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "修改快递驿站：")
                                            Row(
                                                modifier = Modifier.clickable {
                                                    isClick.value = !isClick.value
                                                }
                                            ){
                                                Text(text = selectType.value)
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = isClick.value,
                                            modifier = Modifier.width(200.dp),
                                            onDismissRequest = {
                                                isClick.value = !isClick.value

                                            },
                                            content = {
                                                yzNames.forEach {
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            isClick.value = !isClick.value
                                                            selectType.value = it
                                                            editCode.value!!.yz = it
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
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "修改快递公司：")
                                            Row(
                                                modifier = Modifier.clickable {
                                                    isClick2.value = !isClick2.value
                                                }
                                            ){
                                                Text(text = selectType2.value)
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = isClick2.value,
                                            modifier = Modifier.width(200.dp),
                                            onDismissRequest = {
                                                isClick2.value = !isClick2.value
                                            },
                                            content = {
                                                expressNames.forEach {
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            isClick2.value = !isClick2.value
                                                            selectType2.value = it
                                                            editCode.value!!.kd = it
                                                        },
                                                        text = {
                                                            Text(text = it)
                                                        }
                                                    )
                                                }
                                            },
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            thread {
                                                val database: CodeDatabase =
                                                    CodeDatabase.getDatabase(this@MainActivity)
                                                val codeDao = database.codeDao()
                                                codeDao.update(editCode.value!!)
                                                editCode.value==null
                                                page.value = 1
                                                runOnUiThread {
                                                    loadingList()
                                                    editCode.value = null
                                                    Toast.makeText(this@MainActivity, "成功", Toast.LENGTH_SHORT).show()
                                                    isShowYz.value =  false

                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(text = "保存")

                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
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
                            this@MainActivity, 1
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
                                        showDialog.value = false
                                    }) {
                                        Text(text = "取消", color = fontColor)
                                    }
                                    Button(onClick = {
                                        showDialog.value = false
                                        //onDelete(format.id)
                                        if (deleteID.value != 0L) {
                                            thread {
                                                val database: CodeDatabase =
                                                    CodeDatabase.getDatabase(this@MainActivity)
                                                val codeDao = database.codeDao()
                                                codeDao.deleteById(deleteID.value)
                                                page.value = 1
                                                runOnUiThread {
                                                    loadingList()
                                                    deleteID.value = 0L
                                                    Toast.makeText(this@MainActivity, "删除成功", Toast.LENGTH_SHORT).show()

                                                }
                                            }
                                        } else {
                                            Toast.makeText(this@MainActivity, "删除失败", Toast.LENGTH_SHORT).show()
                                        }

                                    }) {
                                        Text(text = "确定", color = fontColor)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(visible = !isLandscape(this@MainActivity)) {
                            // 底部栏
                            // 获取系统导航栏高度

                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(
                                            getDarkModeBackgroundColor(
                                                this@MainActivity, 1
                                            )
                                        )
                                ) {


                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        labelList.forEachIndexed { index, label ->
                                            IconButton(
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .fillMaxHeight(),

                                                onClick = {
                                                    selectIndex.value = index
                                                    if (selectIndex.value == 1) {
                                                        loadingBar()
                                                        loadCodeFormat()
                                                        loadingStrList()
                                                    } else if (selectIndex.value == 0) {
                                                        loadNoteClass()
                                                        loadNote()
                                                    } else if (selectIndex.value == 2) {
                                                        loadingBar()
                                                        loadCodeFormat()
                                                    } else if (selectIndex.value == 3) {
                                                        loadingStrList()
                                                        loadCodeFormat()
                                                    }
                                                    print("selectIndex = ${selectIndex.value}")

                                                }
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                ) {
                                                    Icon(
                                                        imageVector = iconList[index],
                                                        contentDescription = label,

                                                        tint = if (selectIndex.value == index)
                                                            MaterialTheme.colorScheme.primary
                                                        else getDarkModeTextColor(this@MainActivity)
                                                    )
                                                    Text(
                                                        text = label,
                                                        fontSize = 8.sp,
                                                        color = getDarkModeTextColor(this@MainActivity)
                                                    )
                                                }

                                            }

                                        }
                                    }
                                    if (selectIndex.value == 0 || selectIndex.value == 1 || selectIndex.value == 2) {
                                        IconButton(
                                            onClick = {
                                                if (selectIndex.value == 1) {
                                                    /// 刷新
                                                    loadingList()

                                                } else if (selectIndex.value == 3) {
                                                    loadingBar()
                                                }
                                            },
                                            modifier = Modifier
                                                .padding(
                                                    end = 20.dp,
                                                    start = 5.dp,
                                                    top = 10.dp,
                                                    bottom = 5.dp
                                                )
                                                .size(60.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.shapes.extraLarge
                                                )

                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Refresh,
                                                contentDescription = "add",
                                                tint = Color.White,
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.navigationBarsPadding())
                            }

                        }
                    },

                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            getDarkModeBackgroundColor(
                                this@MainActivity, 0
                            )
                        )) { innerPadding ->
                    Column(

                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        when (selectIndex.value) {
                            0 -> {
                                NotePage(
                                    backgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 0
                                    ),
                                    subBackgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 1
                                    ),
                                    fontColor = getDarkModeTextColor(this@MainActivity),
                                    list = noteList,
                                    classLists = noteClassList,
                                    onSeleted = { id ->
                                        Log.d("MainActivity", "onSeleted $id")
                                        noteCurrClass.value = noteClassList[id].id
                                        notePage.value = 1
                                    },


                                    onAddClick = {
                                       val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
                                        startActivity(intent)
                                    },
                                    isagent = isAgent.value,
                                    getAgent = {
                                        val intent = Intent(this@MainActivity, AgentActivity::class.java)
                                        startActivity(intent)
                                    },
                                    clickItem = {
                                        val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
                                        intent.putExtra("id",noteList[it].id)
                                        startActivity(intent)
                                    },
                                    loadMore = {
                                        notePage.value +=1
                                        loadMoreNote()
                                    }
                                )
                            }
                            1 -> {
                                HomePage(
                                    backgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 0
                                    ),
                                    subBackgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 1
                                    ),
                                    fontColor = getDarkModeTextColor(this@MainActivity),
                                    list = list,
                                    onClickDots = { yid, id, currStatus ->
                                        thread {
                                            val database: CodeDatabase =
                                                CodeDatabase.getDatabase(this@MainActivity)
                                            val codeDao = database.codeDao()
                                            val codeBin = codeDao.getById(list[yid].codes[id].id)
                                            if (codeBin != null) {
                                                val status = if (codeBin.status == 0) 1 else 0
                                                codeDao.updateStatusById(list[yid].codes[id].id, status)
                                                page.value = 1
                                                runOnUiThread {
                                                    loadingList()

                                                }
                                            }
                                        }
                                    },
                                    onSeleted = { id ->
                                        Log.d("MainActivity", "onSeleted $id")
                                        currMode.value = id
                                        loadingList()
                                    },
                                    loadMore = { pid,page->
                                        if (page == -1){
                                            Toast.makeText(this@MainActivity, "没有更多数据了", Toast.LENGTH_SHORT).show()
                                        }else{
                                            loadingMoreList(page,list[pid].yz)
                                        }

                                    },
                                    onDeleteDots = { yid,index, state ->
                                        showDialog.value = true
                                        deleteID.value = list[yid].codes[index].id
                                    },
                                    onEditCode = {
                                        isShowYz.value = true
                                        editCode.value = it
                                    },
                                    onAddClick = {
                                        isShowAdd.value=!isShowAdd.value
                                    },
                                    isagent = isAgent.value,
                                    getAgent = {
                                        val intent = Intent(this@MainActivity, AgentActivity::class.java)
                                        startActivity(intent)
                                    }
                               )
                            }
                            2 -> {
                                OverPage(
                                    backgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 0
                                    ),
                                    subBackgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 1
                                    ),
                                    fontColor = getDarkModeTextColor(this@MainActivity),
                                    hots = barPoint,
                                    allNums = listOf(
                                        allNums.value,
                                        dqj.value,
                                        yqj.value
                                    ),
                                    onclickDots = {timeStart,timeEnd->
                                        Log.d("MainActivity", "timeStart = $timeStart timeEnd = $timeEnd")
                                    },

                                    )
                            }
                            3 -> {
                                AboutPage(
                                    backgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 0
                                    ),
                                    subBackgroundColor = getDarkModeBackgroundColor(
                                        this@MainActivity, 1
                                    ),
                                    fontColor = getDarkModeTextColor(this@MainActivity),
                                    context = this@MainActivity,
                                    formatsNum = formatsNum.value,
                                    click = {
                                        val intent = Intent(this@MainActivity, CodeFormatActivity::class.java)
                                        startActivity(intent)
                                    },
                                    show=true,
                                    click2 = {
                                        val intent = Intent(this@MainActivity, PortsActivity::class.java)
                                        startActivity(intent)
                                    },
                                    click3 = {
                                        val intent = Intent(this@MainActivity, ExpressActivity::class.java)
                                        startActivity(intent)
                                    },
                                    click4 = {
                                        val intent = Intent(this@MainActivity, MoreSettingsActivity2::class.java)
                                        startActivity(intent)
                                    },
                                    click5 = {
                                        val intent = Intent(this@MainActivity, CheckUpdateActivity::class.java)
                                        startActivity(intent)
                                    },
                                    clickYh = {
                                        val intent = Intent(this@MainActivity, PolicyActivity::class.java)
                                        intent.putExtra("urls","file:///android_asset/yhxy.html")

                                        startActivity(intent)
                                    },
                                    clickYs = {
                                        val intent = Intent(this@MainActivity, PolicyActivity::class.java)
                                        intent.putExtra("urls","file:///android_asset/ysxy.html")
                                        startActivity(intent)
                                    },
                                    aboutMe = {
                                        val intent = Intent(this@MainActivity, AboutActivity::class.java)
                                        startActivity(intent)
                                    },
                                    clickDy = {
                                        val intent = Intent(this@MainActivity, SubscribeMainActivity::class.java)
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (PermissionUtils.checkPermissions(this, PermissionUtils.permissions.toTypedArray())) {
            // 权限请求成功
            var intent = Intent(this@MainActivity, MessageService::class.java)
            startForegroundService(intent)
        }

    }
}

