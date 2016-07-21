package com.grachro.t2entity

enum class ColumnType {
    INT, STRING, TIMESTAMP,ETC,
    ;

    companion object {
        fun get(dbType:String): ColumnType {
            return when(dbType) {
                "INT" -> INT
                "CHAR","VARCHAR" -> STRING
                "DATETIME","DATE","TIMESTAMP" -> TIMESTAMP
                else -> ETC
            }
        }
    }
}