package shahzod.projects.ozbektiliningizohliugati.myApp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import shahzod.projects.ozbektiliningizohliugati.database.AppDatabase
import shahzod.projects.ozbektiliningizohliugati.notification.WordOfTheDayWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        AppDatabase.getInstance(this)
        scheduleWordOfTheDay()
    }

    private fun scheduleWordOfTheDay() {
        val delay = calculateInitialDelay(hour = 9, minute = 0)

        val request = PeriodicWorkRequestBuilder<WordOfTheDayWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "word_of_the_day",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_MONTH, 1)
        return target.timeInMillis - now.timeInMillis
    }
}