package com.tatiana.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tatiana.data.database.entity.Client
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с клиентами (Client).
 */

@Dao
interface ClientDao {

    // Вставить нового клиента (или заменить, если ID совпал)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long

    // Обновить данные клиента
    @Update
    suspend fun updateClient(client: Client)

    // Удалить клиента
    @Delete
    suspend fun deleteClient(client: Client)

    // Получить клиента по ID
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: Long): Client?

    // Получить всех клиентов, отсортированных по имени (возвращает Flow)
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>

    // Получить всех клиентов для поиска (возвращает обычный список)
    // Используется, например, для заполнения выпадающего списка в Расписании
    @Query("SELECT * FROM clients ORDER BY name ASC")
    suspend fun getAllClientsList(): List<Client>

    // Поиск клиентов по имени (для автодополнения/поиска)
    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchClientsByName(query: String): Flow<List<Client>>
}