package cn.tw.sar.note.dao

import UpdateDataBased
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
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
}