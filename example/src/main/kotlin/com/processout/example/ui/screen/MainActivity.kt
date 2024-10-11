package com.processout.example.ui.screen

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.processout.example.R

class MainActivity : AppCompatActivity() {

    private lateinit var topSpacer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContentView(R.layout.activity_main)
        topSpacer = findViewById(R.id.top_spacer)
        adjustInsets()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun adjustInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(android.R.id.content)
        ) { content, insets ->
            topSpacer.updateLayoutParams {
                height = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars()
                            or WindowInsetsCompat.Type.displayCutout()
                ).top
            }
            content.setPaddingRelative(
                0, 0, 0,
                insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars()
                            or WindowInsetsCompat.Type.ime()
                ).bottom
            )
            insets
        }
    }
}
