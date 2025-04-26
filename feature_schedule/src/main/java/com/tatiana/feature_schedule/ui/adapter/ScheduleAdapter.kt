package com.tatiana.feature_schedule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tatiana.data.database.entity.Appointment
import com.tatiana.data.database.entity.ColumnConfiguration
import com.tatiana.feature_schedule.databinding.ItemScheduleRowBinding
import java.text.SimpleDateFormat
import com.tatiana.feature_schedule.R
import java.util.*

class ScheduleAdapter(
    private val listener: OnItemInteractionListener // Интерфейс для кликов
) : PagingDataAdapter<Appointment, ScheduleAdapter.AppointmentViewHolder>(APPOINTMENT_DIFF_CALLBACK) {

    interface OnItemInteractionListener {
        // Вызывается при клике на ячейку
        fun onCellClick(appointment: Appointment, columnConfig: ColumnConfiguration)
        // TODO: Добавить методы для клика на заголовок (для меню)
    }

    private var columns: List<ColumnConfiguration> = emptyList()
    // Храним контекст для создания View программно
    private var context: Context? = null

    fun submitColumns(newColumns: List<ColumnConfiguration>) {
        val oldColumns = columns
        columns = newColumns
        // Если изменился только порядок или видимость, но не сами данные строк,
        // лучше перерисовать все видимые элементы.
        // Это все еще не оптимально, но лучше, чем было.
        if (oldColumns != newColumns) { // Проверяем, изменилось ли что-то
            notifyItemRangeChanged(0, itemCount) // Перерисовать все видимые элементы
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context // Получаем контекст
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemScheduleRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppointmentViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = getItem(position)
        if (appointment != null) {
            holder.bind(appointment, columns)
        }
    }

    inner class AppointmentViewHolder(
        private val binding: ItemScheduleRowBinding,
        private val listener: OnItemInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        // Для форматирования даты и времени
        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(appointment: Appointment, columnConfig: List<ColumnConfiguration>) {
            // Очищаем предыдущие ячейки
            binding.linearLayoutCells.removeAllViews()

            // Динамически создаем ячейки для каждого видимого столбца
            columnConfig.forEach { config ->
                // Создаем TextView для ячейки (позже можно усложнить)
                val cellView = createCellTextView(config, appointment)
                // Добавляем обработчик клика
                cellView.setOnClickListener {
                    listener.onCellClick(appointment, config)
                }
                // Добавляем ячейку в LinearLayout
                binding.linearLayoutCells.addView(cellView)

                // Добавляем разделитель (вертикальную линию) между ячейками, кроме последней
                if (columnConfig.indexOf(config) < columnConfig.size - 1) {
                    val separator = View(context)
                    val params = LinearLayout.LayoutParams(1.dpToPx(), LinearLayout.LayoutParams.MATCH_PARENT)
                    params.setMargins(4.dpToPx(), 0, 4.dpToPx(), 0) // Отступы для разделителя
                    separator.layoutParams = params
                    separator.setBackgroundColor(context?.getColor(R.color.separator_color) ?: 0) // Добавьте цвет в colors.xml
                    binding.linearLayoutCells.addView(separator)
                }
            }
        }

        // Вспомогательная функция для создания TextView ячейки
        private fun createCellTextView(config: ColumnConfiguration, appointment: Appointment): TextView {
            val textView = TextView(context)
            // Задаем параметры макета (ширина, высота, вес)
            val params = LinearLayout.LayoutParams(
                100.dpToPx(), // Фиксированная ширина для примера, лучше сделать адаптивной
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // TODO: Можно задавать разную ширину или вес в зависимости от типа столбца
            textView.layoutParams = params
            textView.setPadding(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx()) // Отступы внутри ячейки
            textView.textSize = 14f // Размер текста
            //textView.maxLines = 1 // Ограничение в одну строку
            //textView.ellipsize = TextUtils.TruncateAt.END // Многоточие если не влезает

            // Устанавливаем текст в зависимости от столбца
            textView.text = getCellText(config, appointment)

            return textView
        }

        // Вспомогательная функция для получения текста ячейки
        private fun getCellText(config: ColumnConfiguration, appointment: Appointment): String {
            return when (config.columnId) {
                ColumnConfiguration.COL_ID_DATE -> dateFormat.format(Date(appointment.date * 86400000L)) // Пример, если date - дни с эпохи
                ColumnConfiguration.COL_ID_CLIENT -> "Client ${appointment.clientId ?: '?'}" // TODO: Загрузить имя клиента
                ColumnConfiguration.COL_ID_TIME -> timeFormat.format(Date(appointment.startTimeMillis))
                ColumnConfiguration.COL_ID_DURATION -> "${appointment.durationMinutes} мин"
                ColumnConfiguration.COL_ID_SERVICE -> "Svc ${appointment.serviceId ?: '?'}" // TODO: Загрузить название услуги
                ColumnConfiguration.COL_ID_NOTES -> appointment.notes ?: ""
                ColumnConfiguration.COL_ID_CONTACTS -> "..." // TODO: Показать иконки / данные контактов
                ColumnConfiguration.COL_ID_COST -> formatCost(appointment.cost, appointment.currencyCode)
                ColumnConfiguration.COL_ID_STATUS -> appointment.status
                ColumnConfiguration.COL_ID_OPTIONAL_1 -> appointment.optionalField1Value ?: ""
                ColumnConfiguration.COL_ID_OPTIONAL_2 -> appointment.optionalField2Value ?: ""
                else -> "?"
            }
        }

        // Форматирование стоимости
        private fun formatCost(cost: Long?, currency: String?): String {
            return if (cost != null) {
                // Пример: делим копейки на 100 для отображения
                String.format(Locale.getDefault(), "%.2f %s", cost / 100.0, currency ?: "")
            } else {
                ""
            }
        }

        // Вспомогательная функция для конвертации dp в пиксели
        private fun Int.dpToPx(): Int {
            val scale = context?.resources?.displayMetrics?.density ?: 1f
            return (this * scale + 0.5f).toInt()
        }
    }

    companion object {
        private val APPOINTMENT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Appointment>() {
            override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
                oldItem == newItem
        }
    }
}

// Добавьте цвет в `feature_schedule/src/main/res/values/colors.xml`
// <color name="separator_color">#CCCCCC</color> // Например, светло-серый