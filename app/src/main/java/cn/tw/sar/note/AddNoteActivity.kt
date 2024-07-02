package cn.tw.sar.note

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.NoteClass
import cn.tw.sar.note.entity.Notes
import cn.tw.sar.note.pages.MyDialog
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlin.concurrent.thread


class AddNoteActivity : ComponentActivity() {
    private val title = mutableStateOf("")
    var noteClassList = mutableStateListOf<NoteClass>()
    var selectIndex = mutableStateOf(0)
    var timeCurr = mutableStateOf("")
    var fontNum = mutableStateOf(0)
    var paras = mutableStateOf(
        ""
    )
    var editMode = mutableStateOf(0)
    var editNote  = mutableStateOf<Notes?>(null)

    fun loadNoteClass(){
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@AddNoteActivity)
            val noteDao = database.noteDao()
            val q = noteDao.getAllClass()
            Log.d("MainActivity", "noteClassList: $q")
            runOnUiThread {
                noteClassList.clear()
                noteClassList.addAll(q)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 获取当前时间
        val time = System.currentTimeMillis()
        val timeFORMAT = "yyyy-MM-dd HH:mm"
        val timeStr = android.text.format.DateFormat.format(timeFORMAT, time).toString()
        timeCurr.value = timeStr
    }
    fun loadNote(id: Long) {
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@AddNoteActivity)
            val noteDao = database.noteDao()
            val q = noteDao.getNoteById(id)
            Log.d("MainActivity", "noteClassList: $q")
            runOnUiThread {
                Log.d("MainActivity", "noteClassList: $q")
                title.value = q.noteTitle
                paras.value = q.noteContent
                selectIndex.value = noteClassList.indexOfFirst { it.id == q.classID }
                Log.d("MainActivity", "noteClassList: ${selectIndex.value}")

                editNote.value = q
                editMode.value = 1
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(this@AddNoteActivity)
            val noteDao = database.noteDao()
            val q = noteDao.getAllClass()
            Log.d("MainActivity", "noteClassList: $q")
            runOnUiThread {
                noteClassList.clear()
                noteClassList.addAll(q)
                if (intent.hasExtra("id")) {
                    loadNote(intent.getLongExtra("id", 0))
                }
            }
        }

        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()

                    ) {
                        var classOpen = remember {
                            mutableStateOf(false)
                        }
                        var showClass = listOf(
                            "Edit", "Preview"
                        )
                        var showMode = remember {
                            mutableStateOf(0)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.9f)
                                .verticalScroll(
                                    rememberScrollState()
                                ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.4f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .background(
                                            color = getDarkModeBackgroundColor(
                                                context = this@AddNoteActivity,
                                                level = 1
                                            ),
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .padding(15.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (noteClassList.size>0){
                                        Column(
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                // Text(text = "快递公司：")
                                                Row(
                                                    modifier = Modifier.clickable {
                                                        classOpen.value = !classOpen.value
                                                    }
                                                ){
                                                    Text(text = noteClassList[selectIndex.value].className)
                                                }

                                                Button(
                                                    onClick = {
                                                        // 插入Notes
                                                        // 检查是否有标题
                                                        if (editMode.value ==0){
                                                            if (title.value.isEmpty()) {
                                                                title.value = "无标题的笔记"
                                                            }
                                                            thread {
                                                                val database: CodeDatabase =
                                                                    CodeDatabase.getDatabase(this@AddNoteActivity)
                                                                val noteDao = database.noteDao()
                                                                noteDao.insertNote(
                                                                    cn.tw.sar.note.entity.Notes(
                                                                        classID = noteClassList[selectIndex.value].id,
                                                                        noteTitle = title.value,
                                                                        noteContent = paras.value,
                                                                        insertTime = System.currentTimeMillis(),
                                                                        updateTime = System.currentTimeMillis(),
                                                                        noteState = 0
                                                                    )
                                                                )
                                                            }
                                                            finish()
                                                        }else{
                                                            if (title.value.isEmpty()) {
                                                                title.value = "无标题的笔记"
                                                            }
                                                            thread {
                                                                val database: CodeDatabase =
                                                                    CodeDatabase.getDatabase(this@AddNoteActivity)
                                                                val noteDao = database.noteDao()
                                                                if (editNote.value != null) {
                                                                    editNote.value!!.classID = noteClassList[selectIndex.value].id
                                                                    editNote.value!!.noteTitle = title.value
                                                                    editNote.value!!.noteContent = paras.value
                                                                    editNote.value!!.updateTime = System.currentTimeMillis()

                                                                    noteDao.updateNotes(
                                                                        editNote.value!!
                                                                    )
                                                                }

                                                            }
                                                            finish()
                                                        }
                                                    },
                                                ) {
                                                    Text("保存")
                                                }
                                            }

                                            DropdownMenu(
                                                expanded = classOpen.value,
                                                modifier = Modifier.width(200.dp),
                                                onDismissRequest = {
                                                    classOpen.value = !classOpen.value
                                                },
                                                content = {
                                                    noteClassList.forEach {
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                classOpen.value = !classOpen.value
                                                                selectIndex.value =  noteClassList.indexOf(it)
                                                            },
                                                            text = {
                                                                Text(text = it.className)
                                                            }
                                                        )
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                                TextField(
                                    value = title.value,
                                    onValueChange = {
                                        try {
                                            title.value = it
                                        } catch (e: Exception) {
                                            return@TextField
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            "标题", textAlign = TextAlign.Start,
                                            fontSize = 40.sp,
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    ),
                                    textStyle = TextStyle(
                                        textAlign = TextAlign.Start,
                                        fontSize = 40.sp,

                                        ),
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 1,
                                    colors = TextFieldDefaults.colors(
                                        disabledTextColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedLabelColor = Color.Transparent,

                                        ),
                                    shape =  MaterialTheme.shapes.medium
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = timeCurr.value)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(text = "字数："+fontNum.value)
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val leadingIcon: @Composable () -> Unit = {
                                        Icon(Icons.Default.Check, null) }
                                    showClass.forEachIndexed { index, s ->

                                        FilterChip(
                                            selected = index ==  showMode.value,
                                            onClick = {
                                                showMode.value = index
                                            },
                                            label = { Text(s) },
                                            leadingIcon = if (index == showMode.value) leadingIcon else null
                                        )

                                        Spacer(modifier = Modifier.width(5.dp))
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(1f)
                            ){
                                if (showMode.value == 0) {
                                    TextField(
                                        value = paras.value,
                                        onValueChange = {
                                            // 判断输入是否含有换行符
                                            try {
                                                paras.value = it
                                                fontNum.value = it.length
                                            } catch (e: Exception) {
                                                return@TextField
                                            }

                                        },


                                        placeholder = {
                                            Text(
                                                "点击开始输入吧", textAlign = TextAlign.Start,
                                                fontSize = 20.sp,
                                            )
                                        },

                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Text,
                                        ),
                                        textStyle = TextStyle(
                                            textAlign = TextAlign.Start,
                                            fontSize = 20.sp,
                                        ),
                                        maxLines = 999,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(500.dp),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedLabelColor = Color.Transparent,

                                            ),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                } else {
                                    MarkdownText(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        markdown = paras.value,
                                        maxLines = 999,
                                        style = TextStyle(
                                            color = getDarkModeTextColor(this@AddNoteActivity),
                                            fontSize = 12.sp,
                                            lineHeight = 10.sp,
                                            textAlign = TextAlign.Justify,
                                        ),
                                    )
                                }
                            }
                        }
                        var tools = listOf(
                            "H1", "H2", "H3", "H4"
                        )
                        val htitleShow = remember {
                            mutableStateOf(false)
                        }
                        var mode = remember {
                            mutableStateOf(0)
                        }
                        var halfDialog = remember {
                            mutableStateOf(false)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(1f)
                                .padding(15.dp)
                                .horizontalScroll(
                                    rememberScrollState()
                                ).background(
                                    color = getDarkModeBackgroundColor(
                                        context = this@AddNoteActivity,
                                        level = 1
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        htitleShow.value = !htitleShow.value
                                    }
                                ){
                                    Text(text = "标题")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        mode.value = 4
                                        halfDialog.value = true
                                    }
                                ){
                                    Text(text = "斜体")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        mode.value = 5
                                        halfDialog.value = true
                                    }
                                ){
                                    Text(text = "·")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        mode.value = 6
                                        halfDialog.value = true
                                    }
                                ){
                                    Text(text = "粗体")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        mode.value = 7
                                        halfDialog.value = true
                                    }
                                ){
                                    Text(text = "代码块")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        mode.value = 8
                                        halfDialog.value = true
                                    }
                                ){
                                    Text(text = "超链接")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Text(text = "快递公司：")
                                Row(
                                    modifier = Modifier.clickable {
                                        mode.value = 9
                                        halfDialog.value = true
                                    }
                                ){
                                    Text(text = "图片")
                                }
                            }
                        }



                        DropdownMenu(
                            expanded = htitleShow.value,
                            modifier = Modifier.width(200.dp),
                            onDismissRequest = {
                                htitleShow.value = !htitleShow.value
                            },
                            offset = DpOffset(0.dp,0.dp),
                            content = {
                                tools.forEach {
                                    DropdownMenuItem(
                                        onClick = {
                                            htitleShow.value = !htitleShow.value
                                            mode.value = tools.indexOf(it)
                                            halfDialog.value = true
                                        },
                                        text = {
                                            Text(text = it)
                                        }
                                    )
                                }
                            },
                        )
                        var modeStr = when (mode.value) {
                            0 -> "一级标题"
                            1 -> "二级标题"
                            2 -> "三级标题"
                            3 -> "四级标题"
                            4 -> "斜体"
                            5 -> "·"
                            6 -> "加粗"
                            7 -> "代码块"
                            8 -> "超链接"
                            9 -> "图片（目前只支持在线图片）"
                            10 -> "公式块"
                            else -> ""
                        }
                        var modeStrDesp = when (mode.value) {
                            9 -> "（目前只支持在线图片）"
                            else -> ""
                        }
                        MyDialog(
                            onDismissRequest = {
                                if (halfDialog.value) {
                                    halfDialog.value = false
                                }
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                usePlatformDefaultWidth = false
                            ),
                            showDialog = halfDialog.value,
                            fontColor = getDarkModeTextColor(this@AddNoteActivity),
                            subBackgroundColor = getDarkModeBackgroundColor(
                                this@AddNoteActivity, 1
                            ),
                            content = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)

                                ) {
                                    Text(
                                        text = "添加"+modeStr, color = getDarkModeTextColor(this@AddNoteActivity),
                                        fontSize = 20.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = modeStrDesp, color = getDarkModeTextColor(this@AddNoteActivity),
                                        fontSize = 15.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val edit  = remember { mutableStateOf( "") }
                                    val url = remember { mutableStateOf( "") }
                                    if (mode.value == 9 || mode.value == 8) {
                                        TextField(
                                            value = url.value,
                                            onValueChange = {
                                                url.value = it
                                            },
                                            placeholder = {
                                                Text("请输入地址")
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp),
                                            maxLines = 4
                                        )
                                    }
                                    TextField(
                                        value = edit.value,
                                        onValueChange = {
                                            edit.value = it
                                        },
                                        placeholder = {
                                            Text("请输入内容")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        maxLines = 4
                                    )
                                    Button(
                                        onClick = {
                                           if (mode.value == 0) {
                                               if (paras.value.isNotEmpty()) {
                                                   paras.value += "\n"
                                               }
                                               paras.value += "# "+edit.value
                                           }
                                            if (mode.value == 1) {
                                                if (paras.value.isNotEmpty()) {
                                                    paras.value += "\n"
                                                }
                                                paras.value += "\n## "+edit.value
                                            }
                                            if (mode.value == 2) {
                                                if (paras.value.isNotEmpty()) {
                                                    paras.value += "\n"
                                                }
                                                paras.value += "\n### "+edit.value
                                            }
                                            if (mode.value == 3) {
                                                if (paras.value.isNotEmpty()) {
                                                    paras.value += "\n"
                                                }
                                                paras.value += "\n#### "+edit.value
                                            }
                                            if (mode.value == 4) {
                                                paras.value += "*"+edit.value + "*"
                                            }
                                            if (mode.value == 5) {
                                                if (paras.value.isNotEmpty()) {
                                                    paras.value += "\n"
                                                }
                                                paras.value += "\n* "+edit.value
                                            }
                                            if (mode.value == 6) {
                                                paras.value += "**"+edit.value + "**"
                                            }
                                            if (mode.value == 7) {
                                                paras.value += "\n```\n"+edit.value+"\n```\n"
                                            }
                                            if (mode.value == 8) {
                                                paras.value += "\n["+edit.value+"]("+url.value+")"
                                            }
                                            if (mode.value == 9) {
                                                paras.value += "\n!["+edit.value+"]("+url.value+")\n"
                                            }
                                            if (mode.value == 10) {
                                                paras.value += "\n$$\n"+edit.value+"\n$$\n"
                                            }
                                            halfDialog.value = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("确定")
                                    }
                                }
                            }
                        )


                    }
                }
            }
        }
    }
}
