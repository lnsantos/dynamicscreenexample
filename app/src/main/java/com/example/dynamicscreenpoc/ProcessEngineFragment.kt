package com.example.dynamicscreenpoc

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.dynamicscreenpoc.engine.FragmentInfo
import com.example.dynamicscreenpoc.fragments.NameFragment
import com.example.dynamicscreenpoc.fragments.TabFragment
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

data class Item(
    @SerializedName("index") val i: Int,
    @SerializedName("type") val type: String,
    @SerializedName("metadata") val payload: String
)

data class DataPayload(
    @SerializedName("data") val data: List<Item>
)

class ProcessEngineFragment {

    fun start(ctx: Context): List<FragmentInfo<Fragment>> {
        val input = ctx.resources?.openRawResource(R.raw.payload)
        val reader = InputStreamReader(input)
        val json = String(IOUtils.toByteArray(reader))

        Log.i("TT::JSON", json)

        val gson = GsonBuilder()
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f?.name == "metadata"
                }

                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }
            }
            ).create()

        val list = listOf(
            TabFragment.createViewController(),
            NameFragment.createViewController()
        ).filter {
            it.display
        }

        val fragments = mutableListOf<FragmentInfo<Fragment>>()
        val data = gson.fromJson(json, DataPayload::class.java)

        data.data.sortedBy { it.i }.forEach { item ->
            list.find { it.key == item.type }?.let {
                fragments.add(it.create(item.payload))
            }
        }

        return fragments
    }
}