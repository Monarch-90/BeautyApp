package com.tatiana.feature_schedule.ui.viewmodel

import androidx.lifecycle.SavedStateHandle // Для сохранения состояния фильтров
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tatiana.data.database.entity.Appointment
import com.tatiana.data.database.entity.ColumnConfiguration
import com.tatiana.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar // Для работы с датами
import javax.inject.Inject

// Ключи для сохранения состояния фильтров
private const val KEY_FILTER_YEAR = "filter_year"
private const val KEY_FILTER_MONTH = "filter_month" // 0-11

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository, // Внедряем репозиторий
    private val savedStateHandle: SavedStateHandle // Для сохранения состояния
) : ViewModel() {

    // --- Фильтры ---
    // Используем SavedStateHandle для автоматического сохранения/восстановления
    val selectedYear: StateFlow<Int> = savedStateHandle.getStateFlow(
        KEY_FILTER_YEAR,
        Calendar.getInstance().get(Calendar.YEAR) // По умолчанию текущий год
    )
    val selectedMonth: StateFlow<Int> = savedStateHandle.getStateFlow(
        KEY_FILTER_MONTH,
        Calendar.getInstance().get(Calendar.MONTH) // По умолчанию текущий месяц (0-11)
    )

    // Функция для обновления фильтров из UI
    fun setFilter(year: Int, month: Int) {
        savedStateHandle[KEY_FILTER_YEAR] = year
        savedStateHandle[KEY_FILTER_MONTH] = month
        // TODO: Нужно перезапустить PagingSource с новыми фильтрами.
        // Paging 3 обычно делает это сам, если PagingSource зависит от Flow/StateFlow фильтров.
        // Или можно использовать refresh() у PagingDataAdapter.
    }

    // --- Данные для списка ---
    // TODO: Обновить PagingSource в репозитории, чтобы он учитывал selectedYear/selectedMonth
    val appointmentsPagingFlow: Flow<PagingData<Appointment>> =
        repository.getAppointmentsPagingSource() // Пока без фильтров
            .cachedIn(viewModelScope)

    // --- Конфигурация столбцов ---
    val visibleColumnsFlow: StateFlow<List<ColumnConfiguration>> =
        repository.getVisibleColumnsConfiguration(ColumnConfiguration.SCREEN_SCHEDULE)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val hiddenColumnsFlow: Flow<List<ColumnConfiguration>> =
        repository.getHiddenColumnsConfiguration(ColumnConfiguration.SCREEN_SCHEDULE)


    // --- Методы для действий пользователя ---

    // Пример: Обновление существующей записи (вызывается после редактирования в диалоге)
    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.updateAppointment(appointment)
        }
    }

    // Пример: Добавление новой записи
    fun addAppointment(newAppointmentData: Appointment) { // Передаем объект с данными
        viewModelScope.launch {
            // Можно добавить логику установки значений по умолчанию
            val appointmentToInsert = newAppointmentData.copy(
                status = Appointment.DEFAULT_STATUS // Устанавливаем статус по умолчанию
            )
            repository.insertAppointment(appointmentToInsert)
        }
    }

    // Пример: Сделать столбец видимым (вызывается после выбора в диалоге "+")
    fun showColumn(columnConfig: ColumnConfiguration) {
        viewModelScope.launch {
            // Получаем текущую конфигурацию видимых столбцов, чтобы определить новый порядок
            val currentVisible = visibleColumnsFlow.value
            val updatedConfig = columnConfig.copy(
                isVisible = true,
                displayOrder = (currentVisible.maxOfOrNull { it.displayOrder } ?: -1) + 1 // Ставим в конец
            )
            repository.updateColumnConfiguration(updatedConfig)
            // TODO: Возможно, нужно обновить displayOrder у других столбцов, если вставляем не в конец
        }
    }

    // Пример: Скрыть столбец
    fun hideColumn(columnConfig: ColumnConfiguration) {
        viewModelScope.launch {
            val updatedConfig = columnConfig.copy(isVisible = false)
            repository.updateColumnConfiguration(updatedConfig)
            // TODO: Нужно обновить displayOrder у столбцов, идущих после скрытого
        }
    }

    // Пример: Переименовать опциональный столбец
    fun renameOptionalColumn(columnConfig: ColumnConfiguration, newTitle: String) {
        // Проверяем, что это действительно опциональный столбец
        if (columnConfig.columnId == ColumnConfiguration.COL_ID_OPTIONAL_1 ||
            columnConfig.columnId == ColumnConfiguration.COL_ID_OPTIONAL_2) {
            viewModelScope.launch {
                val updatedConfig = columnConfig.copy(userTitle = newTitle)
                repository.updateColumnConfiguration(updatedConfig)
            }
        } else {
            // Логика обработки ошибки - нельзя переименовать базовый столбец
            println("Error: Cannot rename base column ${columnConfig.columnId}")
        }
    }

    // TODO: Добавить метод для перемещения столбцов (потребует обновления displayOrder у нескольких)
    // TODO: Добавить методы для получения данных связанных сущностей (Client, Service) для отображения
    //       (Хотя это лучше делать в адаптере или через специальные data классы)

}