package com.example.dpoae;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import androidx.fragment.app.Fragment;

public class Constants {
    static boolean DEBUG;
//    static PrepareFragment PrepareFragment;
    static MeasureFragment MeasureFragment;
    static SettingsFragment SettingsFragment;
    static Fragment CurrentFragment;
    static boolean testInProgress;

    static BottomNavigationView nav;
    static short[][] initbuffer;
    static double[][] sumbuffer;
    static int[] numTries;
    static double[] noise;
    static double[] signal;
    static boolean[] complete;
    static ArrayList<BarEntry> graphData;
    static ArrayList<Entry> lineData1;
    static ArrayList<Entry> lineData2;
    static View vv;
    static Activity context;

    static boolean AGC = false;

    // CALIBRATION
    static int PROBE_CHECK_MIN = 120;
    static int PROBE_CHECK_MAX = 150;
    static int PROBE_CHECK_SAFE_ZONE_START = 130;
    static int PROBE_CHECK_SAFE_ZONE_END = 140;

    // TEST
    static double SNR_THRESH = 7;
    static double SPL_THRESH = -50;

    static int MAX_TRIES = 1;
    static int samplingRate = 48000;

    static int samplen = (int)(Constants.samplingRate*1.5);
    static double RECORDING_WINDOW_IN_SECONDS = 1.5;
    static double PLAY_WINDOW_IN_MILLISECONDS = 1500;

//    static int PROCESS_WINDOW_LENGTH = (int)(.65*Constants.samplingRate);
    static int PROCESS_WINDOW_LENGTH = (int)(Constants.samplingRate);

//    static int INIT_SIGNAL_TRIM_LENGTH = (int)(Constants.samplingRate);
    static int INIT_SIGNAL_TRIM_LENGTH = (int)((Constants.samplingRate)*1);

    static int SIGNAL_DETECTION_THRESHOLD = 100;
    static int TEMPLATE_LENGTH = 500;

    static int ProbeCheckTone = 80;

    static boolean EARLY_STOPPING = false;
    static double CALIB_VOLUME = .5;
    static boolean OCTAVES;

    static int SEAL_CHECK_THRESH=80;
    static int SEAL_OCCLUSION_THRESH=500;

    static LinkedList<short[]> fullrec;

    static HashMap<Integer, Integer> freqLookup=new HashMap<Integer,Integer>();
    static HashMap<Integer, Integer> oaeLookup=new HashMap<Integer,Integer>();
    static HashMap<Integer, Integer> oaeLookup2=new HashMap<Integer,Integer>();
    static HashMap<Integer, Float> vol1LookupDefaults=new HashMap<Integer,Float>();
    static HashMap<Integer, Float> vol1Lookup=new HashMap<Integer,Float>();
    static HashMap<Integer, Float> vol2Lookup=new HashMap<Integer,Float>();
    static HashMap<Integer, Float> vol3Lookup=new HashMap<Integer,Float>();
    static LinkedList<Integer> octaves=new LinkedList<>();
    static LinkedList<Integer> volCalibMags=new LinkedList<>();
    static HashMap<Integer,Integer> volOffset = new HashMap<Integer,Integer>();
    static HashMap<Integer,Integer> volTarget = new HashMap<Integer,Integer>();
    static boolean[] freqs = new boolean[]{true, true, true, true, false, false, false, false};
    static AlertDialog dialog;
    static AlertDialog measureDialog;
    static boolean CALIBRATE = true;
    static boolean INTERLEAVED = false;
    static int CONSTANT_TONE_LENGTH_IN_SECONDS = 5;
    static double TONE_CALIB_LENGTH_IN_SECONDS = 0.2;
    static double EXAMINE_CALIB_LENGTH_IN_SECONDS = 0.1;
    static double PAD_CALIB_LENGTH_IN_SECONDS = 0.05;
    static int ART_THRESH = 4;
//    static int IMD_THRESH=100;
    static int IMD_THRESH=200;
    static String filename;
    static int volumeSetting = 2;
    static boolean SPL_CHECK = true;
    static int AMP_THRESH = 500;
    static boolean CHECK_FIT = false;
    static boolean VOL_CALIB=true;
    static String calibSig="tone";
    static double[] chirp;
    static boolean MEASURE_LOADER=true;
    static boolean SHOW_RESULT=false;
    static double totalSeconds=0;
    static double secondCounter=0;
    static String phone="sch3";
    static double volDefault;
    //lab is for paper experiments

