package com.example.project2178

import android.content.Intent // Импорт для переходов между экранами
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager // Для управления списком (вертикальный/горизонтальный)
import androidx.recyclerview.widget.RecyclerView // Сам компонент списка

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_list) // Устанавливаем XML-макет со списком товаров

        // Находим текстовое поле заголовка (плашка магазина)
        val mainTitle: TextView = findViewById(R.id.textView2)
        mainTitle.text = "ЖОРА ГЕЙМС" // Устанавливаем название магазина
        mainTitle.setTextColor(android.graphics.Color.parseColor("#FFD700")) // Красим текст в золотой цвет

        // Кнопка перехода в "Библиотеку"
        findViewById<Button>(R.id.btn_go_library).setOnClickListener {
            // Создаем намерение открыть экран LibraryActivity и запускаем его
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        // Кнопка перехода в "Корзину" (первый вариант инициализации)
        findViewById<Button>(R.id.btn_go_cart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Находим кнопки избранного и корзины для дальнейшей работы
        val btnFav: Button = findViewById(R.id.btn_go_favorites)
        val btnCart: Button = findViewById(R.id.btn_go_cart)

        // Слушатель для перехода в "Избранное"
        btnFav.setOnClickListener {
            // Создаем намерение открыть экран FavoritesActivity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent) // Выполняем переход
        }

        // Повторная инициализация кнопки избранного (как в твоем исходнике)
        val btnGoFav: Button = findViewById(R.id.btn_go_favorites)

        btnGoFav.setOnClickListener {
            // Создаем намерение открыть экран FavoritesActivity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        // Слушатель для перехода в "Корзину"
        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Инициализируем базу данных
        val db = DBHelper(this, null)

        // Находим RecyclerView (список) в макете
        val list = findViewById<RecyclerView>(R.id.itemList)

        // Указываем, что список будет линейным (элементы друг под другом)
        list.layoutManager = LinearLayoutManager(this)

        // Подключаем адаптер: берем данные из БД (рекомендованные игры) и передаем в список
        list.adapter = ItemsAdapter(db.getRecommendedGames(), this)
    }
}