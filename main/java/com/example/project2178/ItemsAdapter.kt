package com.example.project2178

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

// Адаптер, который превращает список объектов в визуальные карточки на экране
class ItemsAdapter(var items: List<Item>, var context: Context, var type: String = "shop") : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    // Класс-держатель (ViewHolder) — находит и хранит ссылки на элементы дизайна (кнопки, картинки)
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView3)
        val title: TextView = view.findViewById(R.id.item_title)
        val price: TextView = view.findViewById(R.id.item_price)
        val btnMore: Button = view.findViewById(R.id.button2)
        val btnFav: ImageButton = view.findViewById(R.id.btn_favorite)
        val btnDel: Button = view.findViewById(R.id.btn_delete)
    }

    // Создаем внешний вид одной карточки, используя разметку item_in_list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false))

    // Наполняем карточку данными и вешаем обработчики кликов
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.price.text = "${item.price} ₽"

        // Динамически подтягиваем картинку по её названию из папки drawable
        val resId = context.resources.getIdentifier(item.image, "drawable", context.packageName)
        if (resId != 0) holder.image.setImageResource(resId)

        // Настраиваем видимость кнопок в зависимости от того, где мы (Магазин, Корзина или Избранное)
        holder.btnDel.visibility = if (type == "cart" || type == "fav") View.VISIBLE else View.GONE
        holder.btnFav.visibility = if (type == "library") View.GONE else View.VISIBLE

        // Логика кнопки "Подробнее"
        holder.btnMore.setOnClickListener {
            // Создаем намерение открыть экран с описанием игры (ContentActivity)
            val intent = Intent(context, ContentActivity::class.java)
            // Передаем данные игры на новый экран
            intent.putExtra("itemId", item.id)
            intent.putExtra("itemTitle", item.title)
            intent.putExtra("itemImg", item.image)
            intent.putExtra("itemText", item.full_text)
            // Запускаем переход
            context.startActivity(intent)
        }

        // Если мы в библиотеке (купленные игры), меняем поведение кнопки
        if (type == "library") {
            holder.btnMore.text = "ИГРАТЬ"
            holder.btnMore.setOnClickListener {
                // Вместо перехода просто имитируем запуск игры уведомлением
                Toast.makeText(context, "Запуск ${item.title}... Приятной игры!", Toast.LENGTH_LONG).show()
            }
            // Прячем лишние элементы для купленных игр
            holder.price.visibility = View.GONE
            holder.btnFav.visibility = View.GONE
        }

        // Кнопка добавления в избранное (звездочка)
        holder.btnFav.setOnClickListener {
            // Достаем имя текущего юзера из памяти телефона
            val login = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE).getString("username", "") ?: ""
            // Сохраняем связь юзера и игры в таблицу избранного
            DBHelper(context, null).addToFavorites(login, item.id)
            Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
        }

        // Кнопка удаления (корзина/избранное)
        holder.btnDel.setOnClickListener {
            val login = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE).getString("username", "") ?: ""
            val db = DBHelper(context, null)

            // Определяем, какую именно карточку нажал пользователь
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            if (type == "fav") {
                // Удаляем из базы избранного
                db.removeFromFavorites(login, item.id)
                // Удаляем из списка и плавно убираем карточку из списка на экране
                val mutableItems = items.toMutableList()
                mutableItems.removeAt(currentPosition)
                items = mutableItems
                notifyItemRemoved(currentPosition)
                Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
            } else if (type == "cart") {
                // Аналогично для корзины
                db.removeFromCart(login, item.id)
                val mutableItems = items.toMutableList()
                mutableItems.removeAt(currentPosition)
                items = mutableItems
                notifyItemRemoved(currentPosition)
                Toast.makeText(context, "Удалено из корзины", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Говорим списку, сколько всего элементов нужно отобразить
    override fun getItemCount() = items.count()
}