package com.schneeloch.transitlib;

import com.google.common.collect.ImmutableList;

/**
 * Created by george on 3/9/15.
 */
public class PredictionForStop {
    private final ImmutableList<IPrediction> predictionList;
    private final long lastUpdate;

    public PredictionForStop(ImmutableList<IPrediction> predictionList, long lastUpdate) {
        this.predictionList = predictionList;
        this.lastUpdate = lastUpdate;
    }

    public ImmutableList<IPrediction> getPredictionList() {
        return predictionList;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
