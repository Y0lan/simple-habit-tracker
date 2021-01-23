package dev.yolan.habit_tracker

data class Habit(val title: String, val description: String, val image: Int) {
}

fun getSampleHabits(): List<Habit> {
    return listOf(
        Habit(
            "Go for a walk",
            "walking is good for you",
            R.drawable.walk
        ),
        Habit(
            "Drink water",
            "water is life",
            R.drawable.water
        )
    )
}