    public static void init(Context context) {
        Log.e("asdf","init "+ Build.MODEL);
        if (Build.MODEL.equals("SM-G960U1")) {
            phone = "sch2";
        }
        else if (Build.MODEL.equals("S96Pro")) {
            phone="doogee_calib";
        }
        else if (Build.MODEL.equals("Pixel 3a XL")) {
            phone="pixel_calib";
        }
        else if (Build.MODEL.equals("Infinix X5515")) {
            phone="infinix_calib";
        }
        Log.e("asdf","PHONE "+phone);

//        PrepareFragment = new PrepareFragment();
        MeasureFragment = new MeasureFragment();
        SettingsFragment = new SettingsFragment();

        chirp=FileOperations.readrawasset(context,R.raw.chirp);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Constants.MAX_TRIES=prefs.getInt("maxtries",Constants.MAX_TRIES);
        Constants.OCTAVES=prefs.getBoolean("octaves",Constants.OCTAVES);
        Constants.EARLY_STOPPING=prefs.getBoolean("earlystop",Constants.EARLY_STOPPING);
        Constants.CALIBRATE=prefs.getBoolean("calibrate",Constants.CALIBRATE);
        Constants.INTERLEAVED =prefs.getBoolean("adaptive",Constants.INTERLEAVED);
        Constants.SPL_CHECK =prefs.getBoolean("spl",Constants.SPL_CHECK);
        Constants.CONSTANT_TONE_LENGTH_IN_SECONDS=prefs.getInt("constantToneLength",Constants.CONSTANT_TONE_LENGTH_IN_SECONDS);
        Constants.SEAL_CHECK_THRESH =prefs.getInt("checkFitThresh",Constants.SEAL_CHECK_THRESH);
        Constants.CHECK_FIT =prefs.getBoolean("checkFit",Constants.CHECK_FIT);
        Constants.VOL_CALIB =prefs.getBoolean("volCalib",Constants.VOL_CALIB);
        Constants.MEASURE_LOADER =prefs.getBoolean("measureLoader",Constants.MEASURE_LOADER);
        Constants.SHOW_RESULT =prefs.getBoolean("showResult",Constants.SHOW_RESULT);

        for (int i = 0; i < freqs.length; i++) {
            freqs[i] = prefs.getBoolean("check"+i, freqs[i]);
        }

        if (octaves.size() == 0) {
            freqLookup.put(1031,844);
            freqLookup.put(1500,1219);
            freqLookup.put(2016,1640);
            freqLookup.put(2953,2438);
            freqLookup.put(3985,3282);
            freqLookup.put(4969,4078);
            freqLookup.put(6000,4922);
            freqLookup.put(8016,6563);

            oaeLookup.put(1031,657);
            oaeLookup.put(1500,938);
            oaeLookup.put(2016,1264);
            oaeLookup.put(2953,1923);
            oaeLookup.put(3985,2579);
            oaeLookup.put(4969,3187);
            oaeLookup.put(6000,3844);
            oaeLookup.put(8016,5110);

            oaeLookup2.put(1031,1218);
            oaeLookup2.put(1500,1781);
            oaeLookup2.put(2016,2392);
            oaeLookup2.put(2953,3468);
            oaeLookup2.put(3985,4688);
            oaeLookup2.put(4969,5860);
            oaeLookup2.put(6000,7078);
            oaeLookup2.put(8016,9469);

            octaves.add(2016);
            octaves.add(2953);
            octaves.add(3985);
            octaves.add(4969);

            // deployment
            /////////////////////////////////////////??
            populateVolume();
        }
    }

