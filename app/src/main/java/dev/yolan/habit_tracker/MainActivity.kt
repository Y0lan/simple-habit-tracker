package dev.yolan.habit_tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_card.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = HabitsAdapter(getSampleHabits())
    }
}