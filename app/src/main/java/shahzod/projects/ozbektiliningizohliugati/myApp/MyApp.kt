package shahzod.projects.ozbektiliningizohliugati.myApp

import android.app.Application
import android.util.Log
import shahzod.projects.ozbektiliningizohliugati.database.AppDatabase

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(this)
    }
}