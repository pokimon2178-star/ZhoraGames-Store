package com.example.project2178

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Экран авторизации пользователя
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем внешний вид экрана из XML-файла
        setContentView(R.layout.auth_activity)

        // Находим поля ввода логина и пароля
        val userLogin: EditText = findViewById(R.id.user_login_auth)
        val userPass: EditText = findViewById(R.id.user_pass_auth)

        // Находим кнопку входа и текстовую ссылку на регистрацию
        val btnAuth: Button = findViewById(R.id.button_auth)
        val linkToReg: TextView = findViewById(R.id.link_to_reg)

        // Инициализируем помощника для работы с базой данных
        val db = DBHelper(this, null)

        // Обработка нажатия на кнопку "Войти"
        btnAuth.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPass.text.toString().trim()

            // Проверяем через базу данных, существует ли пользователь с таким паролем
            if (db.getUser(login, pass)) {
                // Если данные верны, сохраняем логин в память телефона (SharedPreferences)
                // Чтобы приложение "помнило" нас на других экранах
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                sharedPref.edit().putString("username", login).apply()

                // Создаем намерение открыть главный экран с играми (ItemsActivity)
                val intent = Intent(this, ItemsActivity::class.java)
                startActivity(intent)

                // Закрываем экран логина, чтобы нельзя было вернуться назад кнопкой
                finish()
            } else {
                // Если данные не подошли — показываем ошибку
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработка клика по ссылке "Зарегистрироваться"
        linkToReg.setOnClickListener {
            // Создаем намерение открыть экран регистрации (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}