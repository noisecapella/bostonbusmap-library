package com.schneeloch.transitlib.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.common.collect.ImmutableList;
import com.schneeloch.transitlib.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.Maps;


public class NextbusPredictionsFeedParser extends DefaultHandler
{
    private static final String stopTagKey = "stopTag";
    private static final String minutesKey = "minutes";
    private static final String epochTimeKey = "epochTime";
    private static final String vehicleKey = "vehicle";
    private static final String affectedByLayoverKey = "affectedByLayover";
    private static final String dirTagKey = "dirTag";
    private static final String predictionKey = "prediction";
    private static final String directionKey = "direction";
    private static final String predictionsKey = "predictions";
    private static final String routeTagKey = "routeTag";
    private static final String delayedKey = "delayed";
    private static final String titleKey = "title";
    private static final String blockKey = "block";

    private static final String errorKey = "Error";

    private String directionTitle;
    private String routeId;
    private String stopId;

    private final long currentTimeMillis;

    private final Map<String, ImmutableList.Builder<IPrediction>> predictions;

    private final NextbusDirectionLookup directionLookup;

    public NextbusPredictionsFeedParser() {
        this.currentTimeMillis = System.currentTimeMillis();
        this.directionLookup = new NextbusDirectionLookup();

        this.predictions = Maps.newHashMap();
    }

    public void runParse(InputStream data) throws ParserConfigurationException, SAXException, IOException
    {
        //android.util.Xml.parse(data, Encoding.UTF_8, this);
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(data, this);
        data.close();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (qName.equals(predictionsKey))
        {

            routeId = attributes.getValue(routeTagKey);

            stopId = attributes.getValue(stopTagKey);
        }
        else if (qName.equals(directionKey)) {
            directionTitle = attributes.getValue(titleKey);
        }
        else if (qName.equals(predictionKey))
        {
            if (stopId != null && routeId != null)
            {
                int minutes = Integer.parseInt(attributes.getValue(minutesKey));

                long epochTime = Long.parseLong(attributes.getValue(epochTimeKey));

                String vehicleId = attributes.getValue(vehicleKey);

                boolean affectedByLayover = Boolean.parseBoolean(attributes.getValue(affectedByLayoverKey));

                boolean isDelayed = Boolean.parseBoolean(attributes.getValue(delayedKey));



                String dirTag = attributes.getValue(dirTagKey);
                String block = attributes.getValue(blockKey);

                String directionSnippet = directionLookup.getTitleAndName(dirTag);

                if (StringUtil.isEmpty(directionSnippet)) {
                    directionSnippet = directionTitle;
                }
                long arrivalTimeMillis = currentTimeMillis + minutes * 60 * 1000;

                NextbusPrediction prediction = new NextbusPrediction(routeId, vehicleId,
                        directionSnippet,
                        affectedByLayover, isDelayed,
                        block, minutes);

                ImmutableList.Builder<IPrediction> predictionsForStop = predictions.get(stopId);
                if (predictionsForStop == null) {
                    predictionsForStop = ImmutableList.builder();
                    predictions.put(stopId, predictionsForStop);
                }
                predictionsForStop.add(prediction);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(directionKey)) {
            directionTitle = null;
        }
    }

    public Map<String, PredictionForStop> getPredictions() {
        Map<String, PredictionForStop> ret = Maps.newHashMap();

        long currentMillis = System.currentTimeMillis();

        for (Map.Entry<String, ImmutableList.Builder<IPrediction>> entry : predictions.entrySet()) {
            PredictionForStop predictionForStop = new PredictionForStop(entry.getValue().build(), currentMillis);
            ret.put(entry.getKey(), predictionForStop);
        }

        return ret;
    }
}
