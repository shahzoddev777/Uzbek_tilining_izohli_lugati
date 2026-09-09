package shahzod.projects.ozbektiliningizohliugati

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import shahzod.projects.ozbektiliningizohliugati.databinding.ActivityMainBinding
import shahzod.projects.ozbektiliningizohliugati.presentation.AtamaFragment
import shahzod.projects.ozbektiliningizohliugati.presentation.HomeFragment
import shahzod.projects.ozbektiliningizohliugati.presentation.SettingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val view = binding.bottomNavigationView.findViewById<View>(item.itemId)
            view?.let {
                val anim = AnimationUtils.loadAnimation(this, R.anim.scale_up)
                it.startAnimation(anim)
            }

            if (navController.currentDestination?.id != item.itemId) {
                navController.navigate(item.itemId)
                true
            } else {
                false
            }
        }
    }
}