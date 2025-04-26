package com.tatiana.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/** Сущность (таблица) для хранения конфигурации столбцов для разных экранов */

@Entity(tableName = "column_configurations")
data class ColumnConfiguration(

    // Составной первичный ключ: ID столбца уникален в рамках одного экрана
    @PrimaryKey val columnId: String, // Уникальный идентификатор столбца (напр., "date", "client", "optional_1")
    val screenName: String, // Идентификатор экрана (напр., "schedule", "clients")

    var userTitle: String, // Название столбца, которое видит пользователь (можно менять для доп. столбцов)
    var isVisible: Boolean, // Виден ли столбец
    var displayOrder: Int, // Порядок отображения (0, 1, 2...)

    /**
     * Имя поля в сущности Appointment, откуда брать данные для этого столбца.
     * Может быть null для столбцов, которые требуют специальной обработки (как 'client' или 'contacts').
     */
    val dataFieldName: String?, // Поле для связи с реальным полем в Appointment
) {
    companion object {
        // Константы для ID базовых столбцов Расписания
        const val COL_ID_DATE = "date"
        const val COL_ID_CLIENT = "client" // Требует загрузки Client
        const val COL_ID_TIME = "time" // Будет использовать startTimeMillis
        const val COL_ID_DURATION = "duration" // Будет использовать durationMinutes
        const val COL_ID_SERVICE = "service" // Требует загрузки Service
        const val COL_ID_NOTES = "notes"
        const val COL_ID_CONTACTS = "contacts" // Отображает данные из Client
        const val COL_ID_COST = "cost"
        const val COL_ID_STATUS = "status"

        // Константы для ID дополнительных столбцов
        const val COL_ID_OPTIONAL_1 = "optional_1"
        const val COL_ID_OPTIONAL_2 = "optional_2"

        const val SCREEN_SCHEDULE = "schedule" // Имя экрана Расписания

        // Функция для создания начальной конфигурации столбцов для Расписания
        fun createInitialScheduleConfig(): List<ColumnConfiguration> {
            return listOf(
                ColumnConfiguration(COL_ID_DATE, SCREEN_SCHEDULE, "Дата", true, 0, "date"),
                ColumnConfiguration(COL_ID_CLIENT, SCREEN_SCHEDULE, "Клиент", true, 1, null), // Данные из Client
                ColumnConfiguration(COL_ID_TIME, SCREEN_SCHEDULE, "Время", true, 2, "startTimeMillis"), // Связываем с полем времени
                ColumnConfiguration(COL_ID_DURATION, SCREEN_SCHEDULE, "Длительность", true, 3, "durationMinutes"), // Связываем с полем длительности
                ColumnConfiguration(COL_ID_SERVICE, SCREEN_SCHEDULE, "Услуга", true, 4, null), // Данные из Service
                ColumnConfiguration(COL_ID_NOTES, SCREEN_SCHEDULE, "Примечание", true, 5, "notes"),
                ColumnConfiguration(COL_ID_CONTACTS, SCREEN_SCHEDULE, "Контакты", true, 6, null), // Данные из Client
                ColumnConfiguration(COL_ID_COST, SCREEN_SCHEDULE, "Стоимость", true, 7, "cost"), // Использует cost и currencyCode
                ColumnConfiguration(COL_ID_STATUS, SCREEN_SCHEDULE, "Статус", true, 8, "status"),

                // Дополнительные столбцы изначально скрыты
                ColumnConfiguration(COL_ID_OPTIONAL_1, SCREEN_SCHEDULE, "Новый столбец", false, 9, "optionalField1Value"),
                ColumnConfiguration(COL_ID_OPTIONAL_2, SCREEN_SCHEDULE, "Новый столбец", false, 10, "optionalField2Value")
            )
        }
    }
}