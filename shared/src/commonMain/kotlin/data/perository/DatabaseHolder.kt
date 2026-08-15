package data.perository

import data.room.myDataBase
import kotlin.concurrent.Volatile


class DatabaseHolder(
    private val databaseBuilder: () -> myDataBase // Передаем лямбду создания вашей БД
) {
    @Volatile
    private var _db: myDataBase? = databaseBuilder()

    // Репозитории будут вызывать холдер.db, чтобы всегда получать рабочую ссылку
    val db: myDataBase
        get() = _db ?: error("База данных временно недоступна (идет восстановление)")

    // Закрывает старую базу, освобождая файлы на диске
    fun close() {
        _db?.close()
        _db = null
    }

    // Открывает базу заново над новыми файлами
    fun recreate() {
        _db?.close()
        _db = databaseBuilder()
    }
}