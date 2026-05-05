package com.example.project2178

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Устанавливаем макет экрана регистрации

        // Привязываем переменные к элементам интерфейса из XML по их ID
        val userLogin: EditText = findViewById(R.id.user_login)       // Поле ввода логина
        val userEmail: EditText = findViewById(R.id.user_email)       // Поле ввода почты
        val userPassword: EditText = findViewById(R.id.user_password) // Поле ввода пароля
        val btnReg: Button = findViewById(R.id.button)               // Кнопка "Зарегистрироваться"
        val linkToAuth: TextView = findViewById(R.id.link_to_reg)     // Текстовая ссылка на вход

        // Инициализируем базу данных для работы с пользователями
        val db = DBHelper(this, null)

        // Обработка нажатия на кнопку регистрации
        btnReg.setOnClickListener {
            // Считываем текст из полей, убирая лишние пробелы (trim)
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPassword.text.toString().trim()

            // Проверка: не остались ли поля пустыми
            if(login.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
            // Проверка в БД: не занят ли такой логин другим пользователем
            else if (db.checkUserExists(login)) {
                Toast.makeText(this, "Логин занят", Toast.LENGTH_SHORT).show()
            }
            // Если всё ок — регистрируем
            else {
                // Создаем объект пользователя и сохраняем его в базу данных
                val user = User(login, email, pass)
                db.addUser(user)

                // Выводим сообщение об успехе
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                // Переходим на экран авторизации (входа)
                startActivity(Intent(this, AuthActivity::class.java))
            }
        }

        // Обработка клика по ссылке "Уже есть аккаунт?" (возврат на AuthActivity)
        linkToAuth.setOnClickListener {
            // Создаем намерение открыть экран входа
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}