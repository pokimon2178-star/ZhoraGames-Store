package com.example.project2178

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Экран со списком избранных игр пользователя
class FavoritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем разметку экрана избранного из XML
        setContentView(R.layout.activity_favorites)

        // Логика возврата: при нажатии на заголовок "Избранное" просто закрываем этот экран
        findViewById<TextView>(R.id.fav_title).setOnClickListener {
            // Завершаем работу Activity и возвращаемся в предыдущее окно
            finish()
        }

        // Достаем логин авторизованного пользователя из памяти (SharedPreferences)
        val login = getSharedPreferences("UserSession", Context.MODE_PRIVATE).getString("username", "") ?: ""

        // Создаем объект для работы с базой данных
        val db = DBHelper(this, null)

        // Получаем из БД список игр, которые текущий пользователь пометил как любимые
        // Превращаем результат в MutableList, чтобы список можно было динамически обновлять
        val items = db.getFavoriteGames(login).toMutableList()

        // Находим в разметке наш список (RecyclerView)
        val rv: RecyclerView = findViewById(R.id.fav_rv)

        // Указываем списку, что элементы нужно располагать линейно (друг под другом)
        rv.layoutManager = LinearLayoutManager(this)

        // Подключаем адаптер для отображения карточек игр
        // ВАЖНО: передаем тип "fav", чтобы в адаптере включилась кнопка удаления
        rv.adapter = ItemsAdapter(items, this, "fav")
    }
}