package com.grachro.t2entity

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

data class MetaTable(val tableName:String, val primaryKyeNames:Set<String>, val cols:List<MetaColumn>) {

    val needEmbeddedId:Boolean
    get() {
        return primaryKyeNames.size > 1
    }

    val singlePkCol:MetaColumn
        get() = cols.first()

    val pkCols:List<MetaColumn>
        get() = cols.filter { it.pk }


    val pkColDefaultValues:String
    get() {
        val list:List<String> = pkCols.map {it.kotlinDefaultValue  }
        return list.joinToString(",")
    }

    val normalCols:List<MetaColumn>
        get() = cols.filter { !it.pk }

    val hasTimestampNormalField:Boolean
        get() = normalCols.filter { it.entityType == ColumnType.TIMESTAMP }.isNotEmpty()

    val hasTimestampPkField:Boolean
        get() = normalCols.filter { it.entityType == ColumnType.TIMESTAMP }.isNotEmpty()

    fun createEntity(templete:String,toPackage:String,toEntityClass:String) : String {

        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.templateMode = TemplateMode.TEXT
        templateResolver.isCacheable = false

        val templateEngine = TemplateEngine()
        templateEngine.addTemplateResolver(templateResolver)

        val ctx = Context()
        ctx.setVariable("package", toPackage)
        ctx.setVariable("entityClass", toEntityClass)
        ctx.setVariable("tableName", tableName)
        ctx.setVariable("table", this)

        return templateEngine.process(templete, ctx)
    }



}

