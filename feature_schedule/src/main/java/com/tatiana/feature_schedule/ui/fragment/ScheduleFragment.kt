package com.tatiana.feature_schedule.ui.fragment


import android.os.Bundle
import android.view.*
import android.widget.Toast // Для временных сообщений
import androidx.core.view.MenuProvider // Новый способ работы с меню Toolbar во фрагментах
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder // Красивые диалоги
import com.tatiana.data.database.entity.Appointment
import com.tatiana.data.database.entity.ColumnConfiguration
import com.tatiana.feature_schedule.databinding.FragmentScheduleBinding
import com.tatiana.feature_schedule.ui.adapter.ScheduleAdapter
import com.tatiana.feature_schedule.ui.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.tatiana.feature_schedule.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment(), ScheduleAdapter.OnItemInteractionListener { // Реализуем интерфейс

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModels()
    private var scheduleAdapter: ScheduleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar() // Настраиваем Toolbar и его меню
        observeViewModel()

    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(this) // Передаем 'this' как listener
        binding.recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
            // Можно добавить разделитель между строками, если нужно
            // addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupToolbar() {
        // Настройка клика на иконку навигации (гамбургер)
        binding.toolbar.setNavigationOnClickListener {
            // TODO: Открыть Navigation Drawer
            Toast.makeText(context, "Открыть меню", Toast.LENGTH_SHORT).show()
        }

        // Добавляем обработчик для элементов меню Toolbar (новый способ)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Инфлейтим наше меню
                menuInflater.inflate(R.menu.schedule_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Обрабатываем нажатия на элементы меню
                return when (menuItem.itemId) {
                    R.id.action_add_column -> {
                        showAddColumnDialog() // Показываем диалог добавления столбца
                        true // Сообщаем, что событие обработано
                    }
                    R.id.action_filter -> {
                        showFilterDialog() // Показываем диалог фильтра
                        true
                    }
                    // TODO: Добавить обработку поиска
                    else -> false // Передаем обработку другим компонентам
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED) // Привязываем к жизненному циклу
    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.appointmentsPagingFlow.collectLatest { pagingData ->
                        scheduleAdapter?.submitData(pagingData)
                    }
                }
                launch {
                    viewModel.visibleColumnsFlow.collectLatest { columns ->
                        scheduleAdapter?.submitColumns(columns)
                    }
                }
                // Наблюдаем за фильтрами для обновления UI (если нужно)
                launch {
                    viewModel.selectedYear.collect { year ->
                        // TODO: Обновить UI, отображающий текущий год фильтра
                        binding.toolbar.subtitle = "Фильтр: ${viewModel.selectedMonth.value + 1}/$year" // Пример
                    }
                }
                launch {
                    viewModel.selectedMonth.collect { month ->
                        // TODO: Обновить UI, отображающий текущий месяц фильтра
                        binding.toolbar.subtitle = "Фильтр: ${month + 1}/${viewModel.selectedYear.value}" // Пример
                    }
                }
            }
        }
    }

    // --- Реализация интерфейса ScheduleAdapter.OnItemInteractionListener ---

    override fun onCellClick(appointment: Appointment, columnConfig: ColumnConfiguration) {
        // TODO: Реализовать логику открытия СООТВЕТСТВУЮЩЕГО диалога/редактора
        //       в зависимости от columnConfig.columnId
        val message = "Клик на столбец: ${columnConfig.userTitle}\n" +
                "Запись ID: ${appointment.id}\n" +
                "Поле: ${columnConfig.dataFieldName ?: columnConfig.columnId}"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        when (columnConfig.columnId) {
            ColumnConfiguration.COL_ID_DATE -> { /* Открыть DatePickerDialog */ }
            ColumnConfiguration.COL_ID_CLIENT -> { /* Открыть ClientPickerDialog */ }
            ColumnConfiguration.COL_ID_TIME -> { /* Открыть TimePickerDialog */ }
            // ... и так далее для всех интерактивных столбцов
            ColumnConfiguration.COL_ID_OPTIONAL_1, ColumnConfiguration.COL_ID_OPTIONAL_2 -> {
                // Открыть простой EditText диалог
                showEditTextDialog(appointment, columnConfig)
            }
            else -> { /* Для нередактируемых или пока не реализованных */ }
        }
    }

    // --- Пример диалогов (очень базовые) ---

    private fun showFilterDialog() {
        // TODO: Создать нормальный диалог с выбором года и месяца
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Фильтр")
            .setMessage("Здесь будет выбор Года и Месяца\nТекущий: ${viewModel.selectedMonth.value + 1}/${viewModel.selectedYear.value}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAddColumnDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Получаем список скрытых столбцов однократно
            val hiddenColumns = viewModel.hiddenColumnsFlow.first() // Берем первое значение Flow

            if (hiddenColumns.isEmpty()) {
                Toast.makeText(context, "Нет скрытых столбцов для добавления", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Готовим массив названий для диалога
            val items = hiddenColumns.map { it.userTitle }.toTypedArray()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Добавить столбец")
                .setItems(items) { dialog, which ->
                    // Пользователь выбрал столбец `hiddenColumns[which]`
                    val selectedColumn = hiddenColumns[which]
                    viewModel.showColumn(selectedColumn) // Делаем его видимым
                    // TODO: Сразу открыть диалог переименования для selectedColumn
                    Toast.makeText(context, "Добавлен: ${selectedColumn.userTitle}. TODO: Переименовать!", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    // Пример простого диалога для редактирования текста (для доп. столбцов)
    private fun showEditTextDialog(appointment: Appointment, config: ColumnConfiguration) {
        val editText = android.widget.EditText(context).apply {
            // Устанавливаем текущее значение
            setText(
                if (config.columnId == ColumnConfiguration.COL_ID_OPTIONAL_1) appointment.optionalField1Value else appointment.optionalField2Value
            )
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактировать: ${config.userTitle}")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newValue = editText.text.toString()
                val updatedAppointment = when(config.columnId) {
                    ColumnConfiguration.COL_ID_OPTIONAL_1 -> appointment.copy(optionalField1Value = newValue)
                    ColumnConfiguration.COL_ID_OPTIONAL_2 -> appointment.copy(optionalField2Value = newValue)
                    else -> appointment // На всякий случай
                }
                viewModel.updateAppointment(updatedAppointment) // Обновляем запись в БД
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewSchedule.adapter = null
        _binding = null
    }
}