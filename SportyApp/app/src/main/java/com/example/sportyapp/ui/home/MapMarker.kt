package com.example.sportyapp.ui.home

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MapMarker(latitude: Double, longitude: Double, title: String, snippet: String) : ClusterItem {
    private var mPosition: LatLng = LatLng(latitude, longitude)
    private var mTitle: String = title
    private var mSnippet: String = snippet

    override fun getSnippet(): String {
        return mSnippet
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getPosition(): LatLng {
        return mPosition
    }
}