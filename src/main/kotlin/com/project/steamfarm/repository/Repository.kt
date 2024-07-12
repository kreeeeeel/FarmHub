package com.project.steamfarm.repository

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.steamfarm.adapter.LocalDateTimeAdapter
import java.time.LocalDateTime

val PATH_REPOSITORY = System.getProperty("user.dir") + "/repository"

interface Repository<M> {

    val gson: Gson
        get() = GsonBuilder().setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

    fun findAll(): List<M>
    fun findById(id: String): M?
    fun delete(data: M)
    fun save(data: M)
}