/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.db;

import com.kam.earthquakeuk_kam.models.Earthquake;

import java.util.List;

public interface IEqDAO {

    List<Earthquake> fetchAllEarthquakes();

    boolean addEarthquake(Earthquake earthquake);

    boolean addEarthquakes(List<Earthquake> earthquakes);
}
