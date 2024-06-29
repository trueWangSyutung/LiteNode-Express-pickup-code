package cn.tw.sar.note

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.note.ui.theme.LiteNoteTheme
import cn.tw.sar.note.utils.getDarkModeBackgroundColor
import cn.tw.sar.note.utils.getDarkModeTextColor

class MoreSettingsActivity2 : ComponentActivity() {
    var settingsShowNames = mutableStateListOf(
        "屏蔽默认取件码规则",
    )
    var settingsNames = mutableStateListOf<String>(
        "mrqjm"
    )
    var settingsValues = mutableStateListOf<MutableState<Boolean>>(
        mutableStateOf(false)
    )

    override fun onResume() {
        super.onResume()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharePref = getSharedPreferences("settings", Context.MODE_PRIVATE)

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
                                    this@MoreSettingsActivity2, 0
                                )
                            )
                            .padding(
                                20.dp
                            )
                    ) {
                        var fontColor = getDarkModeTextColor(this@MoreSettingsActivity2)

                        Column {
                            Text(
                                text = "更多设置", color = fontColor,
                                fontSize = 35.sp,
                                modifier = Modifier.fillMaxWidth(),

                                )
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        for (i in 0 until settingsShowNames.size) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = getDarkModeBackgroundColor(
                                            context = this@MoreSettingsActivity2,
                                            level = 1
                                        ),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 5.dp,
                                            bottom = 5.dp,
                                            start = 10.dp,
                                            end = 10.dp
                                        )
                                        ,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = settingsShowNames[i], color = fontColor, fontSize = 20.sp)
                                    var checked = remember {
                                        mutableStateOf(sharePref.getBoolean(settingsNames[i], false))
                                    }
                                    Switch(checked = checked.value , onCheckedChange = {
                                        checked.value = it
                                        sharePref.edit().putBoolean(settingsNames[i], it).apply()

                                    } )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

