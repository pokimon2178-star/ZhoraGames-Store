package com.example.project2178

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Экран корзины, где лежат выбранные, но еще не купленные игры
class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем разметку экрана корзины
        setContentView(R.layout.activity_cart)

        // Достаем логин текущего пользователя, чтобы знать, чью корзину показывать
        val login = getSharedPreferences("UserSession", Context.MODE_PRIVATE).getString("username", "") ?: ""
        val db = DBHelper(this, null)

        // 1. Логика возврата: при нажатии на заголовок "Корзина" закрываем этот экран
        val title: TextView = findViewById(R.id.cart_title)
        title.setOnClickListener {
            // Просто закрываем текущую Activity и автоматически попадаем на предыдущую (в магазин)
            finish()
        }

        // 2. Настройка списка товаров
        // Вытягиваем из базы игры, которые юзер добавил в корзину
        val items = db.getCartGames(login).toMutableList()
        val rv: RecyclerView = findViewById(R.id.cart_rv)

        // Говорим списку отображаться вертикально (один за другим)
        rv.layoutManager = LinearLayoutManager(this)

        // Подключаем адаптер. Передаем тип "cart", чтобы адаптер включил кнопки удаления
        val adapter = ItemsAdapter(items, this, "cart")
        rv.adapter = adapter

        // 3. Логика кнопки "Купить всё"
        findViewById<Button>(R.id.btn_buy_all).setOnClickListener {
            // Если в корзине что-то есть — проводим покупку
            if (items.isNotEmpty()) {
                // В базе переносим игры из таблицы cart в таблицу library
                db.buyAllFromCart(login)
                Toast.makeText(this, "Покупка успешна! Игры добавлены в библиотеку", Toast.LENGTH_SHORT).show()

                // После покупки закрываем экран корзины
                finish()
            } else {
                // Если пусто — ругаемся
                Toast.makeText(this, "Корзина пуста, сначала выберите игры", Toast.LENGTH_SHORT).show()
            }
        }
    }
}