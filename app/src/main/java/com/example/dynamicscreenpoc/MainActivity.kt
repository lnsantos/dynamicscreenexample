package com.example.dynamicscreenpoc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.dynamicscreenpoc.engine.ChildrenUpdateController
import com.example.dynamicscreenpoc.engine.FragmentUpdateController
import com.example.dynamicscreenpoc.type.TypeReference

class MainActivity : FragmentActivity(), FragmentUpdateController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ProcessEngineFragment().start(this).forEach { info ->
            supportFragmentManager
                .beginTransaction()
                .apply { setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) }
                .add(R.id.container_content, info.fragment, info.key)
                .commitNowAllowingStateLoss()
        }
    }

    override fun onNotify(
        tagOrigin: String,
        element: Any
    ) {
        when(tagOrigin) {
            TypeReference.TAB.name -> requestRefreshAll(element)
        }
    }

    override fun onRemoved(tag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(tag) ?: return

        supportFragmentManager
            .beginTransaction()
            .apply {
                setCustomAnimations(0, android.R.anim.fade_out)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                onEnterAnimationComplete()
            }
            .remove(fragment)
            .commitNowAllowingStateLoss()
    }

    private fun requestRefreshAll(element: Any) {
        val fragments = supportFragmentManager.fragments.filter { it is ChildrenUpdateController }
        if (fragments.isNotEmpty()) {
            fragments.forEach { instance ->
                (instance as ChildrenUpdateController).onUpdate(element)
            }
        }
    }
}