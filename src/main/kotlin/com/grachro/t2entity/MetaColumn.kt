package com.grachro.t2entity

import com.google.common.base.CaseFormat

data class MetaColumn(val name: String, val type: String, val nullable: Boolean, val autoIncrement: Boolean, var pk: Boolean) {

    val field: String
        get() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name.toUpperCase())
        }

    val entityType: ColumnType = ColumnType.get(this.type)

    val kotlinPrefix: String
        get() {
            return if (this.entityType.primitive) {
                "var"
            } else {
                if (this.nullable) "var" else "lateinit var"
            }
        }


    val kotlinSuffix: String
        get() {

            val nullable = if (this.nullable) "?" else ""
            val defaultValue = if (this.nullable) " = null" else ""

            return when (this.entityType) {
                ColumnType.INT -> "Int${kotlinIntDefalult}" //Int or Int? = 0
                ColumnType.STRING -> "String${nullable}${defaultValue}"//String or String?
                ColumnType.BOOLEAN -> "Boolean${kotlinBooleanDefalult}"//Boolean or Boolean?
                ColumnType.TIMESTAMP -> "Timestamp${nullable}${defaultValue}"//Timestamp or String?
                else -> "${this.type}${nullable}"
            }
        }

    val kotlinIntDefalult: String
        get() {
            return if (this.nullable) "? = null" else " = 0"
        }

    val kotlinStringDefalult: String
        get() {
            return if (this.nullable) "? = null" else " = \"\""
        }

    val kotlinBooleanDefalult: String
        get() {
            return if (this.nullable) "? = null" else " = false"
        }

    val kotlinPkSuffix: String
        get() {

            val defaultValue = if (this.nullable) " = null" else ""

            return when (this.entityType) {
                ColumnType.INT -> "Int${kotlinIntDefalult}" //Int = 0
                ColumnType.STRING -> "String${kotlinStringDefalult}"//String = ""
                ColumnType.TIMESTAMP -> "Timestamp${nullable}${defaultValue}"
                ColumnType.TIMESTAMP -> "Boolean${kotlinBooleanDefalult}"
                else -> "${this.type}${nullable}"
            }
        }

    val kotlinDefaultValue: String
        get() {

            return when (this.entityType) {
                ColumnType.INT -> "0"
                ColumnType.STRING -> "\"\""
                ColumnType.TIMESTAMP -> "Timestamp(0)"
                ColumnType.BOOLEAN -> "false"
                else -> "\"\""
            }
        }
}