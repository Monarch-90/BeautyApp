package com.tatiana.beauty.presentation


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment // Используем этот импорт
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tatiana.beauty.databinding.ActivityMainBinding
import com.tatiana.beauty.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Обязательно для Activity, использующей Hilt
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null // ViewBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Находим NavHostFragment и его NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
        navController = navHostFragment?.navController

        // Находим BottomNavigationView
        val navView: BottomNavigationView? = binding?.navView

        // Связываем NavController с BottomNavigationView
        // Это автоматически будет обрабатывать переключение фрагментов
        // при нажатии на элементы нижнего меню.
        navController?.let { nc ->
            navView?.setupWithNavController(nc)
        }

        // --- Важное условие из вашего ТЗ! ---
        // Всегда начинаем с первого экрана (Расписание) при запуске,
        // даже если пользователь закрыл приложение на другой вкладке.
        // Мы это настроили в nav_graph через app:startDestination,
        // поэтому дополнительный код здесь не нужен для этой логики.
        // Если бы startDestination не был указан, пришлось бы делать так:
        // if (savedInstanceState == null) { // Проверяем, первый ли это запуск Activity
        //     navView.selectedItemId = R.id.schedule_navigation // Выбираем нужный пункт меню
        // }
    }

    // Если понадобится поддержка кнопки "Назад" для навигации
    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() ?: super.onSupportNavigateUp()
    }
}