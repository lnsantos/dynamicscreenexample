package com.example.dynamicscreenpoc.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.dynamicscreenpoc.R
import com.example.dynamicscreenpoc.engine.ChildrenUpdateController
import com.example.dynamicscreenpoc.engine.FragmentInfo
import com.example.dynamicscreenpoc.engine.FragmentViewController
import com.example.dynamicscreenpoc.type.TypeReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class NameFragment : Fragment(), ChildrenUpdateController {

    private lateinit var root: TextView

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_name, null)
        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root = view.findViewById(R.id.description)

        root.text = arguments?.getString(KEY).toString()
    }

    override fun onUpdate(element: Any) {
        val newName = element as UserPojo
        GlobalScope.launch(Dispatchers.Main) {
            delay((1000L..3000L).random())
            root.text = newName.name
        }
        root.text = "Carregando"
    }

    companion object {

        internal const val KEY = "NAME_KEY"
        const val TAG = "NameFragment::internal"

        @JvmStatic
        fun create(name: String) = NameFragment().apply {
            arguments = bundleOf(KEY to name)
        }

        fun createViewController() = ViewController()

        class ViewController : FragmentViewController<Fragment> {
            override val key: String = TypeReference.NAME.name
            override val display: Boolean = true

            override fun create(payload: String): FragmentInfo<Fragment> {
                return FragmentInfo(key, NameFragment.create(payload))
            }
        }
    }
}
