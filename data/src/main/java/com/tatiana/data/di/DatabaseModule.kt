package com.tatiana.data.di

import android.content.Context
import com.tatiana.data.database.AppDatabase
import com.tatiana.data.database.dao.AppointmentDao
import com.tatiana.data.database.dao.ClientDao
import com.tatiana.data.database.dao.ColumnConfigurationDao
import com.tatiana.data.database.dao.ServiceDao
import com.tatiana.data.repository.ScheduleRepository
import com.tatiana.data.repository.ScheduleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Hilt модуль для предоставления зависимостей, связанных с базой данных.
 * @InstallIn(SingletonComponent::class) означает, что зависимости будут жить, пока живо приложение.
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Предоставляет экземпляр AppDatabase (синглтон).
     * Hilt автоматически предоставит @ApplicationContext.
     */
    @Provides
    @Singleton // Гарантирует, что будет только один экземпляр БД
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    /**
     * Предоставляет AppointmentDao.
     * Hilt знает, как создать AppDatabase (см. метод выше),
     * поэтому он сможет вызвать этот метод.
     */
    @Provides
    fun provideAppointmentDao(appDatabase: AppDatabase): AppointmentDao = appDatabase.appointmentDao()

    /**
     * Предоставляет ClientDao.
     */
    @Provides
    fun provideClientDao(appDatabase: AppDatabase): ClientDao = appDatabase.clientDao()

    /**
     * Предоставляет ServiceDao.
     */
    @Provides
    fun provideServiceDao(appDatabase: AppDatabase): ServiceDao = appDatabase.serviceDao()

    /**
     * Предоставляет ColumnConfigurationDao.
     */
    @Provides
    fun provideColumnConfigurationDao(appDatabase: AppDatabase): ColumnConfigurationDao = appDatabase.columnConfigurationDao()


    // Предоставляем реализацию репозитория
    // Hilt автоматически подставит DAO в конструктор ScheduleRepositoryImpl
    @Provides
    @Singleton
    fun provideScheduleRepository(
        appointmentDao: AppointmentDao,
        columnConfigDao: ColumnConfigurationDao,
        clientDao: ClientDao,
        serviceDao: ServiceDao,
    ): ScheduleRepository { // Возвращаем интерфейс!
        return ScheduleRepositoryImpl(appointmentDao, columnConfigDao, clientDao, serviceDao)
    }
}