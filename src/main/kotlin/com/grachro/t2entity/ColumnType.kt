package com.grachro.t2entity

enum class ColumnType(val primitive:Boolean) {
    INT(true),
    STRING(false),
    BOOLEAN(true),
    TIMESTAMP(false),
    ETC(false),
    ;

    companion object {
        fun get(dbType:String): ColumnType {
            return when(dbType) {
                "INT" -> INT
                "CHAR","VARCHAR" -> STRING
                "DATETIME","DATE","TIMESTAMP" -> TIMESTAMP
                "BIT" -> BOOLEAN
                else -> ETC
            }
        }
    }


}