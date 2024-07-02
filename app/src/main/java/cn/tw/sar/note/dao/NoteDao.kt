package cn.tw.sar.note.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.tw.sar.note.entity.CodeFormat
import cn.tw.sar.note.entity.NoteClass
import cn.tw.sar.note.entity.Notes
import cn.tw.sar.note.entity.Subscribe

/**
 * @Entity(tableName = "subscribes")
 * data class Subscribe(
 *     @PrimaryKey(autoGenerate = true)
 *     val id: Long = 0,
 *
 *     var name:String,  // 订阅名称
 *     var desp :String,  // 订阅描述
 *     var appName :String,  // 订阅应用
 *
 *     @TypeConverters(SubscribeType.Converter::class)
 *     var subscribeType : SubscribeType = SubscribeType.Daily,
 *     // 订阅类型
 *
 *     // 上次续费的时间
 *     var lastRenewTime: Long,
 *     // 金额
 *     var amount: Double,
 *     // 币种
 *     var currency: String,
 *
 *     var insertTime: Long, // 插入时间
 *
 * )
 */
@Dao
interface NoteDao {
    // 插入Notes
    @Insert
    fun insertNote(item: Notes)

    // 插入多个Notes
    @Insert
    fun insertAllNotes(items: List<Notes>)

    // 插入多个Notes
    @Insert
    fun insertAllNotes(vararg items: Notes)

    // 插入 NoteClass
    @Insert
    fun insertClass(item: NoteClass)

    // 插入多个 NoteClass
    @Insert
    fun insertAllClass(items: List<NoteClass>)

    // 插入多个 NoteClass
    @Insert
    fun insertAllClass(vararg items: NoteClass)

    // 获取所有的分类
    @Query("SELECT * FROM note_class WHERE classState = 0 ORDER BY id DESC")
    fun getAllClass(): List<NoteClass>

    @Query("SELECT count(*) FROM note_class WHERE id = :id")
    fun getClassCount(id: Long): Int

    @Query("SELECT * FROM note_detail WHERE id = :id")
    fun getNoteById(id: Long): Notes




    // 删除分类
    @Query("DELETE FROM note_class WHERE id = :id")
    fun deleteClass(id: Long)

    // 更新分类
    @Update
    fun updateClass(item: NoteClass)

    // 获取所有的Notes，分页， 按照id排序
    @Query("SELECT * FROM note_detail WHERE noteState = 0 AND classID = :classID  ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun getAllNotes(offset: Int, limit: Int, classID: Long): List<Notes>

    // 获取所有的Notes，分页， 按照id排序, 搜索

    // 获取所有的Notes，分页, 按照插入时间排序
    @Query("SELECT * FROM note_detail " +
            "WHERE noteState = 0 AND classID = :classID " +
            "ORDER BY insertTime DESC LIMIT :limit OFFSET :offset")
    fun getAllNotesOrderByInsertTime(offset: Int, limit: Int, classID: Long): List<Notes>

    // 获取所有的Notes，分页, 按照更新时间排序
    @Query("SELECT * FROM note_detail WHERE noteState = 0 AND classID = :classID ORDER BY updateTime DESC LIMIT :limit OFFSET :offset")
    fun getAllNotesOrderByUpdateTime(offset: Int, limit: Int, classID: Long): List<Notes>

    // 更新Notes
    @Update
    fun updateNotes(item: Notes)

    // 删除Notes
    @Query("DELETE FROM note_detail WHERE id = :id")
    fun deleteNotes(id: Long)





}