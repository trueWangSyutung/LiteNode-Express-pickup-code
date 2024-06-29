package cn.tw.sar.note.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostStation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name:String,  // 站点名称
    var address :String,  // 站点地址
    var insertTime: Long, // 插入时间

)