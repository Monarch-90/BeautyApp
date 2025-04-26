package com.tatiana.data.repository

import androidx.paging.PagingData
import com.tatiana.data.database.entity.Appointment
import com.tatiana.data.database.entity.Client
import com.tatiana.data.database.entity.ColumnConfiguration
import com.tatiana.data.database.entity.Service
import kotlinx.coroutines.flow.Flow

// Интерфейс репозитория (чтобы ViewModel не зависела от конкретной реализации)
interface ScheduleRepository {
    fun getAppointmentsPagingSource(): Flow<PagingData<Appointment>>
    fun getVisibleColumnsConfiguration(screenName: String): Flow<List<ColumnConfiguration>>
    fun getHiddenColumnsConfiguration(screenName: String): Flow<List<ColumnConfiguration>>
    suspend fun updateColumnConfiguration(config: ColumnConfiguration)
    suspend fun updateColumnConfigurations(configs: List<ColumnConfiguration>)
    suspend fun insertAppointment(appointment: Appointment): Long
    suspend fun updateAppointment(appointment: Appointment)
    suspend fun getClientById(clientId: Long): Client? // Для получения данных клиента
    suspend fun getServiceById(serviceId: Long): Service? // Для получения данных услуги
    // Добавьте другие методы по мере необходимости (delete, get clients list, etc.)
}