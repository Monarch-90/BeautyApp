package com.tatiana.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


/** Сущность (таблица) для хранения записей расписания */

@Entity(
    tableName = "appointments",

    foreignKeys = [

        // Внешний ключ для связи с таблицей клиентов
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"], // Поле в таблице clients
            childColumns = ["clientId"], // Поле в этой таблице (appointments)
            onDelete = ForeignKey.SET_NULL // Если клиент удален, ставим clientId в NULL
        ),

        // Внешний ключ для связи с таблицей услуг
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"], // Поле в таблице services
            childColumns = ["serviceId"], // Поле в этой таблице
            onDelete = ForeignKey.SET_NULL // Если услуга удалена, ставим serviceId в NULL
        )],

    // Индексы для ускорения запросов по внешним ключам и дате
    indices = [Index(value = ["clientId"]), Index(value = ["serviceId"]), Index(value = ["date"])]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Уникальный ID записи

    // --- Основные данные ---
    val clientId: Long?, // ID клиента (связь с Client)
    val serviceId: Long?, // ID услуги (связь с Service)
    val date: Long,      // Дата записи (например, кол-во дней с эпохи или timestamp начала дня)
    val startTimeMillis: Long, // Точное время начала UTC в миллисекундах
    val durationMinutes: Int,  // Длительность в минутах

    // --- Данные, специфичные для этой записи ---
    val notes: String?, // Примечание к записи
    val cost: Long?,     // Финальная стоимость (может отличаться от стандартной цены услуги)
    val currencyCode: String?, // Валюта финальной стоимости
    val status: String, // Статус ("Запланировано", "Выполнено"...)

    // --- Поля для данных 2-х дополнительных столбцов ---
    val optionalField1Value: String?, // Значение для первого доп. столбца
    val optionalField2Value: String?,  // Значение для второго доп. столбца
) {
    // Статус по умолчанию при создании новой записи
    companion object {
        const val DEFAULT_STATUS = "Запланировано"
    }
}