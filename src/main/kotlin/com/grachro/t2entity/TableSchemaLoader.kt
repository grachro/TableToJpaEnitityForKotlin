package com.grachro.t2entity

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.ResultSetMetaData
import java.util.*

object TableSchemaLoader {

    private val LOG = LoggerFactory.getLogger(TableSchemaLoader::class.java)


    fun load(url: String, driver: String, props: Properties, fromTableName: String): MetaTable {
        Class.forName(driver)
        val con = DriverManager.getConnection(url, props)
        try {
            val primaryKyeNames = getPrimaries(con.metaData, fromTableName)
            val cols = getCols(con.metaData, fromTableName, primaryKyeNames)

            return MetaTable(fromTableName, primaryKyeNames, cols)
        } finally {
            con.close()
        }

    }

    private fun getPrimaries(meta: DatabaseMetaData, tableName: String): Set<String> {
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

    private fun getCols(meta: DatabaseMetaData, tableName: String, primaryKyeNames: Set<String>): List<MetaColumn> {
        var result = ArrayList<MetaColumn>()
        val rs = meta.getColumns(null, null, tableName, null)
        try {


            val cols = arrayOf("TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "DATA_TYPE",
                    "TYPE_NAME",
                    "COLUMN_SIZE",
                    "BUFFER_LENGTH",
                    "DECIMAL_DIGITS",
                    "NUM_PREC_RADIX",
                    "NULLABLE",
                    "REMARKS",
                    "COLUMN_DEF",
                    "SQL_DATA_TYPE",
                    "SQL_DATETIME_SUB",
                    "CHAR_OCTET_LENGTH",
                    "ORDINAL_POSITION",
                    "IS_NULLABLE",
                    "SCOPE_CATALOG",
                    "SCOPE_SCHEMA",
                    "SCOPE_TABLE")


            while (rs.next()) {
                val colName = rs.getString("COLUMN_NAME")
                val type = rs.getString("TYPE_NAME")
                val isNullable = rs.getBoolean("IS_NULLABLE")
                val isAutoIncrement = rs.getBoolean("IS_AUTOINCREMENT")
                val pk = primaryKyeNames.contains(colName)
                result.add(MetaColumn(colName, type, isNullable, isAutoIncrement, pk))


                //*******************
                //debug
                //*******************
                if(rs.isFirst) {
                    LOG.debug("******************")
                    LOG.debug("******************")
                    val resultMeta: ResultSetMetaData = rs.metaData
                    for (i in 1..resultMeta.columnCount) {
                        LOG.debug("resultMeta.getColumnName(${i})=${resultMeta.getColumnName(i)}")
                    }
                    LOG.debug("******************")
                    LOG.debug("******************")
                }

                LOG.debug("rs.getString(IS_AUTOINCREMENT)=${rs.getString("IS_AUTOINCREMENT")}")
                for (col in cols) {
                    LOG.debug("rs.getString(${col})=${rs.getString(col)}")
                }
                LOG.debug("==================")
            }


            LOG.debug("==================")
            LOG.debug("==================")
        } finally {
            rs.close()
        }
        return result
    }

}