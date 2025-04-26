package com.tatiana.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tatiana.data.database.entity.Appointment
import kotlinx.coroutines.flow.Flow


/** DAO для работы с записями расписания (Appointment) */

@Dao
interface AppointmentDao {

    // Вставить новую запись (или заменить, если ID совпал)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long // Возвращает ID вставленной записи

    // Обновить существующую запись
    @Update
    suspend fun updateAppointment(appointment: Appointment)

    // Удалить запись
    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    // Получить конкретную запись по ID (если нужно)
    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Long): Appointment?

    // Получить все записи для конкретного клиента (возвращает Flow для автообновления)
    @Query("SELECT * FROM appointments WHERE clientId = :clientId ORDER BY date DESC, startTimeMillis DESC")
    fun getAppointmentsForClient(clientId: Long): Flow<List<Appointment>>

    // Получить источник данных для Paging 3
    // Запрос будет выбирать все записи, отсортированные по дате и времени начала
    // Здесь можно будет добавить фильтрацию по дате (году/месяцу)
    // TODO: Добавить параметры Long? yearMillis, Long? monthMillis для фильтрации
    @Query("SELECT * FROM appointments ORDER BY date DESC, startTimeMillis DESC")
    fun getAppointmentsPagingSource(): PagingSource<Int, Appointment>
}