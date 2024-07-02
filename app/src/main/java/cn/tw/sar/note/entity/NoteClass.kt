package cn.tw.sar.note.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "note_class")
data class NoteClass(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var className:String,  // 站点名称
    var classState :Int,  // 站点地址
    var insertTime: Long, // 插入时间
)