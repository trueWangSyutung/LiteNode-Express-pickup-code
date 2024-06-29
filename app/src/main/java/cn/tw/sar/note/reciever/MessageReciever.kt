package cn.tw.sar.note.reciever

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.tw.sar.note.data.CodeDatabase
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.entity.CodeConfig
import cn.tw.sar.note.service.FocusService
import cn.tw.sar.note.utils.PermissionUtils
import cn.tw.sar.note.utils.PickupCodeUtils
import kg.edu.yjut.litenote.miui.MiuiStringToast
import kg.edu.yjut.litenote.miui.ToastConfig
import kg.edu.yjut.litenote.miui.devicesSDK.isMoreHyperOSVersion
import kg.edu.yjut.litenote.miui.devicesSDK.isUnHyperOSNotices
import java.sql.Date
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class MessageReciever : BroadcastReceiver() {
    private final var SMS_RECEIVER_ACTION = "android.provider.Telephony.SMS_RECEIVED"


    override fun onReceive(context: Context, intent: Intent) {
        var sBuilder = StringBuilder();
        val format = intent.getStringExtra("format");
        if(SMS_RECEIVER_ACTION.equals(intent.getAction()))
        {
            val bundle = intent.getExtras();
            if(null != bundle)
            {
                val pdus = bundle.get("pdus") as Array<Any>;
                val messages = arrayOfNulls<SmsMessage>(pdus.size);
                for(i in messages.indices)
                {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray,format);
                }
                for(msg in messages)
                {
                    if (msg != null) {
                        //sBuilder.append("来自：").append(msg.getDisplayOriginatingAddress()).append("\n")
                        //    .append("短信内容：")
                        sBuilder.append(msg.getDisplayMessageBody()).append("\n")

                    };
                }
            }
        }

        if (sBuilder.toString().isEmpty()) {
            return;
        }else{
            if (PickupCodeUtils.isPickupCode(sBuilder.toString())){
                var code = PickupCodeUtils.getPickedCode(sBuilder.toString(),context)
                var company = PickupCodeUtils.getPickupCompany(sBuilder.toString(),context)
                var yz = PickupCodeUtils.getPickupYz(sBuilder.toString(),context)

                if (code.isNotEmpty()){
                    Log.d("MessageReciever", code.toString());
                    if (code.size > 1){
                        // 将所有拼接成一个字符串，以逗号分隔
                        val codeStr = code.joinToString(",")
                        showQujianma(
                            context,
                            CodeConfig(
                                codeStr,
                                company,
                                0,
                                yz
                            )
                        )
                    }else{
                        showQujianma(
                            context,
                            CodeConfig(
                                code[0],
                                company,
                                0,
                                yz
                            )
                        )
                    }
                    thread {
                        val database: CodeDatabase =
                            CodeDatabase.getDatabase(context)
                        val codeDao = database.codeDao()
                        for (c in code){
                            val codeBin = codeDao.getByCode(c)
                            Log.d("codeBin", codeBin.toString())
                            if (codeBin == null) {
                                var strDay = System.currentTimeMillis()
                                val yzStr = if (yz=="") "未知驿站" else yz
                                val kdStr = if (company=="") "未知" else company
                                codeDao.insert(
                                    Code(
                                        0,
                                        c,
                                        yz = yzStr,
                                        kd = kdStr,
                                        0,
                                        0,
                                        0,
                                        strDay,
                                        timeStempToTime(strDay,2),
                                        timeStempToTime(strDay,1),
                                        timeStempToTime(strDay,3)

                                    )
                                )

                            }
                        }

                    }
                    Toast.makeText(context, "已经添加到取件码", Toast.LENGTH_SHORT).show()

                }

            }

        }

        Log.d("MessageReciever", sBuilder.toString());
    }
    @SuppressLint("SimpleDateFormat")
    private fun timeStempToTime(timest: Long,mode:Int): String {
        //val ptimest=1000L*timest
        // 转换为 2024-12-12 12:12
        if (mode == 1){
            val sdf =  SimpleDateFormat("yyyy-MM-dd");
            val dateStr = sdf.format(java.util.Date(timest).time);
            return dateStr;
        }else if (mode == 2){
            val sdf =  SimpleDateFormat("yyyy-MM");
            val dateStr = sdf.format(java.util.Date(timest).time);
            return dateStr;
        }else if (mode == 3){
            val sdf =  SimpleDateFormat("yyyy");
            val dateStr = sdf.format(java.util.Date(timest).time);
            return dateStr;
        }else{
            val sdf =  SimpleDateFormat("yyyy-MM-dd HH:mm");
            val dateStr = sdf.format(java.util.Date(timest).time);
            return dateStr;
        }

    }
    @SuppressLint("WrongConstant")
    fun showQujianma(
        context: Context,
        config: CodeConfig
    ){
        if (PermissionUtils.checkShowOnLockScreen(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isMoreHyperOSVersion(1f)) {
                if ( isUnHyperOSNotices(context)){
                    val intent = Intent(context, FocusService::class.java)
                    intent.putExtra("text", config.text)
                    intent.putExtra("express", config.express)
                    intent.putExtra("type", config.type)
                    intent.putExtra("from", config.from)
                    intent.putExtra("logMode", false)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        ContextCompat.startForegroundService(context, intent)
                    } else {
                        context.startService(intent)
                    }
                }
                else{
                    MiuiStringToast.showStringToast(
                        context, ToastConfig(
                            "您已收到"+config.from+"的快递" + config.text,
                            "#1296db",
                            "dd",
                            8000L,
                            null
                        )
                    )

                }
                // 保存到数据库



            } else {

                val intent = Intent(context, FocusService::class.java)
                intent.putExtra("text", config.text)
                intent.putExtra("express", config.express)
                intent.putExtra("type", config.type)
                intent.putExtra("from", config.from)
                intent.putExtra("logMode", false)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }

            }

        }

    }

}