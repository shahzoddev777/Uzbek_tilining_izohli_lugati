package shahzod.projects.ozbektiliningizohliugati

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import shahzod.projects.ozbektiliningizohliugati.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentDestinationId: Int = R.id.house

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBarContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val params = v.layoutParams as android.view.ViewGroup.MarginLayoutParams
            val marginInPx = (16 * resources.displayMetrics.density).toInt()
            params.bottomMargin = systemBars.bottom + marginInPx
            v.layoutParams = params
            insets
        }

        // BottomNavigationView tizim panel balandligini o'ziga padding qilib olmasin
        // (3 tugmali kichik telefonlarda ikonkalar tepaga chiqib ketishining sababi shu edi)
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { v, _ ->
            v.setPadding(0, 0, 0, 0)
            WindowInsetsCompat.CONSUMED
        }

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        if (savedInstanceState == null) {
            navController.navigate(R.id.splashFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
            adjustSystemBarsForDestination(destination.id)

            when (destination.id) {
                R.id.splashFragment,
                R.id.dictionaryInfoFragment,
                R.id.starFragment -> {
                    binding.bottomBarContainer.visibility = View.GONE
                }

                else -> {
                    binding.bottomBarContainer.visibility = View.VISIBLE
                    updateIndicatorPosition(destination.id)
                }
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val view = binding.bottomNavigationView.findViewById<View>(item.itemId)
            view?.let {
                val anim = AnimationUtils.loadAnimation(this, R.anim.scale_up)
                it.startAnimation(anim)
            }

            item.onNavDestinationSelected(navController)
        }

        updateBottomNavigationTheme()
    }

    override fun onResume() {
        super.onResume()
        updateBottomNavigationTheme()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateBottomNavigationTheme()
    }

    private fun updateBottomNavigationTheme() {
        applySystemBarAppearance()

        binding.bottomBarContainer.background = ContextCompat.getDrawable(this, R.drawable.bg_glass_border)?.mutate()
        binding.animatedIndicator.background = ContextCompat.getDrawable(this, R.drawable.bg_active_indicator)?.mutate()

        val colorStateList = ContextCompat.getColorStateList(this, R.color.nav_item_color)
        binding.bottomNavigationView.itemIconTintList = colorStateList

        updateIndicatorPosition(currentDestinationId, animate = false)
    }

    private fun updateIndicatorPosition(destinationId: Int, animate: Boolean = true) {
        binding.bottomNavigationView.post {
            val itemView = binding.bottomNavigationView.findViewById<View>(destinationId)
            val indicator = binding.animatedIndicator
            val indicatorWidth = indicator.width.toFloat()

            if (indicatorWidth <= 0f) return@post

            val targetX = if (itemView != null && itemView.width > 0) {
                val itemCenterX = itemView.x + itemView.width / 2f
                itemCenterX - indicatorWidth / 2f
            } else {
                val menu = binding.bottomNavigationView.menu
                var selectedIndex = -1

                for (i in 0 until menu.size()) {
                    if (menu.getItem(i).itemId == destinationId) {
                        selectedIndex = i
                        break
                    }
                }

                if (selectedIndex != -1 && menu.size() > 0) {
                    val totalTabs = menu.size()
                    val tabWidth = binding.bottomNavigationView.width / totalTabs.toFloat()
                    selectedIndex * tabWidth + (tabWidth - indicatorWidth) / 2f
                } else {
                    0f
                }
            }

            if (animate) {
                indicator.animate()
                    .translationX(targetX)
                    .setDuration(280)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .start()
            } else {
                indicator.animate().cancel()
                indicator.translationX = targetX
            }
        }
    }

    private fun applySystemBarAppearance() {
        val isNightMode = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val controller = WindowCompat.getInsetsController(window, window.decorView)
        window.statusBarColor = ContextCompat.getColor(this, R.color.header_blue)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = !isNightMode
    }

    private fun adjustSystemBarsForDestination(destinationId: Int) {
        applySystemBarAppearance()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment

        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}