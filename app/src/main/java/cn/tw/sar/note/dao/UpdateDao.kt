package cn.tw.sar.note.dao

import UpdateDataBased
import cn.tw.sar.note.entity.LogUpdateRes
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface UpdateDao {
    //get请求
    @GET("getCurrentVersion.php")
    fun getCurrVersion(
        @Query("apk_type") apk_type: String,
        @Query("version_code") version_code: Int
    ): Call<UpdateDataBased>?

    //get请求
    @GET("checkUpdate.php")
    fun getUpdateInfo(
        @Query("apk_type") apk_type: String,
        @Query("version_code") version_code: Int
    ): Call<UpdateDataBased>?

    // https://api.wxd2zrx.top/updateLogs.php?type=0&message=dsfdf&detail=dsffddf&key
    @GET("updateLogs.php")
    fun postUpdateLogs(
        @Query("type") type: String,
        @Query("message") message: String,
        @Query("detail") detail: String,
        @Query("key") key: String
    ): Call<LogUpdateRes>?

}