package com.tatiana.data.database.dao

import androidx.room.*
import com.tatiana.data.database.entity.ColumnConfiguration
import kotlinx.coroutines.flow.Flow

@Dao
interface ColumnConfigurationDao {

    // Вставить или заменить конфигурацию (список)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfigurations(configs: List<ColumnConfiguration>)

    // Вставить или заменить одну запись конфигурации
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfiguration(config: ColumnConfiguration)

    // Получить конфигурацию для конкретного экрана, отсортированную по порядку
    // Возвращает Flow, чтобы UI автоматически обновлялся при изменении конфигурации
    @Query("SELECT * FROM column_configurations WHERE screenName = :screenName ORDER BY displayOrder ASC")
    fun getConfigurationForScreen(screenName: String): Flow<List<ColumnConfiguration>>

    // Получить только видимые столбцы для экрана
    @Query("SELECT * FROM column_configurations WHERE screenName = :screenName AND isVisible = 1 ORDER BY displayOrder ASC")
    fun getVisibleConfigurationForScreen(screenName: String): Flow<List<ColumnConfiguration>>

    // Получить скрытые столбцы для экрана (для кнопки "+")
    @Query("SELECT * FROM column_configurations WHERE screenName = :screenName AND isVisible = 0 ORDER BY displayOrder ASC")
    fun getHiddenConfigurationForScreen(screenName: String): Flow<List<ColumnConfiguration>>

    // Обновить одну запись конфигурации (например, при переименовании или скрытии)
    @Update
    suspend fun updateConfiguration(config: ColumnConfiguration)

    // Обновить несколько записей (например, при изменении порядка)
    @Update
    suspend fun updateConfigurations(configs: List<ColumnConfiguration>)

    // Получить конкретную конфигурацию столбца (если нужно)
    @Query("SELECT * FROM column_configurations WHERE columnId = :columnId AND screenName = :screenName")
    suspend fun getColumnConfig(columnId: String, screenName: String): ColumnConfiguration?
}