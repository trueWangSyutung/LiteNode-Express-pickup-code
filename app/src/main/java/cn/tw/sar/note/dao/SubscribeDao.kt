package cn.tw.sar.note.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.tw.sar.note.entity.CodeFormat
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
interface SubscribeDao {
    @Insert
    fun insert(item: Subscribe)

    @Insert
    fun insertAll(items: List<Subscribe>)

    @Insert
    fun insertAll(vararg items: Subscribe)


    // 获取所有的订阅，分页， 按照id排序
    @Query("SELECT * FROM subscribes ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun getAll(offset: Int, limit: Int): List<Subscribe>

    // 获取所有的订阅，分页, 按照插入时间排序
    @Query("SELECT * FROM subscribes ORDER BY insertTime DESC LIMIT :limit OFFSET :offset")
    fun getAllOrderByInsertTime(offset: Int, limit: Int): List<Subscribe>

    // 获取所有的订阅，分页, 按照上次续费时间排序
    @Query("SELECT * FROM subscribes ORDER BY lastRenewTime DESC LIMIT :limit OFFSET :offset")
    fun getAllOrderByLastRenewTime(offset: Int, limit: Int): List<Subscribe>

    // 获取所有的订阅，分页, 按照金额排序
    @Query("SELECT * FROM subscribes ORDER BY amount DESC LIMIT :limit OFFSET :offset")
    fun getAllOrderByAmount(offset: Int, limit: Int): List<Subscribe>
    // 获取所有的订阅，分页, 按照金额排序
    @Query("SELECT * FROM subscribes ORDER BY amount ASC LIMIT :limit OFFSET :offset")
    fun getAllOrderByAmountASC(offset: Int, limit: Int): List<Subscribe>
    // 更新
    @Update
    fun update(item: Subscribe)

    // 删除
    @Query("DELETE FROM subscribes WHERE id = :id")
    fun deleteById(id: Long)

    // 获取
    @Query("SELECT * FROM subscribes WHERE id = :id")
    fun getById(id: Long): Subscribe?

    // 获取数目
    @Query("SELECT COUNT(*) FROM subscribes")
    fun getCount(): Int











}