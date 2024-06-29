package cn.tw.sar.note.widgets


import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import cn.tw.sar.note.R
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.CItem
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.utils.expercessToResource
import cn.tw.sar.note.utils.isDarkMode
import kotlin.concurrent.thread


/**
 * Implementation of App Widget functionality.
 */
class TableCodeWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TableCodeWidgetBigger()
}

class TableCodeWidgetBigger : GlanceAppWidget() {
    var lists = mutableListOf<List<Code>>()
    var stations = mutableListOf<CItem>()
    var isOk = mutableStateOf(false)
    fun loadList(context: Context){
        // load data from database
        lists.clear()
        isOk.value = false

        thread {
            val database = CodeDatabase.getDatabase(context)
            val dao = database.codeDao()
            stations.addAll(dao.getAllByPostsAndStatus(0))
            for (i2 in 0 until stations.size) {
                lists.addAll(
                    listOf(
                        dao.getAllByStatusAndYz(0, 0, 5,
                            stations[i2].first)
                    )
                )
            }
            isOk.value = true
            Log.d("TableCodeWidgetBigger","Loading Data Succeed")
            Log.d("TableCodeWidgetBigger",lists.toString())

        }
    }


    fun getDarkModeTextColor2(
        context: Context
    ): Color {
        return if (isDarkMode(context)) {
            Color(0xFFFFFFFF)
        } else {
            Color(0xFF000000)
        }
    }

    fun getDarkModeBackgroundColor2(
        context: Context,
        level : Int
    ):  Color {
        return if (isDarkMode(context)) {
            if (level == 0) {
                Color(0xFF000000)
            } else if (level == 1) {
                Color(0xFF444444)

            } else if (level == 2) {
                Color(0xFF888888)

            } else {
                Color(0xFF000000)

            }

        } else {
            if (level == 0) {
                Color(0xFFFFFFFF)

            } else if (level == 1) {
                Color(0xFFF5F5F5)

            } else if (level == 2) {
                Color(0xFFFFF8E3)

            } else {
                Color(0xFFFFFFFF)

            }
        }
    }

    fun getWidth(context: Context): Int {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                TableCodeWidget::class.java
            )
        )
        val options = appWidgetManager.getAppWidgetOptions(appWidgetIds[0])
        return options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
    }
    var index = mutableStateOf(0)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        loadList(context)
        val width = getWidth(context)
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().background(
                    color = getDarkModeBackgroundColor2(context,0)
                ).cornerRadius(
                    20.dp
                ).padding(10.dp)
            ) {
                if (isOk.value){
                    Column(
                        modifier = GlanceModifier.fillMaxSize().background(
                            color = getDarkModeBackgroundColor2(context, 1)
                        ).cornerRadius(
                            20.dp
                        ).padding(10.dp),
                     ) {
                        if (lists.size!=0){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = GlanceModifier.fillMaxWidth()
                            ) {

                                Text(text = stations[index.value].first, style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    color = ColorProvider(getDarkModeTextColor2(context))
                                ))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = GlanceModifier.fillMaxWidth()

                            ) {
                                Row(
                                    modifier = GlanceModifier.clickable {
                                        Log.d("TableCodeWidgetBigger","index.value = "+index.value)
                                        Log.d("TableCodeWidgetBigger","stations.size = "+stations.size)
                                        index.value = 0
                                        Log.d("TableCodeWidgetBigger","index.value = "+index.value)
                                        loadList(context)
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Text(text = "刷新", style = TextStyle(color = ColorProvider(getDarkModeTextColor2(context))))
                                    Image(
                                        provider = ImageProvider(
                                            R.drawable.baseline_refresh_24
                                        ),
                                        contentDescription = "kuaidi",
                                        modifier = GlanceModifier.width((15).dp).height(15.dp)
                                    )
                                }
                                Spacer(modifier = GlanceModifier.width((width*0.4).dp))

                                Row(
                                    modifier = GlanceModifier.clickable {
                                        Log.d("TableCodeWidgetBigger","index.value = "+index.value)
                                        Log.d("TableCodeWidgetBigger","stations.size = "+stations.size)
                                        index.value = (index.value+1)%stations.size
                                        Log.d("TableCodeWidgetBigger","index.value = "+index.value)

                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Text(text = "下一个驿站", style = TextStyle(color = ColorProvider(getDarkModeTextColor2(context))))
                                    Image(
                                        provider = ImageProvider(
                                            R.drawable.baseline_arrow_right_24
                                        ),
                                        contentDescription = "kuaidi",
                                        modifier = GlanceModifier.width((15).dp).height(15.dp)
                                    )
                                }

                            }
                            Spacer(modifier = GlanceModifier.height((5).dp))

                            for (i in 0 until lists[index.value].size) {
                                if (i<lists[index.value].size){
                                    var it = lists[index.value][i]
                                    var showText = if (it.kd.contains("快递")) {
                                        it.kd
                                    } else if (it.kd.contains("驿站")) {
                                        it.kd
                                    } else {
                                        it.kd + "快递"
                                    }
                                    Row(
                                        verticalAlignment = Alignment.TopStart.vertical
                                    ) {
                                        Image(
                                            provider = ImageProvider(
                                                expercessToResource(it.kd)
                                            ),
                                            contentDescription = it.kd,
                                            modifier = GlanceModifier.width(20.dp).height(20.dp).padding(
                                                end = 6.dp
                                            )
                                        )
                                        Text(
                                            text = showText + " " + it.code,
                                            modifier = GlanceModifier.padding(end = 15.dp)
                                            , style = TextStyle(color = ColorProvider(getDarkModeTextColor2(context))))

                                    }
                                }
                            }
                        }else{
                            Column(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = GlanceModifier.fillMaxSize().padding(10.dp).clickable {
                                    loadList(context)
                                },
                            ){
                                Image(
                                    provider = ImageProvider(
                                        R.mipmap.empty
                                    ),
                                    contentDescription = "empty",
                                    modifier = GlanceModifier.width((50).dp).height(50.dp)
                                )
                                Text(text = "当前为空，点击刷新", style = TextStyle(color = ColorProvider(getDarkModeTextColor2(context))))

                            }
                        }

                    }
                }
            }
        }
    }
}
