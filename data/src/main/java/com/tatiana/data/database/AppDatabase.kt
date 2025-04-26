package com.tatiana.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tatiana.data.database.dao.AppointmentDao
import com.tatiana.data.database.dao.ClientDao
import com.tatiana.data.database.dao.ColumnConfigurationDao
import com.tatiana.data.database.dao.ServiceDao
import com.tatiana.data.database.entity.Appointment
import com.tatiana.data.database.entity.Client
import com.tatiana.data.database.entity.ColumnConfiguration
import com.tatiana.data.database.entity.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Главный класс базы данных Room.
 * Объявляет все Entities (таблицы) и DAOs (интерфейсы доступа).
 */

@Database(
    entities = [
        Appointment::class,
        Client::class,
        Service::class,
        ColumnConfiguration::class
    ],
    version = 1, // Увеличивайте версию при изменении схемы БД
    exportSchema = false // Можно включить для отслеживания истории схемы
)
abstract class AppDatabase : RoomDatabase() {

    // Абстрактные методы для получения DAO
    abstract fun appointmentDao(): AppointmentDao
    abstract fun clientDao(): ClientDao
    abstract fun serviceDao(): ServiceDao
    abstract fun columnConfigurationDao(): ColumnConfigurationDao

    companion object {
        @Volatile // Гарантирует, что значение видно всем потокам
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Возвращаем существующий экземпляр или создаем новый (синглтон)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "beauty_app_database" // Имя файла БД
                )
                    .addCallback(AppDatabaseCallback()) // Добавляем Callback для начального заполнения
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback для начального заполнения данных
    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Передаем конкретный DAO в метод заполнения
                    populateInitialData(database.columnConfigurationDao())
                }
            }
        }

        // Метод для начального заполнения конфигурации столбцов
        suspend fun populateInitialData(columnDao: ColumnConfigurationDao) { // Тип параметра должен быть ColumnConfigurationDao
            columnDao.insertOrUpdateConfigurations(ColumnConfiguration.createInitialScheduleConfig())
        }
    }
}