package com.example.dynamicscreenpoc.engine

data class FragmentInfo<T>(
    val key: String,
    val fragment: T
)

interface FragmentViewController<T> {

    val key: String
    val display: Boolean
    fun create(payload: String) : FragmentInfo<T>

}