package cn.tw.sar.note.utils

import android.util.Log
import cn.tw.sar.note.dao.UpdateDao
import cn.tw.sar.note.entity.LogUpdateRes
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

object LogUpdate {
    fun updateLog(type: String, message: String, detail: String) {
        val mRetrofit =  Retrofit.Builder()
            //设置网络请求BaseUrl地址
            .baseUrl("https://api.wxd2zrx.top/")
            //设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        val api: UpdateDao = mRetrofit.create(UpdateDao::class.java)
        Log.d("LogUpdate", "updateLog: $type $message $detail")

        val dataCall: Call<LogUpdateRes>? = api.postUpdateLogs(type, message, detail,"Dbdm6J2Zu8gftgpU")
        if (dataCall != null) {
            val response: Response<LogUpdateRes> = dataCall.execute()
            Log.d("LogUpdate", "updateLog: ${response}")

            if (response.isSuccessful) {
                val data: LogUpdateRes? = response.body()
                Log.d("LogUpdate", "updateLog: ${data}")
            }
        }
    }
}