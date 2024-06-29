package cn.tw.sar.note.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.tw.sar.note.entity.CItem
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.entity.CodeFormat
import cn.tw.sar.note.entity.PostStation

/**
@Entity(tableName = "posts")
data class PostStation(
@PrimaryKey(autoGenerate = true)
val id: Long = 0,
var name:String,  // 站点名称
var address :String,  // 站点地址
var insertTime: Long, // 插入时间
 */
@Dao
interface PortsDao {

    @Query("SELECT * FROM posts")
    fun getAll(): List<PostStation>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getPostById(id: Long): PostStation

    @Insert
    fun insertPost(post: PostStation)

    @Update
    fun updatePost(post: PostStation)

    @Query("DELETE FROM posts WHERE id = :id")
    fun deletePost(id: Long)

    @Query("DELETE FROM posts")
    fun deleteAllPosts()

    @Query("SELECT COUNT(*) FROM posts")
    fun getPostsCount(): Int

    @Query("SELECT name FROM posts ")
    fun getAllPostNames(): List<String>









}