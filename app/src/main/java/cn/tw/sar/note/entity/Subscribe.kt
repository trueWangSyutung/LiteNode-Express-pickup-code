package cn.tw.sar.note.entity


import androidx.annotation.InspectableProperty
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

enum class SubscribeType(var value: Int) {
    Daily(0),
    Weekly(1),
    Monthly(2),
    Quarterly(3),
    Yearly(4);

    object Convert {

        @TypeConverter
        fun fromSubscribeType(value: SubscribeType): Int{
            return value.ordinal
        }

        @TypeConverter
        fun toSubscribeType(value: Int): SubscribeType{
            return when(value){
                0 -> SubscribeType.Daily
                1 -> SubscribeType.Weekly
                2 -> SubscribeType.Monthly
                3 -> SubscribeType.Quarterly
                4 -> SubscribeType.Yearly
                else -> SubscribeType.Daily
            }
        }

        fun toShowString(value: SubscribeType): String{
            return when(value){
                Daily -> "/日"
                Weekly -> "/周"
                Monthly -> "/月"
                Quarterly -> "/季"
                Yearly -> "/年"
            }
        }
    }
}


@Entity(tableName = "subscribes")
data class Subscribe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var name:String,  // 订阅名称
    var desp :String,  // 订阅描述
    var appName :String,  // 订阅应用

    @TypeConverters(SubscribeType.Convert::class)
    var subscribeType : SubscribeType = SubscribeType.Daily,
    // 订阅类型

    // 上次续费的时间
    var lastRenewTime: Long,
    // 金额
    var amount: Double,
    // 币种
    var currency: String,

    var insertTime: Long, // 插入时间

)