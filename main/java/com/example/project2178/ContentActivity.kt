package com.example.project2178

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Экран с детальным описанием конкретной игры
class ContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем XML-разметку для этого экрана
        setContentView(R.layout.activity_content)

        // 1. Находим все элементы дизайна на экране (текст, картинку, кнопку)
        val title: TextView = findViewById(R.id.content_title)
        val fullText: TextView = findViewById(R.id.content_full_text)
        val image: ImageView = findViewById(R.id.content_image)
        val btnAddToCart: Button = findViewById(R.id.btn_buy_content)

        // 2. Достаем данные, которые нам "переслал" ItemsAdapter через Intent
        val gameId = intent.getIntExtra("itemId", -1)
        val gameTitle = intent.getStringExtra("itemTitle") ?: "Игра"
        val gameDescription = intent.getStringExtra("itemText")
        val imageName = intent.getStringExtra("itemImg")

        // 3. Заполняем экран полученными данными
        title.text = gameTitle
        fullText.text = gameDescription

        // Превращаем текстовое имя картинки в ресурс, который поймет ImageView
        val imageId = resources.getIdentifier(imageName, "drawable", packageName)
        if (imageId != 0) {
            image.setImageResource(imageId)
        }

        // 4. Логика кнопки "Добавить в корзину"
        btnAddToCart.setOnClickListener {
            // Достаем имя текущего пользователя из памяти устройства
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val login = sharedPref.getString("username", "") ?: ""

            // Проверяем, залогинен ли пользователь и корректен ли ID игры
            if (login.isNotEmpty() && gameId != -1) {
                // Создаем объект базы данных и вызываем метод добавления в корзину
                val db = DBHelper(this, null)
                db.addToCart(login, gameId)

                // Показываем сообщение об успешном добавлении
                Toast.makeText(this, "$gameTitle добавлена в корзину!", Toast.LENGTH_SHORT).show()

                // Закрываем текущий экран, чтобы вернуться обратно в магазин
                finish()
            } else {
                // Если пользователь не вошел — просим авторизоваться
                Toast.makeText(this, "Сначала войдите в аккаунт!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}