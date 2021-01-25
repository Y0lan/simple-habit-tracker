package dev.yolan.habit_tracker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import dev.yolan.habit_tracker.database.HabitDatabaseTable
import kotlinx.android.synthetic.main.activity_create_habit.*
import kotlinx.android.synthetic.main.single_card.*
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val CHOOSE_IMAGE_REQUEST = 1
    private var imageBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
    }

    private fun EditText.isBlank() = this.text.toString().isBlank()

    fun storeHabit(view: View) {
        if (et_title.isBlank() ||
            et_description.isBlank()
        ) {
            displayErrorMessage("You need to add a motivating title and description for your habit!")
            return
        }
        if (imageBitmap == null) {
            displayErrorMessage("You need to add an awesome image for your habit!")
            return
        }
        tv_error.visibility = View.INVISIBLE
        val title = et_title.text.toString()
        val description = et_description.text.toString()
        val habit = Habit(title, description, imageBitmap!!)
        val id = HabitDatabaseTable(this).store(habit)
        if (id == -1L) displayErrorMessage("Habit could not be stored... Database Error")
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayErrorMessage(message: String) {
        tv_error.text = message
        tv_error.visibility = View.VISIBLE
    }

    fun chooseImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val chooser = Intent.createChooser(intent, "Choose image for habit")
        startActivityForResult(chooser, CHOOSE_IMAGE_REQUEST)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            val bitmap = tryReadBitmap(data.data)
            bitmap?.let {
                imageBitmap = bitmap
                iv_image_preview.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, false))
            }
        }
    }

    private fun tryReadBitmap(data: Uri?): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, data)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}
