package cn.tw.sar.note.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.tw.sar.note.dao.CodeDao
import cn.tw.sar.note.dao.DetailDao
import cn.tw.sar.note.dao.ExpressDao
import cn.tw.sar.note.dao.FormatDao
import cn.tw.sar.note.dao.NoteDao
import cn.tw.sar.note.dao.PortsDao
import cn.tw.sar.note.dao.SubscribeDao
import cn.tw.sar.note.entity.Code
import cn.tw.sar.note.entity.CodeDetail
import cn.tw.sar.note.entity.CodeFormat
import cn.tw.sar.note.entity.Express
import cn.tw.sar.note.entity.NoteClass
import cn.tw.sar.note.entity.Notes
import cn.tw.sar.note.entity.PostStation
import cn.tw.sar.note.entity.Subscribe

private val MIGRATION_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        // 将 item 表添加字段
        //     var strMonth : String,
        //    var strDay : String,
        //    var strYear : String,
        val sql = "ALTER TABLE item ADD COLUMN strMonth TEXT NOT NULL DEFAULT '';"
        database.execSQL(sql)
        val sql2 = "ALTER TABLE item ADD COLUMN strDay TEXT NOT NULL DEFAULT '';"
        database.execSQL(sql2)
        val sql3 = "ALTER TABLE item ADD COLUMN strYear TEXT NOT NULL DEFAULT '';"
        database.execSQL(sql3)
        // 将 item 表的所有数据的 strMonth 字段设置为  strftime('%Y-%m-%d', insertTime / 1000, 'unixepoch')
        val sql4 = "UPDATE item SET strMonth = strftime('%Y-%m', insertTime / 1000, 'unixepoch');"
        database.execSQL(sql4)
        // 将 item 表的所有数据的 strDay 字段设置为  strftime('%d', insertTime / 1000, 'unixepoch')
        val sql5 = "UPDATE item SET strDay = strftime('%Y-%m-%d', insertTime / 1000, 'unixepoch');"
        database.execSQL(sql5)
        // 将 item 表的所有数据的 strYear 字段设置为  strftime('%Y', insertTime / 1000, 'unixepoch')
        val sql6 = "UPDATE item SET strYear = strftime('%Y', insertTime / 1000, 'unixepoch');"
        database.execSQL(sql6)




    }
}

private val MIGRATION_2_3 = object : Migration(2,3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加数据表
        // data class CodeFormat(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Int = 0,
        //
        //    var codeFormat: String,
        //    var codeLength : Int,
        //    var codeTypes : String,  // 【0,1,3
        //    )
        val sql = "CREATE TABLE format (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "codeFormat TEXT NOT NULL DEFAULT '', " +
                "codeLength INTEGER NOT NULL DEFAULT 0, " +
                "codeTypes TEXT NOT NULL DEFAULT '');"
        database.execSQL(sql)


    }
}

private val MIGRATIOM_3_4 = object : Migration(3,4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 将 format和item表的id字段改为Long类型
        var sql = "ALTER TABLE item "+
                " MODIFY (id BIGINT" +
                " PRIMARY KEY AUTOINCREMENT NOT NULL);"
        database.execSQL(sql)
        sql = "ALTER TABLE format "+
                " MODIFY (id BIGINT" +
                " PRIMARY KEY AUTOINCREMENT NOT NULL);"
        database.execSQL(sql)




    }
}

private val MIGRATION_4_5 = object : Migration(4,5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // @Entity(tableName = "posts")
        //data class PostStation(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //    var name:String,  // 站点名称
        //    var address :String,  // 站点地址
        //    var insertTime: Long, // 插入时间
        // 添加数据库
        val sql = "CREATE TABLE posts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL DEFAULT '', " +
                "address TEXT NOT NULL DEFAULT '', " +
                "insertTime INTEGER NOT NULL DEFAULT 0);"
        database.execSQL(sql)


        //@Entity(tableName = "detail")
        //data class CodeDetail(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //    var pid : Long = 0L,
        //    var name:String, // Experss Name \ 快递物件名称
        //    var buyPlace : String, // Buy Place \ 购买地点、快递渠道
        //    var buyTime : Long, // Buy Time \ 购买时间
        //    var buyPrice : Double, // Buy Price \ 购买价格
        //    var buyCurrency : String, // Buy Currency \ 购买货币
        //    var buyStatusDesc : String, // Buy Status Description \ 购买状态描述
        //    var tags : String, // Tags \ 标签
        //)
        val sql2 = "CREATE TABLE detail (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "pid INTEGER NOT NULL DEFAULT 0, " +
                "name TEXT NOT NULL DEFAULT '', " +
                "buyPlace TEXT NOT NULL DEFAULT '', " +
                "buyTime INTEGER NOT NULL DEFAULT 0, " +
                "buyPrice REAL NOT NULL DEFAULT 0.0, " +
                "buyCurrency TEXT NOT NULL DEFAULT '', " +
                "buyStatusDesc TEXT NOT NULL DEFAULT '', " +
                "tags TEXT NOT NULL DEFAULT '');"
        database.execSQL(sql2)

        // 对所有的Code（item数据库），修改 yz 字段为 未知驿站
        val sql4 = "UPDATE item SET yz = '未知驿站';"
        database.execSQL(sql4)


    }
}

