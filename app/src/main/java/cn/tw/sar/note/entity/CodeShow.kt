package cn.tw.sar.note.entity

data class CodeShow(
    var code : Code,
    var detail : CodeDetail,
)

data class CodeAndYz(
    var codes : MutableList<Code>,
    var yz : String,
    var num : Int,
)