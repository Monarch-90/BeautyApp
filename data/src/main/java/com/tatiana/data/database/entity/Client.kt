package com.tatiana.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


 /** Сущность (таблица) для хранения информации о клиентах */

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Уникальный ID клиента

    val name: String?,
    val phone: String?,
    val telegram: String?,
    val viber: String?,
    val whatsapp: String?,
    val instagram: String?,
    val notes: String?,
)