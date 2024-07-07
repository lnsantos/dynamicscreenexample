package com.example.dynamicscreenpoc.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.dynamicscreenpoc.engine.FragmentInfo
import com.example.dynamicscreenpoc.engine.FragmentUpdateController
import com.example.dynamicscreenpoc.engine.FragmentViewController
import com.example.dynamicscreenpoc.type.TypeReference
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserPojo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Parcelable

@Parcelize
data class TabPojo(
    @SerializedName("title") val title: String,
    @SerializedName("data")  val data: List<UserPojo>
) : Parcelable

class TabFragment : Fragment(), TabLayout.OnTabSelectedListener {

    companion object {

        internal const val KEY = "TABS"
        const val TAG = "ABC"

        @JvmStatic
        fun create(arg: TabPojo): TabFragment {
            val fragment = TabFragment()
            fragment.arguments = bundleOf(KEY to arg)
            return fragment
        }

        fun createViewController() = ViewController()

        class ViewController : FragmentViewController<Fragment> {
            override val key: String = TypeReference.TAB.name
            override val display: Boolean = true

            override fun create(payload: String): FragmentInfo<Fragment> {
                val data = Gson().fromJson(payload, TabPojo::class.java)
                return FragmentInfo(key, TabFragment.create(data))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is FragmentUpdateController) {
            "your activity must implement FragmentUpdateController"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): TabLayout {
        val root = TabLayout(requireContext())
        root.contentDescription = "ABC"
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val root = (view as TabLayout)
        val tabs = arguments?.getParcelable<TabPojo>(KEY)

        tabs?.data?.forEachIndexed { index, userPojo ->
            val tab = root.newTab()
            tab.setText(userPojo.name)
            tab.tag = userPojo
            tab.setId(userPojo.id)
            tab.contentDescription = "nome de ${userPojo.name}"

            root.addTab(tab, index)
        }

        root.addOnTabSelectedListener(this)
    }

    override fun onTabSelected(p: TabLayout.Tab?) {
        val inversionCallback = requireContext() as FragmentUpdateController

        if (p?.id == 2) {
            inversionCallback.onRemoved(TypeReference.TAB.name)
        } else {
            p?.tag?.let { inversionCallback.onNotify(TypeReference.TAB.name, it) }
        }
    }

    override fun onTabUnselected(p: TabLayout.Tab?) {}
    override fun onTabReselected(p: TabLayout.Tab?) {}
}