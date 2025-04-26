package com.tatiana.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tatiana.data.database.dao.AppointmentDao
import com.tatiana.data.database.dao.ClientDao
import com.tatiana.data.database.dao.ColumnConfigurationDao
import com.tatiana.data.database.dao.ServiceDao
import com.tatiana.data.database.entity.Appointment
import com.tatiana.data.database.entity.Client
import com.tatiana.data.database.entity.ColumnConfiguration
import com.tatiana.data.database.entity.Service
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Реализация репозитория для экрана Расписания.
 * @Inject constructor - Hilt предоставит экземпляры DAO.
 */

@Singleton // Репозиторий обычно один на все приложение
class ScheduleRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val columnConfigDao: ColumnConfigurationDao,
    private val clientDao: ClientDao, // Добавили DAO для клиентов
    private val serviceDao: ServiceDao, // Добавили DAO для услуг
) : ScheduleRepository {

    override fun getAppointmentsPagingSource(): Flow<PagingData<Appointment>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { appointmentDao.getAppointmentsPagingSource(/* TODO: Передать фильтры */) }
        ).flow
    }

    override fun getVisibleColumnsConfiguration(screenName: String): Flow<List<ColumnConfiguration>> {
        return columnConfigDao.getVisibleConfigurationForScreen(screenName)
    }

    override fun getHiddenColumnsConfiguration(screenName: String): Flow<List<ColumnConfiguration>> {
        return columnConfigDao.getHiddenConfigurationForScreen(screenName)
    }

    override suspend fun updateColumnConfiguration(config: ColumnConfiguration) {
        columnConfigDao.updateConfiguration(config)
    }

    override suspend fun updateColumnConfigurations(configs: List<ColumnConfiguration>) {
        columnConfigDao.updateConfigurations(configs)
    }

    override suspend fun insertAppointment(appointment: Appointment): Long {
        return appointmentDao.insertAppointment(appointment)
    }

    override suspend fun updateAppointment(appointment: Appointment) {
        appointmentDao.updateAppointment(appointment)
    }

    // Добавляем реализацию получения клиента и услуги
    override suspend fun getClientById(clientId: Long): Client? {
        return clientDao.getClientById(clientId)
    }

    override suspend fun getServiceById(serviceId: Long): Service? {
        return serviceDao.getServiceById(serviceId)
    }

    // TODO: Реализовать остальные методы интерфейса при необходимости
}