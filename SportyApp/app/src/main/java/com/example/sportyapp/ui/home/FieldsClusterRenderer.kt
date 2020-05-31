package com.example.sportyapp.ui.home

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class FieldsClusterRenderer(context: Context, map: GoogleMap, manager: ClusterManager<MapMarker>) :
    DefaultClusterRenderer<MapMarker>(context, map, manager) {

    override fun onBeforeClusterItemRendered(item: MapMarker?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}