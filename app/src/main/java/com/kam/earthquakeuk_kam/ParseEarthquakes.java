/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam;

import android.util.Log;

import com.kam.earthquakeuk_kam.models.Earthquake;
import com.kam.earthquakeuk_kam.models.Location;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ParseEarthquakes {

    private static final String TAG = "ParseEarthquakes";
    private final ArrayList<Earthquake> earthquakes;

    public ParseEarthquakes() {
        this.earthquakes = new ArrayList<>();
    }

    public List<Earthquake> getEarthquakes() {
        return earthquakes;
    }

    public void parse(String xml) {

        boolean status = true;
        Earthquake currentEarthquake = null;
        String text = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagname = parser.getName();

                if (eventType == XmlPullParser.START_TAG) {

                    if (tagname.equals("item")) {
                        currentEarthquake = new Earthquake();
                    }

                } else if (eventType == XmlPullParser.TEXT) {
                    text = parser.getText();

                } else if (eventType == XmlPullParser.END_TAG) {
                    if (tagname.equalsIgnoreCase("item")) {

                        earthquakes.add(currentEarthquake);

                    } else if (tagname.equalsIgnoreCase("link")) {

                        if (currentEarthquake != null) {
                            currentEarthquake.setLink(text);
                        }

                    } else if (tagname.equalsIgnoreCase("description") && currentEarthquake != null) {
                        String[] elements;
                        elements = text.split("([A-Za-z/ ]+?):");

                        String pattern = "EEE, dd MMM yyyy kk:mm:ss";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String dateString = elements[1].replace(" ;", "").trim();
                        currentEarthquake.setDate(simpleDateFormat.parse(dateString));

                        String locname = elements[2].replace(",", ", ");
                        Location loc = new Location(locname, elements[3]);
                        currentEarthquake.setLocation(loc);
                        currentEarthquake.setDepth(Integer.parseInt(elements[4].replace(" km ;", "").trim()));
                        currentEarthquake.setMagnitude(Double.parseDouble(elements[5].trim()));


                    } else if (parser.getName().equals("category") && currentEarthquake != null) {

                        currentEarthquake.setCategory(text);

                    }

                }

                eventType = parser.nextToken();
            }

        } catch (XmlPullParserException e) {
            status = false;
            e.printStackTrace();
        } catch (ParseException e) {
            status = false;
            Log.e(TAG, "Error Parsing Date: " + e.getMessage());
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }

    }

}
