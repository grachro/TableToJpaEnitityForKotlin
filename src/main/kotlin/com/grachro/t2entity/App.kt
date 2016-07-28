package com.grachro.t2entity

import java.io.File
import java.util.*


fun main(args : Array<String>) {

    val dbProps = Properties()
    dbProps.load(Properties::class.java.getResourceAsStream("/db.properties"))
    println(dbProps.stringPropertyNames())
    val url = dbProps.getProperty("url")
    val driver = dbProps.getProperty("driverClass")


    File("out/base").mkdirs()

    val tblProps = Properties()
    tblProps.load(Properties::class.java.getResourceAsStream("/table.properties"))
    for (propKey in tblProps.propertyNames()) {

        val fromTableName = propKey as String

        val value = tblProps.getProperty(fromTableName)
        val index = value.lastIndexOf(".")

        val toPackage = value.substring(0,index)
        val toEntityClass = value.substring(index + 1)
        println("fromTableName=${fromTableName},toPackage=${toPackage},toEntityClass=${toEntityClass}")


        val tbl = TableSchemaLoader.load(url, driver, dbProps, fromTableName)
        println(tbl)


        println("#################################")
        println("${tbl.tableName} start")

        if (tbl.needEmbeddedId) {
            val pkText = tbl.createEntity("kotlin-pk-templete.txt", toPackage, toEntityClass)
            File("out/${toEntityClass}Pk.kt").writeText(pkText, Charsets.UTF_8)
        }


        val basetext = tbl.createEntity("kotlin-entity-base-templete.txt",toPackage, toEntityClass)
        File("out/base/Abstract${toEntityClass}.kt").writeText(basetext, Charsets.UTF_8)

        val text = tbl.createEntity("kotlin-entity-templete.txt",toPackage, toEntityClass)
        File("out/${toEntityClass}.kt").writeText(text, Charsets.UTF_8)

        println("${tbl.tableName} end")
        println("#################################")


    }







}

