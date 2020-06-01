package com.example.sportyapp.ui.home

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MapMarker(
    latitude: Double,
    longitude: Double,
    title: String,
    val fieldID: Long,
    snippet: String?
) :
    ClusterItem {
    private var mPosition: LatLng = LatLng(latitude, longitude)
    private var mTitle: String = title
    private var mSnippet: String? = snippet

    constructor(latitude: Double, longitude: Double, title: String, fieldID: Long) : this(
        latitude,
        longitude,
        title,
        fieldID,
        null
    )

    override fun getSnippet(): String? {
        return mSnippet
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun toString(): String {
        return "$mTitle $mPosition"
    }
}