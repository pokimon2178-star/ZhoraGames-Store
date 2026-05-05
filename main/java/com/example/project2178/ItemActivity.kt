package com.example.project2178

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item) // Устанавливаем макет экрана из файла item.xml

        // Находим все элементы управления на экране по их ID
        val image: ImageView = findViewById(R.id.imageView3) // Картинка товара
        val title: TextView = findViewById(R.id.item_title)   // Заголовок
        val text: TextView = findViewById(R.id.item_text)     // Описание
        val price: TextView = findViewById(R.id.item_price)   // Цена
        val btnBuy: Button = findViewById(R.id.button2)       // Кнопка "Купить"
        val btnFav: Button = findViewById(R.id.button_fav)    // Кнопка "В избранное"

        // Достаем данные о товаре, которые передали через Intent с другого экрана
        val id = intent.getIntExtra("itemId", -1) // ID товара (нужен для БД)
        title.text = intent.getStringExtra("itemTitle") // Ставим название
        text.text = intent.getStringExtra("itemText")   // Ставим текст описания
        price.text = intent.getStringExtra("itemPrice") + " ₽" // Ставим цену с символом рубля

        // Установка изображения товара: если передали 0, ставим картинку по умолчанию
        val imgId = intent.getIntExtra("itemImage", R.drawable.normal)
        image.setImageResource(if (imgId != 0) imgId else R.drawable.normal)

        // Инициализируем помощника базы данных
        val db = DBHelper(this, null)

        // Получаем доступ к "сессии" (хранилищу), чтобы узнать имя текущего пользователя
        val sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sp.getString("username", "guest") ?: "guest"

        // Слушатель нажатия на кнопку покупки
        btnBuy.setOnClickListener {
            db.addToCart(username, id) // Добавляем запись в таблицу корзины в БД
            Toast.makeText(this, "В корзине!", Toast.LENGTH_SHORT).show() // Всплывающее уведомление
            finish() // Закрываем экран и возвращаемся назад
        }

        // Слушатель нажатия на кнопку "Лайк" (избранное)
        btnFav.setOnClickListener {
            db.addToFavorites(username, id) // Добавляем запись в таблицу избранного в БД
            Toast.makeText(this, "Лайк! ❤️", Toast.LENGTH_SHORT).show() // Всплывающее уведомление

            // Если захочешь сделать переход на экран избранного, код пишется сюда:
            // val intent = Intent(this, FavoritesActivity::class.java)
            // startActivity(intent)

            finish() // Закрываем экран и возвращаемся назад
        }
    }
}