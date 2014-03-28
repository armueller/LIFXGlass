package com.t10.lifxglass.lifx;

import android.content.Context;

import java.util.ArrayList;

import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXTypes.LFXPowerState;
import lifx.java.android.light.LFXLight;
import lifx.java.android.light.LFXLight.LFXLightListener;
import lifx.java.android.light.LFXLightCollection.LFXLightCollectionListener;
import lifx.java.android.network_context.LFXNetworkContext;
import lifx.java.android.network_context.LFXNetworkContext.LFXNetworkContextListener;

/**
 * Created by austinmueller on 3/27/14.
 */
public class LifxWrapper {

    private Context context;
    private LFXNetworkContext networkContext;

    private LFXLightListener lightListener;
    private LFXLightCollectionListener lightCollectionListener;
    private LFXNetworkContextListener networkContextListener;

    public LifxWrapper(Context context) {
        this.context = context;
        networkContext = LFXClient.getSharedInstance(context).getLocalNetworkContext();
        if (!networkContext.isConnected()) {
            networkContext.connect();
        }
    }

    public void addLightCollectionListener(LFXLightCollectionListener listener) {
        this.lightCollectionListener = listener;
        networkContext.getAllLightsCollection().addLightCollectionListener(listener);
    }

    public void removeLightCollectionListener() {
        networkContext.getAllLightsCollection().removeLightCollectionListener(lightCollectionListener);
        this.lightCollectionListener = null;
    }

    public void addNetworkContextListener(LFXNetworkContextListener listener) {
        this.networkContextListener = listener;
        networkContext.addNetworkContextListener(networkContextListener);
    }

    public void removeNetworkContextListener() {
        networkContext.removeNetworkContextListener(networkContextListener);
        this.networkContextListener = null;
    }

    public void addLightListenersToAllLights(LFXLightListener listener) {
        this.lightListener = listener;
        ArrayList<LFXLight> lights = networkContext.getAllLightsCollection().getLights();
        for(LFXLight light : lights) {
            light.addLightListener(lightListener);
        }
    }

    public void removeAllLightListeners() {
        ArrayList<LFXLight> lights = networkContext.getAllLightsCollection().getLights();
        for(LFXLight light : lights) {
            light.removeLightListener(lightListener);
        }
        this.lightListener = null;
    }

    public ArrayList<LFXLight> getAllLights() {
        return networkContext.getAllLightsCollection().getLights();
    }

    public int getNumberOfLightsCurrentlyOn() {
        int numLightsOn = 0;
        ArrayList<LFXLight> lights = getAllLights();
        for(LFXLight light : lights) {
            if (light.getPowerState() == LFXPowerState.ON) {
                numLightsOn++;
            }
        }
        return numLightsOn;
    }

    public int getNumberOfLightsCurrentlyOff() {
        int numLightsOff = 0;
        ArrayList<LFXLight> lights = getAllLights();
        for(LFXLight light : lights) {
            if (light.getPowerState() == LFXPowerState.OFF) {
                numLightsOff++;
            }
        }
        return numLightsOff;
    }

    public int turnOnAllLights() {
        int numLights = networkContext.getAllLightsCollection().getLights().size();
        networkContext.getAllLightsCollection().setPowerState(LFXPowerState.ON);
        return numLights;
    }

    public int turnOffAllLights() {
        int numLights = networkContext.getAllLightsCollection().getLights().size();
        networkContext.getAllLightsCollection().setPowerState(LFXPowerState.OFF);
        return numLights;
    }
}
