/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.kam.earthquakeuk_kam.helpers.ColorHelper;
import com.kam.earthquakeuk_kam.models.Earthquake;

public class MapClusterRenderer extends DefaultClusterRenderer<Earthquake> {

    public MapClusterRenderer(Context context, GoogleMap map,
                              ClusterManager<Earthquake> clusterManager) {

        super(context, map, clusterManager);
        IconGenerator mClusterIconGenerator = new IconGenerator(context);


    }

    @Override
    protected void onBeforeClusterItemRendered(Earthquake item, MarkerOptions markerOptions) {

        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(ColorHelper.getHue(item.getMagnitude())));

        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }


    @Override
    protected int getColor(int clusterSize) {
        return Color.rgb(160, 28, 36);
    }
}
