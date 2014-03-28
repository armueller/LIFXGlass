package com.t10.lifxglass.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.t10.lifxglass.R;
import com.t10.lifxglass.lifx.LifxWrapper;

import java.util.ArrayList;

import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.light.LFXLight;
import lifx.java.android.light.LFXLightCollection;


public class AllOffActivity extends Activity implements LFXLightCollection.LFXLightCollectionListener, LFXLight.LFXLightListener {

    private Runnable apoptosis = new Runnable() {
        @Override
        public void run() {
            AllOffActivity.this.finish();
        }
    };

    private Runnable timeout = new Runnable() {
        @Override
        public void run() {
            AllOffActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    textViewProgressMsg.setVisibility(View.INVISIBLE);
                    textViewNoLightsError.setVisibility(View.VISIBLE);
                    if (numLights == 0) {
                        textViewNoLightsError.setText(R.string.no_lights);
                    } else if (numLightsOff != numLights) {
                        String turnedOffLights = getString(R.string.turned_off_some_lights);
                        turnedOffLights = turnedOffLights.replace("$0", Integer.toString(numLightsOff));
                        turnedOffLights = turnedOffLights.replace("$1", Integer.toString(numLights));
                        textViewNoLightsError.setText(turnedOffLights);
                    }
                }
            });

            new Handler().postDelayed(apoptosis, 2000);
        }
    };

    private ProgressBar progressBar;
    private TextView textViewNoLightsError;
    private TextView textViewProgressMsg;

    private LifxWrapper lifxWrapper;
    private int numLights;
    private int numLightsOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lifxWrapper = new LifxWrapper(this);
        lifxWrapper.addLightCollectionListener(this);
        lifxWrapper.addLightListenersToAllLights(this);
        setLightInfo();

        setupViews();

        lifxWrapper.turnOffAllLights();
        new Handler().postDelayed(timeout, 4000);
    }

    private void setLightInfo() {
        ArrayList<LFXLight> lights = lifxWrapper.getAllLights();
        numLights = lights.size();
        numLightsOff = lifxWrapper.getNumberOfLightsCurrentlyOff();
    }

    private void setupViews() {
        bindViews();
        inflateData();
    }

    private void bindViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewNoLightsError = (TextView) findViewById(R.id.textViewNoLightsError);
        textViewProgressMsg = (TextView) findViewById(R.id.textViewProgressMsg);
    }

    private void inflateData() {
        progressBar.animate();
        textViewNoLightsError.setVisibility(View.INVISIBLE);
        textViewProgressMsg.setText(R.string.turning_off_all_lights);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifxWrapper.removeLightCollectionListener();
        lifxWrapper.removeAllLightListeners();
    }

    @Override
    public void lightCollectionDidAddLight(LFXLightCollection lightCollection, LFXLight light) {
        numLights++;
        light.addLightListener(this);
        if (light.getPowerState() == LFXTypes.LFXPowerState.OFF) {
            numLightsOff++;
        } else {
            light.setPowerState(LFXTypes.LFXPowerState.OFF);
        }
    }

    @Override
    public void lightCollectionDidRemoveLight(LFXLightCollection lightCollection, LFXLight light) {
        numLights--;
        light.removeLightListener(this);
    }

    @Override
    public void lightCollectionDidChangeLabel(LFXLightCollection lightCollection, String label) {

    }

    @Override
    public void lightCollectionDidChangeColor(LFXLightCollection lightCollection, LFXHSBKColor color) {

    }

    @Override
    public void lightCollectionDidChangeFuzzyPowerState(LFXLightCollection lightCollection, LFXTypes.LFXFuzzyPowerState fuzzyPowerState) {

    }

    @Override
    public void lightDidChangeLabel(LFXLight light, String label) {

    }

    @Override
    public void lightDidChangeColor(LFXLight light, LFXHSBKColor color) {

    }

    @Override
    public void lightDidChangePowerState(LFXLight light, LFXTypes.LFXPowerState powerState) {
        if (powerState == LFXTypes.LFXPowerState.OFF) {
            numLightsOff++;
            if (numLightsOff == numLights) {
                progressBar.setVisibility(View.INVISIBLE);
                textViewProgressMsg.setVisibility(View.INVISIBLE);
                String turnedOffLights = getString(R.string.turned_off_lights);
                turnedOffLights = turnedOffLights.replace("$0", Integer.toString(numLightsOff));
                textViewNoLightsError.setText(turnedOffLights);
                textViewNoLightsError.setVisibility(View.VISIBLE);
                new Handler().postDelayed(apoptosis, 2000);
            }
        }
    }
}
