package cn.tw.sar.note.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "note_detail")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var noteTitle:String,  // 标题
    var classID :Long,  // 分类ID
    var noteContent: String, // 内容
    var insertTime: Long, // 插入时间
    var updateTime: Long, // 更新时间

    var noteState :Int, // 状态

)