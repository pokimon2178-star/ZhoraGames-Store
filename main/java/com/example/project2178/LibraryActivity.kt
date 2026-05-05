package com.example.project2178

import android.content.Context
import android.os.Bundle
import android.widget.TextView // Импорт для работы с текстовыми полями
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager // Управляет расположением элементов в списке
import androidx.recyclerview.widget.RecyclerView // Компонент для отображения больших списков

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library) // Подключаем XML-файл экрана библиотеки

        // --- ЛОГИКА ВОЗВРАТА НА ГЛАВНУЮ ---
        // Находим заголовок в макете по его ID
        val title: TextView = findViewById(R.id.library_title)

        // Устанавливаем слушатель нажатия на заголовок
        title.setOnClickListener {
            // Метод finish() закрывает текущую Activity и возвращает на предыдущий экран
            finish()
        }

        // --- ЗАГРУЗКА СПИСКА КУПЛЕННЫХ ИГР ---
        // Достаем логин текущего пользователя из настроек SharedPreferences
        val login = getSharedPreferences("UserSession", Context.MODE_PRIVATE).getString("username", "") ?: ""

        // Инициализируем помощника базы данных
        val db = DBHelper(this, null)

        // Получаем из БД список игр, которые принадлежат этому пользователю
        val items = db.getLibraryGames(login)

        // Находим компонент списка (RecyclerView) в макете
        val rv: RecyclerView = findViewById(R.id.library_rv)

        // Указываем, что элементы списка должны идти друг за другом вертикально
        rv.layoutManager = LinearLayoutManager(this)

        // Подключаем адаптер, передавая список игр и пометку "library" для особого отображения
        rv.adapter = ItemsAdapter(items, this, "library")

    }
}