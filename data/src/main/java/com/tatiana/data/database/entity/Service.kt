package com.tatiana.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/** Сущность (таблица) для хранения информации об услугах */

@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Уникальный ID услуги

    val name: String, // Название услуги
    val cost: Long?, // Стоимость в копейках/центах (Long для точности)
    val currencyCode: String?, // Код валюты (например, "BYN", "USD", "EUR")
    val defaultDurationMinutes: Int?, // Дефолтная продолжительность услуги
)