package dev.yolan.habit_tracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.yolan.habit_tracker.Habit
import dev.yolan.habit_tracker.database.HabitEntry.DESCR_COL
import dev.yolan.habit_tracker.database.HabitEntry.IMAGE_COL
import dev.yolan.habit_tracker.database.HabitEntry.TABLE_NAME
import dev.yolan.habit_tracker.database.HabitEntry.TITLE_COL
import dev.yolan.habit_tracker.database.HabitEntry._ID
import java.io.ByteArrayOutputStream

class HabitDatabaseTable(context: Context) {
    private val databaseHelper = HabitTrainerDatabase(context)

    fun store(habit: Habit): Long {
        val database = databaseHelper.writableDatabase
        val values = ContentValues()
        with(values) {
            put(TITLE_COL, habit.title)
            put(DESCR_COL, habit.description)
            put(IMAGE_COL, toByteArray(habit.image))
        }

        return database.transaction {
            insert(TABLE_NAME, null, values)
        }
    }

    fun readAllHabits(): List<Habit> {
        val columns = arrayOf(
            _ID,
            TITLE_COL,
            DESCR_COL,
            IMAGE_COL
        )
        val order = "$_ID ASC"
        val database = databaseHelper.readableDatabase
        val cursor = database.doQuery(
            TABLE_NAME,
            columns,
            orderBy = order
        )
        return parseHabitsFrom(cursor)
    }

    private fun parseHabitsFrom(cursor: Cursor): MutableList<Habit> {
        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(TITLE_COL)
            val description = cursor.getString(DESCR_COL)
            val bitmap = cursor.getBitmap(IMAGE_COL)
            habits.add(Habit(title, description, bitmap))
        }
        cursor.close()
        return habits
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()

    }
}

private fun Cursor.getBitmap(columnName: String): Bitmap {
    val bytes = getBlob(getColumnIndex(columnName))
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))

private fun SQLiteDatabase.doQuery(
    table: String,
    columns: Array<String>,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    groupBy: String? = null,
    having: String? = null,
    orderBy: String? = null
): Cursor {
    return query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
}

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    } finally {
        endTransaction()
    }
    close()
    return result
}