    public static void populateVolume() {
        if (phone.equals("sch")) {
//            vol1LookupDefaults.put(2016, .95f);
//            vol1LookupDefaults.put(2953, .95f);
//            vol1LookupDefaults.put(3985, .95f);
//            vol1LookupDefaults.put(4969, .95f);
            vol1LookupDefaults.put(2016, .85f);
            vol1LookupDefaults.put(2953, .85f);
            vol1LookupDefaults.put(3985, .85f);
            vol1LookupDefaults.put(4969, .85f);
            volDefault=.85;
            SEAL_CHECK_THRESH=200;
        }
        else if (phone.equals("sch2")) {
            vol1LookupDefaults.put(2016, .95f);
            vol1LookupDefaults.put(2953, .95f);
            vol1LookupDefaults.put(3985, .95f);
            vol1LookupDefaults.put(4969, .95f);
            volDefault=.95;
//            vol1LookupDefaults.put(2016, .5f);
//            vol1LookupDefaults.put(2953, .5f);
//            vol1LookupDefaults.put(3985, .5f);
//            vol1LookupDefaults.put(4969, .5f);
//            volDefault=.5;
            SEAL_CHECK_THRESH=200;
        }
        else if (phone.equals("sch3")) {
            vol1LookupDefaults.put(2016, .5f);
            vol1LookupDefaults.put(2953, .35f);
            vol1LookupDefaults.put(3985, 1f);
            vol1LookupDefaults.put(4969, 1f);
//            volDefault=.95;
//            volDefault=.5;
            SEAL_CHECK_THRESH=200;
        }
        else if (phone.equals("s9_calib")){
            volDefault=.8;
        }
        else if (phone.equals("doogee_calib")){
            volDefault=1;
        }
        else if (phone.equals("pixel_calib")){
            volDefault=1;
        }
        else if (phone.equals("infinix_calib")){
            volDefault=1;
        }

//        vol1LookupDefaults.put(2016, (float)volDefault);
//        vol1LookupDefaults.put(2953, (float)volDefault);
//        vol1LookupDefaults.put(3985, (float)volDefault);
//        vol1LookupDefaults.put(4969, (float)volDefault);
        SEAL_CHECK_THRESH=200;
        /////////////////////////////////////////

        for (Integer i : octaves) {
            float val = vol1LookupDefaults.get(i);
            vol1LookupDefaults.put(freqLookup.get(i), val);
        }

        for(Integer i : vol1LookupDefaults.keySet()) {
            float val = vol1LookupDefaults.get(i);
            vol1Lookup.put(i,val);
        }

        ///////////////////////////////////
        if (phone.equals("sch")) {
//            vol3Lookup.put(1640, .9f);
//            vol3Lookup.put(2016, .85f);
//
//            vol3Lookup.put(2438, 1f);
//            vol3Lookup.put(2953, .7f);
//
//            vol3Lookup.put(3282, 1f);
//            vol3Lookup.put(3985, .8f);
//
//            vol3Lookup.put(4078, .5f);
//            vol3Lookup.put(4969, .55f);

            vol3Lookup.put(1640, .16f);
            vol3Lookup.put(2016, .04f);

            vol3Lookup.put(2438, .11f);
            vol3Lookup.put(2953, .03f);

            vol3Lookup.put(3282, .28f);
            vol3Lookup.put(3985, .03f);

            vol3Lookup.put(4078, .95f);
            vol3Lookup.put(4969, .26f);
        }
        else if (phone.equals("sch2")) {
            vol3Lookup.put(1640, .11f);
            vol3Lookup.put(2016, .05f);

            vol3Lookup.put(2438, .1f);
            vol3Lookup.put(2953, .04f);

            vol3Lookup.put(3282, .14f);
            vol3Lookup.put(3985, .03f);

            vol3Lookup.put(4078, .16f);
            vol3Lookup.put(4969, .05f);

//            vol3Lookup.put(1640, .4f);
//            vol3Lookup.put(2016, .2f);

//            vol3Lookup.put(2438, .13f);
//            vol3Lookup.put(2953, .04f);
//
//            vol3Lookup.put(3282, .14f);
//            vol3Lookup.put(3985, .03f);
//
//            vol3Lookup.put(4078, .16f);
//            vol3Lookup.put(4969, .05f);
        }
        else if (phone.equals("sch3")) {
            vol3Lookup.put(1640, .15f);
            vol3Lookup.put(2016, .05f);

            vol3Lookup.put(2438, .07f);
            vol3Lookup.put(2953, .09f);

            vol3Lookup.put(3282, .25f);
            vol3Lookup.put(3985, .03f);

            vol3Lookup.put(4078, .18f);
            vol3Lookup.put(4969, .08f);
        }
        else if (phone.equals("doogee_calib")) {
            vol3Lookup.put(1640, .2f);
            vol3Lookup.put(2016, .1f);

            vol3Lookup.put(2438, .13f);
            vol3Lookup.put(2953, .04f);

            vol3Lookup.put(3282, .14f);
            vol3Lookup.put(3985, .03f);

            vol3Lookup.put(4078, .16f);
            vol3Lookup.put(4969, .05f);
        }
        else {
            vol3Lookup.put(1640, .11f);
            vol3Lookup.put(2016, .05f);

            vol3Lookup.put(2438, .13f);
            vol3Lookup.put(2953, .04f);

            vol3Lookup.put(3282, .14f);
            vol3Lookup.put(3985, .03f);

            vol3Lookup.put(4078, .16f);
            vol3Lookup.put(4969, .05f);
        }
    }
}
