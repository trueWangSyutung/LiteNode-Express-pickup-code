package kg.edu.yjut.litenote.miui
/*
  * This file is part of HyperCeiler.

  * HyperCeiler is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation, either version 3 of the
  * License.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.

  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.

  * Copyright (C) 2023-2024 HyperCeiler Contributions
*/

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import cn.tw.sar.note.MainActivity
import cn.tw.sar.note.R
import com.google.gson.Gson
import kg.edu.yjut.litenote.miui.devicesSDK.isMoreHyperOSVersion
import kg.edu.yjut.litenote.miui.devicesSDK.isUnHyperOSNotices
import kg.edu.yjut.litenote.miui.res.IconParams
import kg.edu.yjut.litenote.miui.res.Left
import kg.edu.yjut.litenote.miui.res.Right
import kg.edu.yjut.litenote.miui.res.StringToastBean
import kg.edu.yjut.litenote.miui.res.StringToastBundle
import kg.edu.yjut.litenote.miui.res.TextParams
import java.lang.reflect.InvocationTargetException

object MiuiStringToast {
    fun newIconParams(
        category: String?,
        iconResName: String?,
        iconType: Int,
        iconFormat: String?
    ): IconParams {
        val params = IconParams()
        params.setCategory(category)
        params.setIconResName(iconResName)
        params.setIconType(iconType)
        params.setIconFormat(iconFormat)
        return params
    }

    @SuppressLint("WrongConstant")
    fun showStringToast(context: Context,
                        config: ToastConfig
    ) {
        try {
            val textParams = TextParams()
            textParams.setText(config.text)
            textParams.setTextColor(colorToInt(config.textColor))

            val left = Left()
            left.setTextParams(textParams)

            val iconParams: IconParams =
                newIconParams(Category.DRAWABLE, config.image, 1, FileType.SVG)


            Log.d("STRONGToast", iconParams.toString())


            val right = Right()
            right.setIconParams(iconParams)

            val stringToastBean = StringToastBean()
            stringToastBean.setLeft(left)
            stringToastBean.setRight(right)
            val gson = Gson()
            val str = gson.toJson(stringToastBean)


            val bundle: Bundle = StringToastBundle.Builder()
                .setPackageName(context.packageName)
                .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP_INTENT.value)
                .setTarget(config.intent)
                .setDuration(config.duration)
                .setLevel(1.0f)
                .setRapidRate(0.2f)
                .setCharge(null as String?)
                .setStringToastChargeFlag(0)
                .setParam(str)
                .setStatusBarStrongToast("show_custom_strong_toast")
                .onCreate()

            val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
            service.javaClass.getMethod(
                "setStatus",
                Int::class.javaPrimitiveType,
                String::class.java,
                Bundle::class.java
            )
                .invoke(service, 1, "strong_toast_action", bundle)


        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        }
    }







    fun colorToInt(color: String?): Int {
        val color1 = Color.parseColor(color)
        val color2 = Color.parseColor("#FFFFFF")
        val color3 = color1 xor color2
        return color3.inv()
    }

    object Category {
        const val DRAWABLE = "drawable"
    }

    object FileType {
        const val SVG = "svg"
    }

    enum class StrongToastCategory(var value: String) {
        VIDEO_TEXT("video_text"),
        VIDEO_BITMAP_INTENT("video_bitmap_intent"),
        TEXT_BITMAP("text_bitmap"),
        TEXT_BITMAP_INTENT("text_bitmap_intent"),
        VIDEO_TEXT_TEXT_VIDEO("video_text_text_video"),
        TEXT_TEXT("text_text")
    }
}