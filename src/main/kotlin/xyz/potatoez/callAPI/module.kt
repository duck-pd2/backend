package xyz.potatoez.callAPI

import kotlinx.serialization.*

@Serializable
data class Course (val y: String, val dn: String,
                   val sn: Int?, val ca: String,
                   val sc: String, val g: Int,
                   val fc: String?, val fg: String?,
                   val ct: String, val cn: String,
                   val ci: String?, val cl: String?,
                   val tg: List<String>?, val c: Float?,
                   val r: Boolean?, val i: List<String>?,
                   val s: Int?, val a: Int?,
                   val t: List<String>?)

@Serializable
data class CourseSearchResponse(val success: Boolean,
                                val data: List<Course>?,
                                val msg: String? = null, val code: Int,
                                val err: List<String>? = null,
                                val warn: List<String>? = null)

@Serializable
data class Dept(val nameTW: String, val nameEN: String, val dept: List<List<String>>)

@Serializable
data class DeptGroup(val deptGroup: List<Dept>, val deptCount: Int)

@Serializable
data class DeptSearchResponse(val success: Boolean, val data: DeptGroup?,
                              val msg: String? = null, val code: Int,
                              val err: List<String>? = null,
                              val warn: List<String>? = null)