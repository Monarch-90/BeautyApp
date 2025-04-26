package com.tatiana.data.database.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tatiana.data.database.entity.Service
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с услугами (Service).
 */
@Dao
interface ServiceDao {

    // Вставить новую услугу (или заменить)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service): Long

    // Обновить услугу
    @Update
    suspend fun updateService(service: Service)

    // Удалить услугу
    @Delete
    suspend fun deleteService(service: Service)

    // Удалить несколько услуг (для массового удаления)
    @Delete
    suspend fun deleteServices(services: List<Service>)

    // Получить услугу по ID
    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: Long): Service?

    // Получить все услуги, отсортированные по названию (возвращает Flow)
    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAllServices(): Flow<List<Service>>

    // Получить все услуги (обычный список)
    @Query("SELECT * FROM services ORDER BY name ASC")
    suspend fun getAllServicesList(): List<Service>

    // Поиск услуг по названию
    @Query("SELECT * FROM services WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchServicesByName(query: String): Flow<List<Service>>
}