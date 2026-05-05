package com.example.project2178

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Класс для управления базой данных (создание, обновление, запросы)
class DBHelper(val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "zhora_games_db", factory, 27) { // Используем версию 27 для сброса данных

    // Метод, который вызывается при первом создании базы данных
    override fun onCreate(db: SQLiteDatabase?) {
        // Создаем таблицу пользователей (id, логин, почта, пароль)
        db!!.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, email TEXT, password TEXT)")
        // Создаем таблицу игр с описанием и ценой
        db.execSQL("CREATE TABLE games (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, genre TEXT, price INTEGER, image TEXT, description TEXT, full_text TEXT)")
        // Создаем таблицу корзины (связь пользователя и игры)
        db.execSQL("CREATE TABLE cart (id INTEGER PRIMARY KEY AUTOINCREMENT, user_login TEXT, game_id INTEGER)")
        // Создаем таблицу избранного
        db.execSQL("CREATE TABLE favorites (id INTEGER PRIMARY KEY AUTOINCREMENT, user_login TEXT, game_id INTEGER)")
        // Создаем таблицу купленных игр (библиотека)
        db.execSQL("CREATE TABLE library (id INTEGER PRIMARY KEY AUTOINCREMENT, user_login TEXT, game_id INTEGER)")

        // Заполняем базу начальным списком игр
        insertDefaultGames(db)
    }

    // Вспомогательный метод для добавления стартового контента
    private fun insertDefaultGames(db: SQLiteDatabase) {
        val games = listOf(
            arrayOf("GTA 6", "Action", "7999", "gta", "Легендарный Вайс-Сити.", "Самая ожидаемая игра. Огромный открытый мир и графика нового поколения."),
            arrayOf("Hitman", "Stealth", "2500", "hitman", "Станьте Агентом 47.", "Ликвидируйте цели по всему миру, используя любые подручные средства."),
            arrayOf("Elden Ring", "RPG", "3999", "elden_ring", "Хардкорное приключение.", "Исследуйте Междуземье и победите великих боссов в шедевре от FromSoftware."),
            arrayOf("Stray", "Adventure", "800", "stray", "Мир глазами кота.", "Помогите рыжему коту разгадать тайны кибергорода и вернуться домой.")
        )

        for (g in games) {
            val cv = ContentValues()
            cv.put("title", g[0])
            cv.put("genre", g[1])
            cv.put("price", g[2].toInt())
            cv.put("image", g[3])
            cv.put("description", g[4])
            cv.put("full_text", g[5])
            // Вставляем данные в таблицу games
            db.insert("games", null, cv)
        }
    }

    // Вызывается при обновлении версии базы данных
    override fun onUpgrade(db: SQLiteDatabase?, oldV: Int, newV: Int) {
        // Удаляем старые таблицы, если они существуют
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS games")
        db?.execSQL("DROP TABLE IF EXISTS cart")
        db?.execSQL("DROP TABLE IF EXISTS favorites")
        db?.execSQL("DROP TABLE IF EXISTS library")
        // Создаем всё заново
        onCreate(db)
    }

    // --- ПОЛЬЗОВАТЕЛИ ---

    // Регистрируем нового пользователя в базе
    fun addUser(user: User) {
        val cv = ContentValues()
        cv.put("login", user.login)
        cv.put("email", user.email)
        cv.put("password", user.password)
        this.writableDatabase.insert("users", null, cv)
    }

    // Проверяем, верны ли логин и пароль для входа
    fun getUser(login: String, pass: String): Boolean {
        val cursor = this.readableDatabase.rawQuery("SELECT * FROM users WHERE login = ? AND password = ?", arrayOf(login, pass))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // Проверяем, занят ли логин при регистрации
    fun checkUserExists(login: String): Boolean {
        val cursor = this.readableDatabase.rawQuery("SELECT * FROM users WHERE login = ?", arrayOf(login))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // --- ОПЕРАЦИИ (ДОБАВЛЕНИЕ / УДАЛЕНИЕ / ПОКУПКА) ---

    // Добавляем игру в корзину пользователя
    fun addToCart(login: String, gameId: Int) {
        if (!isItemInTable("cart", login, gameId)) {
            val cv = ContentValues()
            cv.put("user_login", login)
            cv.put("game_id", gameId)
            this.writableDatabase.insert("cart", null, cv)
        }
    }

    // Добавляем игру в список избранного
    fun addToFavorites(login: String, gameId: Int) {
        if (!isItemInTable("favorites", login, gameId)) {
            val cv = ContentValues()
            cv.put("user_login", login)
            cv.put("game_id", gameId)
            this.writableDatabase.insert("favorites", null, cv)
        }
    }

    // Удаляем игру из корзины конкретного пользователя
    fun removeFromCart(login: String, gameId: Int) {
        this.writableDatabase.delete("cart", "user_login = ? AND game_id = ?", arrayOf(login, gameId.toString()))
    }

    // Удаляем игру из избранного
    fun removeFromFavorites(login: String, gameId: Int) {
        val db = this.writableDatabase
        db.delete("favorites", "user_login = ? AND game_id = ?", arrayOf(login, gameId.toString()))
        db.close() // Закрываем базу для экономии ресурсов
    }

    // Переносим все игры из корзины в библиотеку (покупка)
    fun buyAllFromCart(login: String) {
        val db = this.writableDatabase
        // SQL-запрос: копируем из корзины в библиотеку только те игры, которых там еще нет
        val query = """
        INSERT INTO library (user_login, game_id) 
        SELECT user_login, game_id FROM cart 
        WHERE user_login = ? AND game_id NOT IN (
            SELECT game_id FROM library WHERE user_login = ?
        )
    """.trimIndent()

        db.execSQL(query, arrayOf(login, login))

        // Очищаем корзину после успешной "покупки"
        db.delete("cart", "user_login = ?", arrayOf(login))
    }

    // --- ВЫБОРКИ (ДЛЯ ЭКРАНОВ ПРИЛОЖЕНИЯ) ---

    // Получаем список всех доступных игр для главного экрана
    fun getRecommendedGames() = getGamesFromQuery("SELECT * FROM games", null)

    // Получаем игры, которые пользователь добавил в корзину
    fun getCartGames(login: String) = getGamesFromQuery("SELECT games.* FROM games INNER JOIN cart ON games.id = cart.game_id WHERE cart.user_login = ?", arrayOf(login))

    // Получаем список избранных игр пользователя
    fun getFavoriteGames(login: String) = getGamesFromQuery("SELECT games.* FROM games INNER JOIN favorites ON games.id = favorites.game_id WHERE favorites.user_login = ?", arrayOf(login))

    // Получаем список уже купленных игр (библиотека)
    fun getLibraryGames(login: String) = getGamesFromQuery("SELECT games.* FROM games INNER JOIN library ON games.id = library.game_id WHERE library.user_login = ?", arrayOf(login))

    // Универсальный метод для преобразования SQL-запроса в список объектов Item
    private fun getGamesFromQuery(query: String, args: Array<String>?): List<Item> {
        val list = mutableListOf<Item>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, args)
        while (cursor.moveToNext()) {
            list.add(Item(
                cursor.getInt(0),    // Берем ID
                cursor.getString(4), // Берем название картинки
                cursor.getString(1), // Берем название игры
                cursor.getString(5), // Краткое описание
                cursor.getString(6), // Полный текст
                cursor.getInt(3),    // Цена
                cursor.getString(2)  // Жанр
            ))
        }
        cursor.close()
        return list
    }

    // Проверяем, существует ли уже такая запись в таблице (чтобы не дублировать игры)
    private fun isItemInTable(table: String, login: String, gameId: Int): Boolean {
        val cursor = this.readableDatabase.rawQuery("SELECT * FROM $table WHERE user_login = ? AND game_id = ?", arrayOf(login, gameId.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }
}