private val MIGRATION_5_6 = object : Migration(5,6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加数据表
        // @Entity(tableName = "express")
        //data class Express(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //    var name:String,  // 站点名称
        //)
        val sql = "CREATE TABLE express (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL DEFAULT '');"
        database.execSQL(sql)

    }
}

private val MIGRATION_6_7 = object : Migration(6,7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加数据表
        // @Entity(tableName = "subscribes")
        //data class Subscribe(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //
        //    var name:String,  // 订阅名称
        //    var desp :String,  // 订阅描述
        //    var appName :String,  // 订阅应用
        //
        //    @TypeConverters(SubscribeType.Converter::class)
        //    var subscribeType : SubscribeType = SubscribeType.Daily,
        //    // 订阅类型
        //
        //    // 上次续费的时间
        //    var lastRenewTime: Long,
        //    // 金额
        //    var amount: Double,
        //    // 币种
        //    var currency: String,
        //
        //    var insertTime: Long, // 插入时间
        //
        //)
        val sql = " CREATE TABLE subscribes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL DEFAULT '', " +
                "desp TEXT NOT NULL DEFAULT '', " +
                "appName TEXT NOT NULL DEFAULT '', " +
                "subscribeType TEXT NOT NULL DEFAULT '0', " +
                "lastRenewTime INTEGER NOT NULL DEFAULT 0, " +
                "amount REAL NOT NULL DEFAULT 0.0, " +
                "currency TEXT NOT NULL DEFAULT '', " +
                "insertTime INTEGER NOT NULL DEFAULT 0);"
        database.execSQL(sql)


    }
}

private val MIGRATION_8_9 = object : Migration(8,9)  {
    override fun migrate(db: SupportSQLiteDatabase) {




    }

}



private val MIGRATION_9_10 = object : Migration(9,10)  {
    override fun migrate(db: SupportSQLiteDatabase) {
        // @Entity(tableName = "note_detail")
        //data class Notes(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //    var noteTitle:String,  // 标题
        //    var classID :Long,  // 分类ID
        //    var noteContent: String, // 内容
        //    var insertTime: Long, // 插入时间
        //    var updateTime: Long, // 更新时间
        //
        //    var noteState :Int, // 状态
        //
        //)
        val sql = "CREATE TABLE note_detail (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "noteTitle TEXT NOT NULL DEFAULT '', " +
                "classID INTEGER NOT NULL DEFAULT 0, " +
                "noteContent TEXT NOT NULL DEFAULT '', " +
                "insertTime INTEGER NOT NULL DEFAULT 0, " +
                "updateTime INTEGER NOT NULL DEFAULT 0, " +
                "noteState INTEGER NOT NULL DEFAULT 0);"
        db.execSQL(sql)

        // @Entity(tableName = "note_class")
        //data class NoteClass(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //    var className:String,  // 站点名称
        //    var classState :Int,  // 站点地址
        //    var insertTime: Long, // 插入时间
        //)
        val sql2 = "CREATE TABLE note_class (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "className TEXT NOT NULL DEFAULT '', " +
                "classState INTEGER NOT NULL DEFAULT 0, " +
                "insertTime INTEGER NOT NULL DEFAULT 0);"
        db.execSQL(sql2)
        // 插入默认分类
        val sql3 = "INSERT INTO note_class (id,className, classState, insertTime) VALUES (0, '默认分类', 0, strftime('%s','now'));"
        db.execSQL(sql3)




    }

}


@Database(entities = [Code::class,
                      CodeFormat::class,
                      CodeDetail::class,
                      PostStation::class,
                      Express::class,
                      Subscribe::class,
                      Notes::class, NoteClass::class
                     ], version = 10, exportSchema = false)
abstract class CodeDatabase : RoomDatabase() {

    abstract fun codeDao(): CodeDao
    abstract fun formatDao(): FormatDao
    abstract fun detailDao(): DetailDao
    abstract fun portsDao(): PortsDao
    abstract fun expressDao(): ExpressDao
    abstract fun subscribeDao(): SubscribeDao


    abstract fun noteDao(): NoteDao


    companion object {
        @Volatile
        private var INSTANCE: CodeDatabase? = null
        fun getDatabase(context: Context): CodeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeDatabase::class.java,
                    "code_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATIOM_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_7)
                    .addMigrations(MIGRATION_8_9)
                    .addMigrations(MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}