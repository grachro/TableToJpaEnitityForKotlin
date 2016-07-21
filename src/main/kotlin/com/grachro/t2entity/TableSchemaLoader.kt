package com.grachro.t2entity

import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.util.*

object TableSchemaLoader {

    fun load(url:String,driver:String,props:Properties, fromTableName:String): MetaTable {
        Class.forName(driver)
        val con =  DriverManager.getConnection(url, props)
        try {
            val primaryKyeNames = getPrimaries(con.metaData,fromTableName)
            val cols = getCols(con.metaData,fromTableName,primaryKyeNames)

            return MetaTable(fromTableName, primaryKyeNames, cols)
        } finally {
            con.close()
        }

    }

    private fun getPrimaries(meta: DatabaseMetaData, tableName:String ):Set<String> {
        var result = HashSet<String>()
        val pkRm = meta.getPrimaryKeys(null, null, tableName)
        try {
            while (pkRm.next()) {
                result.add(pkRm.getString("COLUMN_NAME"))
            }
        } finally {
            pkRm.close()
        }
        return result
    }

    private fun getCols(meta: DatabaseMetaData, tableName:String,primaryKyeNames:Set<String> ):List<MetaColumn> {
        var result = ArrayList<MetaColumn>()
        val rs = meta.getColumns(null, null, tableName, null)
        try {
            while (rs.next()) {
                val colName = rs.getString("COLUMN_NAME")
                val type = rs.getString("TYPE_NAME")
                val isNullable = rs.getBoolean("IS_NULLABLE")
                val pk = primaryKyeNames.contains(colName)
                result.add(MetaColumn(colName, type, isNullable, pk))

            }
        } finally {
            rs.close()
        }
        return result
    }

}