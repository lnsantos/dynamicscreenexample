package com.example.dynamicscreenpoc.engine

interface FragmentUpdateController {

    fun onNotify(
        tagOrigin: String,
        element: Any
    )
    fun onRemoved(tag: String)
}
