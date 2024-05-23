package com.project.steamfarm.repository

interface Repository<M> {
    fun findAll(): List<M>
    fun findById(id: String): M?
    fun save(data: M)
}