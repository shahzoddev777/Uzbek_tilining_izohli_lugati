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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import shahzod.projects.ozbektiliningizohliugati.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Permission natijasi
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        // MUHIM:
        // enableEdgeToEdge() OLIB TASHLANDI.
        // Chunki status bar ilova kontenti ustiga chiqib ketayotgan edi.

        // System barlarni normal holatda ishlatamiz
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Status bar rangi
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.header_blue
        )

        // Navigation bar rangi
        window.navigationBarColor = ContextCompat.getColor(
            this,
            R.color.surface
        )

        binding = ActivityMainBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        setContentView(binding.root)

        // Status bar ikonkalari
        applySystemBarAppearance()


        // ==============================
        // SYSTEM BAR INSETS
        // ==============================

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )

            insets
        }


        // ==============================
        // BOTTOM NAVIGATION
        // ==============================

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.bottomNavigationView
        ) { v, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                0,
                0,
                0,
                systemBars.bottom
            )

            insets
        }


        // ==============================
        // NAVIGATION
        // ==============================

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment
            ) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(
            navController
        )


        if (savedInstanceState == null) {
            navController.navigate(R.id.splashFragment)
        }


        // ==============================
        // DESTINATION O'ZGARGANDA
        // ==============================

        navController.addOnDestinationChangedListener { _, destination, _ ->

            adjustSystemBarsForDestination(destination.id)

            when (destination.id) {

                R.id.splashFragment,
                R.id.dictionaryInfoFragment,
                R.id.starFragment -> {

                    binding.bottomNavigationView.visibility =
                        View.GONE
                }

                else -> {

                    binding.bottomNavigationView.visibility =
                        View.VISIBLE
                }
            }
        }


        // ==============================
        // BOTTOM NAVIGATION ANIMATION
        // ==============================

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            val view =
                binding.bottomNavigationView.findViewById<View>(
                    item.itemId
                )

            view?.let {

                val anim = AnimationUtils.loadAnimation(
                    this,
                    R.anim.scale_up
                )

                it.startAnimation(anim)
            }

            item.onNavDestinationSelected(navController)
        }
    }


    // ==================================================
    // SYSTEM BAR APPEARANCE
    // ==================================================

    private fun applySystemBarAppearance() {

        val isNightMode =
            (resources.configuration.uiMode
                    and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

        val controller =
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )

        // Header ko'k bo'lgani uchun ikonalar OQ
        controller.isAppearanceLightStatusBars = false

        // Light mode -> qora navigation ikonkalari
        // Dark mode  -> oq navigation ikonkalari
        controller.isAppearanceLightNavigationBars =
            !isNightMode
    }


    // ==================================================
    // HAR BIR EKRAN UCHUN SYSTEM BAR
    // ==================================================

    private fun adjustSystemBarsForDestination(
        destinationId: Int
    ) {

        val isNightMode =
            (resources.configuration.uiMode
                    and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

        val controller =
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )


        // Status bar FONI HAR DOIM header_blue
        window.statusBarColor =
            ContextCompat.getColor(
                this,
                R.color.header_blue
            )


        if (isNightMode) {

            // Dark mode
            controller.isAppearanceLightStatusBars = false

        } else {

            // Light mode
            // Header ko'k bo'lgani uchun HAR DOIM oq ikonka
            controller.isAppearanceLightStatusBars = false
        }


        // Navigation bar
        controller.isAppearanceLightNavigationBars =
            !isNightMode
    }


    override fun onSupportNavigateUp(): Boolean {

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment
            ) as NavHostFragment

        val navController =
            navHostFragment.navController

        return navController.navigateUp() ||
                super.onSupportNavigateUp()
    }
}