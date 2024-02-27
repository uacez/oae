package com.example.dpoae;

import static com.example.dpoae.Constants.populateVolume;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class MeasureFragment extends Fragment {

    Button measureButton;
    Button cancelButton;
    ExtendedBarChart barChart;
    LineChart lineChart;
    static TextView result;
    static TextView fnameView,noiseView;
    View view;
    View view2;
    View view3;
    Random random = new Random(1);
    AudioStreamer gsp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.measure, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        barChart = view.findViewById(R.id.barchart);
        lineChart = view.findViewById(R.id.linechart);

//        Grapher.graphHelper(barChart, getActivity(), dummyData());
//        Grapher.graphHelper2(getActivity(), lineChart, dummyLineData2(), dummyLineData());

        fnameView = view.findViewById(R.id.fnameView);
        noiseView = view.findViewById(R.id.noiseView);
        result = view.findViewById(R.id.result);

        measureButton = view.findViewById(R.id.measureButton);

        measureButton.setEnabled(true);
        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measure();
            }
        });

        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setEnabled(false);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measure.cancel(true);
                gsp.stopit();
                try {
                    gsp.join();
                } catch (InterruptedException e) {
                    Log.e("ex",e.getMessage());
                }
                    rec.stopit();
                try {
                    rec.join();
                } catch (InterruptedException e) {
                    Log.e("ex",e.getMessage());
                }
                enableBottomBar(true);
                measureButton.setEnabled(true);
                cancelButton.setEnabled(false);
                FileOperations.writeRecToDisk(getActivity(), fnameView);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

        EditText pidVal = view.findViewById(R.id.patientID);
        int pid = prefs.getInt("pid",0);
        pidVal.setText(pid+"");

        pidVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                String ss = pidVal.getText().toString();
                if (ss.length()>0) {
                    editor.putInt("pid", Integer.parseInt(ss));
                    editor.commit();
                }
            }
        });

        pidVal.setSelectAllOnFocus(true);

        ImageView upButton = (ImageView)view.findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ss = pidVal.getText().toString();
                if (ss.length() == 0) {
//                    pidVal.setText(prefs.getInt("pid",0)+"");
                }
                else {
                    int pid = Integer.parseInt(ss);
                    pidVal.setText((pid + 1) + "");
                    editor.putInt("pid", (pid + 1));
                    editor.commit();
                }
            }
        });
        ImageView downButton = (ImageView)view.findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ss = pidVal.getText().toString();
                int pid = Integer.parseInt(ss);
                if (ss.length() == 0) {
//                    pidVal.setText(prefs.getInt("pid",0)+"");
                }
                else {
                    if (pid > 0) {
                        pidVal.setText((pid - 1) + "");
                        editor.putInt("pid",(pid-1));
                        editor.commit();
                    }
                }
            }
        });

        pidVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    pidVal.clearFocus();
                }
                return false;
            }
        });

//        String fname="2-right-1626126439246";
//        String fname="0-right-1626916413536";
//        String fname="0-left-1627369751912";
//        String fname="0-left-1627373796231";
//        String fname="0-left-1626816250268";
//        String fname="7-left-1626815932609";

        //9R moved in first part
        //9L, bad measurement.
//        String fname="7-left-1626815932609";
//        String fname="9-left-1627504834952";
//        String fname="9-right-1627504780627";
//        String fname="11-left-1627508818168";
//        String fname="11-right-1627508858230";
//        String fname="12-left-1627514023442";
//        String fname="12-right-1627514090667";
//        String fname="13-left-1627515507225";
//        String fname="13-right-1627515446344";

//        13R: 4/4 => 3/4 (still pass)
        //14L: 4/4 => 3/4 (still pass)
        //15R: 3/4 => 2/4 (changes result?)
        //15R: 2/4 => 1/4
        //15R: 3/4
        //17L: 3/4 => 2/4 (good)
        //18L: 2/4 => 0/4 (good)
        //18R: 1/4 => 0/4 (good)

//        String fname="14-left-1627924761489";
//        String fname="14-right-1627924453334";
//        String fname="15-right-1627933921494";
//        String fname="15-right-1627933983992";
//        String fname="15-right-1627934190776";

//        String fname="16-left-1627940569470";
//        String fname="16-right-1627940485115";
//        String fname="17-left-1628006990725";
//        String fname="17-right-1628007060047";
//        String fname="18-left-1628115098513";
//        String fname="18-right-1628115172445";
//        String fname="38-left-1629915271576";

        //part1
//        String[] fnames=new String[]{"12-left-1627514023442","12-right-1627514090667","13-left-1627515507225","13-right-1627515446344","14-left-1627924761489","14-right-1627924453334","15-right-1627934190776","16-left-1627940569470","16-right-1627940485115","17-left-1628006990725","18-left-1628115098513","18-right-1628115172445","19-left-1628526298057","19-right-1628526229242","20-left-1628532153156","20-right-1628532089572","21-left-1628542569980","22-left-1628546993559","22-right-1628547072513","23-left-1628552316985","23-right-1628552392013","24-left-1629139661713","24-right-1629139774372","25-left-1629223806979","25-right-1629223857735","26-left-1629241768271","26-right-1629241830358"};
        //part2
//        String[] fnames=new String[]{"27-left-1629306765225","28-left-1629311709484","28-right-1629311654437","29-left-1629313560866","29-right-1629313624231","30-left-1629822099988","30-right-1629822230759","31-left-1629822911766","31-right-1629822970177","32-left-1629828899696","32-right-1629829012314","33-left-1629832297940","33-right-1629832359783","34-left-1629837423506","34-right-1629837544262","35-left-1629839526012","35-right-1629839583134","36-left-combined","36-right-1629846584552","37-left-1629846902833","37-right-1629846960636","38-left-1629915271576","38-right-1629915332215","39-left-1629917013470","40-left-1629919440149","40-right-1629919370623","41-left-1629927782102","41-right-1629927875335","42-left-1629931842971","42-right-1629931773710"};

        //part1+part2
//        String[] fnames=new String[]{"12-left-1627514023442","12-right-1627514090667","13-left-1627515507225","13-right-1627515446344","14-left-1627924761489","14-right-1627924453334","15-right-1627934190776","16-left-1627940569470","16-right-1627940485115","17-left-1628006990725","18-left-1628115098513","18-right-1628115172445","19-left-1628526298057","19-right-1628526229242","20-left-1628532153156","20-right-1628532089572","21-left-1628542569980","22-left-1628546993559","22-right-1628547072513","23-left-1628552316985","23-right-1628552392013","24-left-1629139661713","24-right-1629139774372","25-left-1629223806979","25-right-1629223857735","26-left-1629241768271","26-right-1629241830358","27-left-1629306765225","28-left-1629311709484","28-right-1629311654437","29-left-1629313560866","29-right-1629313624231","30-left-1629822099988","30-right-1629822230759","31-left-1629822911766","31-right-1629822970177","32-left-1629828899696","32-right-1629829012314","33-left-1629832297940","33-right-1629832359783","34-left-1629837423506","34-right-1629837544262","35-left-1629839526012","35-right-1629839583134","36-left-combined","36-right-1629846584552","37-left-1629846902833","37-right-1629846960636","38-left-1629915271576","38-right-1629915332215","39-left-1629917013470","40-left-1629919440149","40-right-1629919370623","41-left-1629927782102","41-right-1629927875335","42-left-1629931842971","42-right-1629931773710"};

//        String[] fnames=new String[]{"12-left-1627514023442"};

        //part1+part (w/o errors)
//        String[] fnames=new String[]{"12-left-1627514023442","12-right-1627514090667","13-left-1627515507225","13-right-1627515446344","14-left-1627924761489","14-right-1627924453334","15-right-1627934190776","16-left-1627940569470","16-right-1627940485115","17-left-1628006990725","18-left-1628115098513","18-right-1628115172445","19-left-1628526298057","19-right-1628526229242","20-left-1628532153156","20-right-1628532089572","21-left-1628542569980","22-left-1628546993559","23-left-1628552316985","23-right-1628552392013","24-left-1629139661713","24-right-1629139774372","26-left-1629241768271","26-right-1629241830358","27-left-1629306765225","28-left-1629311709484","28-right-1629311654437","29-left-1629313560866","29-right-1629313624231","30-left-1629822099988","30-right-1629822230759","31-left-1629822911766","31-right-1629822970177","32-left-1629828899696","32-right-1629829012314","33-left-1629832297940","33-right-1629832359783","34-right-1629837544262","36-left-combined","37-left-1629846902833","37-right-1629846960636","38-left-1629915271576","39-left-1629917013470","40-right-1629919370623","41-left-1629927782102","41-right-1629927875335","42-left-1629931842971","42-right-1629931773710"};

        //dupes
//        String[] fnames=new String[]{"15-right-1627933921494","15-right-1627933983992","15-right-1627934190776","22-left-1628544710424","22-left-1628546993559","22-right-1628547072513","22-right-1628547134960","24-right-1629139739914","24-right-1629139774372","30-right-1629822163162","30-right-1629822230759","32-right-1629828958811","32-right-1629829012314","34-right-1629837488084","34-right-1629837544262","35-left-1629839449333","35-left-1629839526012","36-left-1629846444748","36-left-1629846490031","36-right-1629846531438","36-right-1629846584552","37-left-1629846832405","37-left-1629846902833","38-left-1629915207616","38-left-1629915271576","40-left-1629919196770","40-left-1629919257034","40-left-1629919440149"};

//        String[] fnames=new String[]{"28-left-1629311709484","28-right-1629311654437","29-right-1629313624231","31-left-1629822911766","31-right-1629822970177","34-right-1629837544262","41-right-1629927875335","42-left-1629931842971","42-right-1629931773710","27-left-1629306765225","29-left-1629313560866","30-left-1629822099988","30-right-1629822230759","32-left-1629828899696","32-right-1629829012314","33-left-1629832297940","33-right-1629832359783","36-left-combined","37-left-1629846902833","37-right-1629846960636","38-left-1629915271576","39-left-1629917013470","40-right-1629919370623","41-left-1629927782102"};

//        String[] fnames=new String[]{"41-right-1629927875335","42-left-1629931842971"};

//        String[] fnames=new String[]{"37-left-1629846832405","37-left-1629846902833","38-left-1629915207616","38-left-1629915271576","40-left-1629919196770","40-left-1629919257034","40-left-1629919440149"};
//        String[] fnames=new String[]{"2-left-1626126385184","2-right-1626126439246","5-left-1626805025861","5-right-1626805062974","6-left-1626811743688","6-right-1626811700505","7-left-1626815932609","7-right-1626815993744","8-right-1626821526471","9-left-1627504834952"};

//        String[] fnames=new String[]{"38-left-1629915207616","38-left-1629915271576"};
//        String[] fnames=new String[]{"40-left-1629919196770","40-left-1629919257034","40-left-1629919440149"};
//        25-left-1629223806979","25-right-1629223857735
//        String[] fnames=new String[] {"36-right-1629846531438","36-right-1629846584552"};

//        String[] fnames=new String[] {"41-right-1629927875335"};
//        String[] fnames=new String[]{"0-left-1635046021170"};

        int n = Constants.octaves.size();
        Constants.initbuffer = new short[n][Constants.PROCESS_WINDOW_LENGTH];
        Constants.sumbuffer = new double[n][Constants.PROCESS_WINDOW_LENGTH];
        Constants.complete=new boolean[n];
        Constants.numTries = new int[n];
        Constants.signal = new double[n];
        Constants.noise = new double[n];

        Constants.graphData = new ArrayList<>();
        Constants.lineData1 = new ArrayList<>();
        Constants.lineData2 = new ArrayList<>();
        float[] vals=new float[]{24,18,18,21};
        for (int i = 0; i < n; i++) {
            Constants.graphData.add(i, new BarEntry(Constants.octaves.get(i)/1000f, new float[]{(float) vals[i]}));
        }
        Grapher.graphHelper(barChart, getActivity(), Constants.graphData);

        boolean showOld=false;
        String[] fnames = new String[]{"0-left-1655605472059","0-left-1655605490797","0-left-1655605508473","0-left-1655606023171","0-left-1655606041669","0-left-1655606099161","0-left-1655605781684","0-left-1655605975773","0-left-1655605996011","0-left-1655605622874","0-left-1655605643500","0-left-1655605662817","0-left-1655605707583","0-left-1655605726860","0-left-1655605745289"};
//        String[] fnames = new String[]{"1-right-1643308698736","1-left-1643309070293","2-right-1643316965081","2-left-1643317149293","4-right-combined","5-right-1643329220820","5-left-1643328985635","11-right-1646853068138","11-left-1646852965735","13-right-1646931100765","13-left-1646931184462","15-right-1646940140158","15-left-1646940194912","17-right-1646950623532","17-left-1646950478308","13-right-1647475571658","13-left-1647475672044","14-right-1647622320739","14-left-1647622177654","15-right-1647628160109","16-right-1647629951500","16-left-1647629814049","17-left-1647636143349","18-left-1647636752179","26-right-1648493058494","26-left-1648493138582","27-right-1648501871192","27-left-1648501953220","28-right-1648574358019","31-right-1648670376087","31-left-1648670195221","33-right-1648681857274","33-left-1648681777607","33-right-combined","33-left-1649094533489","34-right-1648853089083","34-left-1648853012630","35-right-1648853821817","36-right-combined","36-left-1649096280646","39-right-1649273816213","39-left-1649274756487","40-left-1649282286421","41-right-1649438242350","42-right-1649439582718","42-left-1649439497367","44-left-1649457751513","45-left-1649458864950","46-right-1649872930642","46-left-1649872856338","47-left-1649885268375","48-right-1649890234487","48-left-1649890188044","50-right-1649968438591","50-left-1649968361363","51-right-1649970330084","52-left-1649972929997","54-right-1650041814448","54-left-1650041877737","55-right-1650050214396","55-left-1650049897963","56-left-1650058000752","57-right-1650304046651","57-left-1650303944012","58-right-1650308297236","58-left-1650308383116","32-left-1648673829539","37-right-1649266235074","37-left-1649266073252","38-right-1649267611177","38-left-1649267446217","62-right-1650905273149","62-left-1650905106477","63-left-1650906609619","64-left-1650911568553","65-right-1651009908843","65-left-1651010280162","66-right-1651098161199","66-left-1651098236382","67-right-1651170169914","67-left-1651169572708","68-right-combined","68-left-1651183321804","69-right-1651252417326","69-left-1651252096985","70-right-1651526140231","73-right-1651694679544","73-left-1651694950115","101-right-combined","101-left-1648578860737","102-right-combined","102-left-1648598277517","103-right-1649095119478","103-left-1649095063272","104-right-1649203179545","104-left-1649203118066","105-right-1649286250165","105-left-1649285992935","106-right-1649788298877","106-left-1649788257709","107-right-1649804969072","107-left-1649805173880","108-left-1649892251571","109-left-1650493920385","110-right-1651517846241","110-left-1651517769282"};
//        String[]fnames=new String[]{"74-right-1651703222780","74-right-1651703155678","74-left-1651703442144","74-left-1651703485097","76-right-1651878879554","76-left-1651878958867","77-right-1652119279589","77-left-1652118964671"};
//        for (String fname : fnames) {
//            String filename = fname+"-volcalib.txt";
//            String dir = getActivity().getExternalFilesDir(null).toString();
//            File file = new File(dir + File.separator + filename);
//            if(file.exists()) {
//                short[] ss2 = FileOperations.readfromfile_short(getActivity(), filename);
//                String out = Utils.volcalib(ss2);
//                Log.e("asdf",filename+" "+out);
//            }
//            else {
//                Log.e("asdf",filename);
//            }
//        }

        if (showOld) {
//            all w/o errs
            // these are the original 95 ears
//            String[] fnames=new String[]{usb
//            "12-left-1627514023442","12-right-1627514090667","13-left-1627515507225","13-right-1627515446344","14-left-1627924761489","14-right-1627924453334","15-right-1627934190776","16-left-1627940569470","16-right-1627940485115","17-left-1628006990725","18-left-1628115098513","18-right-1628115172445","19-left-1628526298057","19-right-1628526229242","20-left-1628532153156","20-right-1628532089572","21-left-1628542569980","22-left-1628546993559","23-left-1628552316985","23-right-1628552392013","24-left-1629139661713","24-right-1629139774372","26-left-1629241768271","26-right-1629241830358","27-left-1629306765225","28-left-1629311709484","28-right-1629311654437","29-left-1629313560866","29-right-1629313624231","30-left-1629822099988","30-right-1629822230759","31-left-1629822911766","31-right-1629822970177","32-left-1629828899696","32-right-1629829012314","33-left-1629832297940","33-right-1629832359783","34-right-1629837544262","36-left-combined","37-left-1629846902833","37-right-1629846960636","38-left-combined","39-left-1629917013470","40-left-combined","40-right-1629919370623","41-left-1629927782102","41-right-1629927875335","42-left-1629931842971","42-right-1629931773710","43-left-1630344383191","43-right-1630344437451","44-left-1630428346625","44-right-1630428232014","45-left-out","45-right-1630435725238","49-left-1630538593353","49-right-1630538646201","51-left-1630693084508","52-left-1630702192359","53-left-1630702963554","53-right-1630703075415","54-left-1630704192862","54-right-1630704259032","55-left-1630709241158","55-right-1630709294114","56-left-1631122878287","56-right-1631122935498","57-left-1631123328450","57-right-1631123451150","58-left-1631128240158","58-right-1631128175470","59-left-1631134065574","59-right-1631134119341","60-left-1631138602556","61-right-1631204745698","62-left-1631206355355","62-right-1631206077849","63-left-1631212753764","64-left-1631308035156","64-right-1631308090776","65-left-1631316191950","65-right-1631316080772","66-left-1631549199771","66-right-1631549275922","67-left-1631550566831","67-right-1631550619157","68-left-1631553471710","68-right-1631553570793","69-left-1631558583217","69-right-1631558638211","70-left-1631642758913","70-right-1631642819344","71-left-1631658285530","71-right-1631658337930","72-left-1631660634645","72-right-combined"};
            // 40-left is not used

//            String[] fnames=new String[]{"23-left-1628552316985","26-left-1629241768271","26-right-1629241830358","53-right-1630703075415","56-right-1631122935498","64-right-1631308090776","65-right-1631316080772"};
//            String[]fnames=new String[]{"22-right-1628547072513","22-right-1628547134960","25-left-1629223806979","25-right-1629223857735","47-right-1630529375616"};
//            String[] fnames=new String[]{"34-left-1629837423506","35-left-1629839449333","35-left-1629839526012","35-right-1629839583134","36-right-1629846531438","36-right-1629846584552","38-right-1629915332215","60-right-1631138770426","61-left-1631204654328"};
//            String[] fnames =new String[]{"38-right-1629915332215","60-right-1631138770426","61-left-1631204654328"};
//            String[] fnames=new String[]{"66-left-1631549199771"};®

            boolean revisions=false;
            // revisions

//            String[] fnames = new String[]{"1-right-1643308698736","1-left-1643309070293","2-right-1643316965081","2-left-1643317149293",
//                    "4-right-combined","5-right-1643329220820","5-left-1643328985635","11-right-1646853068138",
//                    "11-left-1646852965735","13-right-1646931100765","13-left-1646931184462","15-right-1646940140158","15-left-1646940194912",
//                    "17-right-1646950623532","17-left-1646950478308","13-right-1647475571658","13-left-1647475672044","14-right-1647622320739",
//                    "14-left-1647622177654","15-right-1647628160109","16-right-1647629951500","16-left-1647629814049","17-left-1647636143349",
//                    "18-left-1647636752179","26-right-1648493058494","26-left-1648493138582","27-right-1648501871192","27-left-1648501953220",
//                    "28-right-1648574358019","31-right-1648670376087","31-left-1648670195221","33-right-1648681857274","33-left-1648681777607",
//                    "33-right-combined","33-left-1649094533489","34-right-1648853089083","34-left-1648853012630","35-right-1648853821817",
//                    "36-right-combined","36-left-1649096280646","39-right-1649273816213","39-left-1649274756487","40-left-1649282286421",
//                    "32-left-1648673829539","37-right-1649266235074","37-left-1649266073252","38-right-1649267611177","38-left-1649267446217",
//                    "101-right-combined","101-left-1648578860737","102-right-combined","102-left-1648598277517","103-right-1649095119478",
//                    "103-left-1649095063272","104-right-1649203179545","104-left-1649203118066","105-right-1649286250165","105-left-1649285992935",
//                    "106-right-1649788298877","106-left-1649788257709","107-right-1649804969072","107-left-1649805173880","108-left-1649892251571"};
//            String[] fnames = new String[]{"41-left-1649437814191","41-right-1649438036689","41-right-1649438242350","42-left-1649439497367","42-right-1649439582718","43-left-1649441453889","43-right-1649441347576","43-right-1649441397130","44-left-1649457751513","44-right-1649457389923","45-left-1649458864950","46-left-1649872856338","46-right-1649872930642","47-left-1649885268375","47-right-1649885024361","48-left-1649890188044","48-right-1649890234487","49-left-1649893909156","49-right-1649893971730"};
//            String[] fnames = new String[]{"50-left-1649968361363","50-right-1649968438591","51-left-1649970392128","51-right-1649970330084","52-left-1649972929997","52-right-1649972857669","53-left-1649973389071","53-right-1649973304287","54-left-1650041877737","54-right-1650041814448","55-left-1650049897963","55-right-1650050214396","56-left-1650058000752","56-right-1650057685919","57-left-1650303944012","57-left-1650304006072","57-right-1650304086077","58-left-1650308383116","58-left-1650308426774","58-right-1650308297236","59-right-1650496727983","59-right-1650496784256","59-right-1650496907884"};
//            String[] fnames=new String[]{"109-left-1650493920385"};
//            String[] fnames=new String[]{"57-right-1650304046651","57-right-1650304086077","58-left-1650308383116","58-right-1650308297236"};

//            String[] fnames=new String[]{"35-left-1648853493678",,"24-left-1648067854054","24-right-1648067544579","43-left-1649441453889","43-right-1649441347576"};
//            "43-left-1649441453889","43-right-1649441347576","24-left-1648067854054","24-right-1648067544579","35-left-1648853493678"
//            String[] fnames=new String[]{"61-left-1650578369452","62-left-1650905106477","62-right-1650905273149","63-left-1650906609619","63-left-1650906649467","63-right-1650906729558","63-right-1650906856097","64-left-1650911568553","64-right-1650911509682","65-left-1651010280162","65-right-1651009908843","66-left-1651098236382","66-right-1651098161199","67-left-1651169572708","67-right-1651170169914","68-left-1651183321804","68-right-1651183396393","68-right-1651183480881"};
//            String[]fnames = new String[]{};

            if (!revisions) {
                fnames=new String[]{};
            }
            Log.e("final","N="+fnames.length);
            for (String fname:fnames) {
                short[] ss = FileOperations.readfromfile_short(getActivity(), fname+".txt");
                if (ss.length > 0) {
                    int numfreqs = 4;
                    int defaultCutLen=(int)(ss.length/48e3/4);
                    int defaultSegLen = (int)(ss.length/48e3/4);
//                    defaultCutLen=6;
//                    int defaultSegLen=4;
//                    Log.e("final","file len "+ss.length/48000+","+defaultCutLen+","+defaultSegLen);

                    int beginCutLen=0;
                    int endCutLen = Constants.samplingRate * defaultCutLen;
                    int segLen = Constants.samplingRate * defaultSegLen;

//                    beginCutLen=48000*4;
//                    endCutLen=48000*6;

//                    if (fname.equals("12-left-1627514023442")||
//                        fname.equals("12-right-1627514090667")||
//                        fname.equals("13-left-1627515507225")||
//                        fname.equals("13-right-1627515446344")) {
//                        segLen=Constants.samplingRate * 9;
//                    }
//                    else if (fname.equals("14-left-1627924761489")||
//                             fname.equals("14-right-1627924453334")) {
//                        segLen=Constants.samplingRate * 7;
//                    }
//                    else if (fname.equals("18-left-1628115098513")||
//                             fname.equals("18-right-1628115172445")) {
//                        segLen=Constants.samplingRate * 10;
//                    }
//                    if (revisions) {
//                        segLen=Constants.samplingRate*6;
//                    }

                    String out1 = "";
                    String out2 = "";
                    int passcounter = 0;
                    double[] snrs = new double[]{0, 0, 0, 0};
                    double[] signal = new double[]{0, 0, 0, 0};
                    double[] noise = new double[]{0, 0, 0, 0};
                    double[] ambient = new double[]{0, 0, 0, 0};
                    for (int i = 0; i < numfreqs; i++) {
                        if (i == 0 || i == 1 || i==2 || i==3) {
//                        if (i == 1) {
                            int cc = segLen*i+beginCutLen;
                            int ee = segLen*i+endCutLen;
//                            int ee = cc+endCutLen;

//                            if (fname.equals("12-left-1627514023442")||
//                                fname.equals("12-right-1627514090667")||
//                                fname.equals("13-left-1627515507225")||
//                                fname.equals("13-right-1627515446344")||
//                                fname.equals("18-left-1628115098513")||
//                                fname.equals("18-right-1628115172445")) {
//                                ee=cc+cutLen;
//                            }

                            short[] samples = Arrays.copyOfRange(ss, cc, ee);
                            float freq = Constants.octaves.get(i);
                            double[] ret = Signal.work(samples, (int) freq, i);
//                            double[] p = new double[]{0,0};
//                            double signalspl=p[0]*ret[0]+p[1];
//                            double noisespl=p[0]*ret[1]+p[1];
//                            double signalspl=ret[0]-90;
//                            double noisespl=ret[1]-90;
//                            out1 += String.format("%5.0f",ret[0]-ret[1]);
//                            out2 += String.format("%5.3f,",ret[1]);
//                            Log.e("final",String.format("%s %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f",fname,ret[0],ret[1],ret[0]-ret[1],ret[2],ret[3],ret[2]-ret[3]));
//                            Log.e("final",String.format("%s %5.3f",fname,ret[0]-ret[1]));
//                            Log.e("final",String.format("%s  %.1f %.1f %.0f %.0f",fname,ret[0],ret[1],signalspl,noisespl));
//                            Log.e("final",String.format("%-30s %-10.0f %-10.0f",fname,ret[1],noisespl));
                            snrs[i] = ret[0]-ret[1];
                            signal[i] = ret[0];
                            noise[i] = ret[1];
                            ambient[i]=ret[2];
//                            if (ret[0] >= Constants.SNR_THRESH) {
//                                passcounter += 1;
//                            }
                        }
                    }
//                    Log.e("final",String.format("%s ",fname));
                    String noisy=checkNoiseThresholds(getActivity());
//                    Log.e("final",fname+" "+out1);
//                    Log.e("final",fname+" "+out2);
//                    Log.e("final"," l");
//                    String passval = "F";
//                    if (passcounter >= 3) {
//                        passval = "P";
//                    }

//                    Log.e("final", String.format("%25s %d\t%d\t%d\t%d", fname, (int)Math.ceil(snrs[0]), (int)Math.ceil(snrs[1]), (int)Math.ceil(snrs[2]), (int)Math.ceil(snrs[3])));
//                    Log.e("final", String.format("%25s %.0f\t%.0f\t%.0f\t%.0f\t%.2f\t%.2f\t%.2f\t%.2f\t%s", fname, Math.ceil(snrs[0]),Math.ceil(snrs[1]),Math.ceil(snrs[2]),Math.ceil(snrs[3]),noise[0], noise[1], noise[2], noise[3],noisy));
                    Log.e("final", String.format("%25s %.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%s", fname, signal[0], signal[1], signal[2], signal[3] ,noise[0], noise[1], noise[2], noise[3], ambient[0],ambient[1],ambient[2],ambient[3], noisy));
//                        Log.e("final", String.format("%25s %3d %3d %3d %3d %3d %3d %3d %3d",fname,snrs[0],snrs[1],snrs[2],snrs[3],imds[0],imds[1],imds[2],imds[3]));
//                    Log.e("final",defaultCutLen+"");
                    Grapher.graph(barChart, lineChart, getActivity(), Constants.graphData, Constants.lineData1, Constants.lineData2, false);

                    checkStatus(getActivity());
                }
            }
        }
        else {
            int numfreqs = 4;
            for (int i = 0; i < numfreqs; i++) {
                if (Constants.freqs[i]) {
                    float freq = Constants.octaves.get(i);
                    Constants.graphData.set(i, new BarEntry(freq / 1000f, new float[]{(float) vals[i]}));
                    float xx = (float) roundToHalf(Constants.octaves.get(i) / 1000.0);
                    Constants.lineData1.add(new Entry(xx, (float) 0));
                    Constants.lineData2.add(new Entry(xx, (float) 0));
                }
            }
            Grapher.graph(barChart, lineChart, getActivity(), Constants.graphData, Constants.lineData1, Constants.lineData2, false);
        }

        return view;
    }

    public static String checkNoiseThresholds(Activity a) {
//        double[] threshs=new double[]{96,94,95,85};
        double[] threshs=new double[]{96,94,95,87};
        String out="";
        int ncounter=0;
        for (int i = 0; i < threshs.length; i++) {
            if (Constants.noise[i] >= threshs[i]) {
                out+="noisy ";
                ncounter+=1;
            }
            else {
                out+="ok ";
            }
//            out += ((int)Constants.noise[i])+",";
        }
        if (ncounter >= 3) {
            out += "retry";
        }
        else {
            out += "noise-pass";
        }
//        Log.e("final",String.format("%s %.0f %.0f %.0f %.0f",out,Constants.noise[0],Constants.noise[1],Constants.noise[2],Constants.noise[3]));
        if (Constants.phone.contains("sch")) {
            String finalOut = out;
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noiseView.setText(finalOut);
                }
            });
        }
        else if (out.contains("retry")) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(a)
                            .setTitle("Error")
                            .setMessage("Too noisy, retry")
                            .setPositiveButton("Ok", null)
                            .setCancelable(false).create();
                    dialog.show();
                }
            });
        }
        return out;
    }

    public static void checkStatus(Activity a) {
        int ncounter=0;
        for (int i = 0; i < 4; i++) {
//            Log.e("justin","loop "+Constants.complete[i]);
            if (Constants.complete[i]) {
                ncounter++;
            }
        }

//        Log.e("justin","ncounter "+ncounter);

        if (Constants.phone.contains("kenya")) {
            int finalNcounter = ncounter;
//            Log.e("justin","finalNcounter "+finalNcounter);
//            a.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            if (Constants.SHOW_RESULT) {
                if (finalNcounter >= 3) {
                    result.setText("Pass");
                } else {
                    result.setText("Refer");
                }
            }
//                }
//            });
        }
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public ArrayList<BarEntry> dummyData() {
        ArrayList<BarEntry> data = new ArrayList<>();
        float[] dat=new float[]{5,5,5,5};
        for (int i = 0; i < Constants.octaves.size(); i++) {
            float xx= Constants.octaves.get(i)/1000f;
            data.add(new BarEntry(xx,new float[]{dat[i]}));
        }
        return data;
    }
//
//    public ArrayList<Entry> dummyLineData() {
//        ArrayList<Entry> data = new ArrayList<>();
//
//        data.add(new Entry(1.000f, 9.7f));
//        data.add(new Entry(1.500f, 12.3f));
//        data.add(new Entry(2.000f, 16.2f));
//        data.add(new Entry(3.000f, 15.2f));
//        data.add(new Entry(4.000f, 12.6f));
//        data.add(new Entry(5.000f, 15f));
//        data.add(new Entry(6.000f, 8.2f));
//        data.add(new Entry(8.000f, 10.8f));
//        return data;
//    }
//
//    public ArrayList<Entry> dummyLineData2() {
//        ArrayList<Entry> data = new ArrayList<>();
//        data.add(new Entry(1.000f, 1.3f));
//        data.add(new Entry(1.500f, 1.6f));
//        data.add(new Entry(2.000f, -12.8f));
//        data.add(new Entry(3.000f, -8.6f));
//        data.add(new Entry(4.000f, -11.2f));
//        data.add(new Entry(5.000f, -11f));
//        data.add(new Entry(6.000f, -19.3f));
//        data.add(new Entry(8.000f, -18.4f));
//        return data;
//    }

    public double[] convert2(short[] sig) {
        double[] out = new double[sig.length];
        for (int i = 0; i < sig.length; i++) {
            out[i] = sig[i];
        }
        return out;
    }

    AsyncTask<Integer,Void,Void> measure;
    OfflineRecorder rec;
    public void measure() {
        try {
            measure = new SendSignal(getActivity(), barChart, lineChart, measureButton, cancelButton).execute();
        }
        catch(Exception e) {
            Log.e("ex","measure");
            Log.e("ex",e.getMessage());
        }
    }

    private void enableBottomBar(boolean enable){
        for (int i = 0; i < Constants.nav.getMenu().size(); i++) {
            Constants.nav.getMenu().getItem(i).setEnabled(enable);
        }
    }

    private class SendSignal extends AsyncTask<Integer,Void,Void> {
        Activity context;
        Button measureButton;
        Button cancelButton;
        BarChart chart;
        LineChart lineChart;

        public SendSignal(Activity context, BarChart chart, LineChart lineChart, Button measureButton, Button cancelButton) {
            this.measureButton = measureButton;
            this.cancelButton = cancelButton;
            this.chart = chart;
            this.lineChart=lineChart;
            this.context=context;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            enableBottomBar(false);
            this.measureButton.setEnabled(false);
            this.cancelButton.setEnabled(true);

            TextInputEditText et = (TextInputEditText)context.findViewById(R.id.patientID);
            Chip leftChip = (Chip)context.findViewById(R.id.leftChip);

            String pid = et.getText().toString().length() == 0 ? "0" : et.getText().toString();
            String ear = leftChip.isChecked() ? "left" : "right";

            long tt = System.currentTimeMillis();
            Constants.filename = pid+"-"+ear+"-"+tt;

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm.ss aa");

            String sub = (Constants.filename.substring(0,Constants.filename.length()-4)+"-"+Constants.filename.substring(Constants.filename.length()-4,Constants.filename.length()));
//            Log.e("outfile","filename "+sub);
            fnameView.setText(sub+" "+format.format(new Date(tt)));

        }

        protected void onPostExecute (Void result) {
            super.onPostExecute(result);
            Log.e("asdf","postexec");
            checkNoiseThresholds(getActivity());
            checkStatus(getActivity());
            this.measureButton.setEnabled(true);
            this.cancelButton.setEnabled(false);
            enableBottomBar(true);
            FileOperations.writeRecToDisk(context, fnameView);
        }

        public Void doInBackground(Integer... params) {
            int n = Constants.octaves.size();

            Constants.initbuffer = new short[n][Constants.PROCESS_WINDOW_LENGTH];
            Constants.sumbuffer = new double[n][Constants.PROCESS_WINDOW_LENGTH];
            Constants.numTries = new int[n];
            Constants.signal = new double[n];
            Constants.noise = new double[n];
            Constants.complete = new boolean[n];

            Constants.graphData = new ArrayList<>();
            Constants.lineData1 = new ArrayList<>();
            Constants.lineData2 = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Constants.graphData.add(i, new BarEntry(Constants.octaves.get(i)/1000f, new float[]{(float) 0}));
            }
            Constants.fullrec = new LinkedList<>();

            for (Integer i : Constants.vol1LookupDefaults.keySet()) {
                Constants.vol1Lookup.put(i, Constants.vol1LookupDefaults.get(i));
            }

            Log.e("asdf","constant");
            if (Constants.CHECK_FIT) {
                checkfit();
            }

            if (Constants.VOL_CALIB) {
                vol_calib();
            }
            else {
                populateVolume();
            }

            ProgressBar pb2 = null;
            if (Constants.MEASURE_LOADER) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Constants.measureDialog = measureDialog();
                        Constants.measureDialog.show();
                    }
                });
                try {
                    while (view3 == null) {
                        Log.e("asdf", "view3 is null");
                        Thread.sleep(100);
                    }
                    pb2 = view3.findViewById(R.id.progressBar3);
                    while (pb2==null) {
                        pb2=view3.findViewById(R.id.progressBar3);
                        Thread.sleep(100);
                    }
                }
                catch(Exception e) {
                    Log.e("asdf",e.getMessage());
                }
            }

            double fcountertotal=0.0;
            for (int i = 0; i < n; i++) {
                if (Constants.freqs[i]) {
                    fcountertotal+=1;
                }
            }

            Constants.vv=getActivity().getWindow().getDecorView().getRootView();
            Constants.context=getActivity();

            Constants.totalSeconds = (Constants.CONSTANT_TONE_LENGTH_IN_SECONDS+1.5)*fcountertotal;
            int fcounter=1;
            Log.e("asdf","tones");
            for (int i = 0; i < Constants.freqs.length; i++) {
                Log.e("asdf",i+":"+Constants.freqs[i]);
            }
            for (int i = 0; i < n; i++) {
                if (Constants.freqs[i]) {
                    float freq = Constants.octaves.get(i);
                    final int finalFcounter = fcounter;
                    final double finalFcountertotal = fcountertotal;
                    final int progress = (int) ((finalFcounter / finalFcountertotal)*100);

                    if (progress==100) {
                        sendTone((int) freq, i, 0, true);
                    }
                    else {
                        sendTone((int) freq, i, 0, false);
                    }

                    if (Constants.MEASURE_LOADER) {
                        final ProgressBar finalPb = pb2;
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("asdf","prevprog1 "+finalPb.getProgress());
                                Log.e("asdf","progress "+progress);
                                finalPb.setProgress(progress);
                                Log.e("asdf","prevprog2 "+finalPb.getProgress());
                            }
                        });
                    }

                    if (fcounter < fcountertotal) {
                        Log.e("asdf","sleep");
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception e) {
//                            Log.e("asdf", e.getMessage());
//                        }
                    }

                    fcounter+=1;
                }
            }

            if (Constants.MEASURE_LOADER) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Constants.measureDialog!= null) {
                            Constants.measureDialog.dismiss();
                            Constants.measureDialog=null;
                        }
                    }
                });
                view3=null;
            }

            Log.e("asdf","done");
            return null;
        }

        public
        <T extends Comparable<? super T>> ArrayList<T> asSortedList(Collection<T> c) {
            ArrayList<T> list = new ArrayList<T>(c);
            java.util.Collections.sort(list);
            return list;
        }

        public ArrayList<Integer> getList() {
            ArrayList<Integer> freqs = asSortedList(Constants.vol1LookupDefaults.keySet());
            ArrayList<Integer> freqs2 = new ArrayList<>();
            for (int i = 0; i < freqs.size(); i++) {
                if (Constants.freqs[i/2]) {
                    freqs2.add(freqs.get(i));
                }
            }
            return freqs2;
        }

        public AlertDialog loadingDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflator = getLayoutInflater();
            view2 = inflator.inflate(R.layout.load_dialog, null);
            builder.setView(view2);
            builder.setCancelable(false);
            return builder.create();
        }

        public AlertDialog measureDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflator = getLayoutInflater();
            view3 = inflator.inflate(R.layout.ongoing, null);
            builder.setView(view3);
            builder.setCancelable(true);
            return builder.create();
        }

        public void vol_calib() {
            short[] pre=Utils.generateChirpSpeaker2(1000,4000,.1,Constants.samplingRate,0,1);
            int padlen=4800;
            short[] output_signal=new short[padlen+(int)((pre.length+(8*Constants.TONE_CALIB_LENGTH_IN_SECONDS * Constants.samplingRate)))*2];
            int counter=padlen;
            for (int i = 0; i < pre.length; i++) {
                output_signal[counter++]=pre[i];
            }

            for (int i = 0; i < 4; i++) {
                int freq = Constants.octaves.get(i);

                int f1 = Constants.freqLookup.get(freq);
                int f2 = freq;

                short[] pulse1;
                short[] pulse2;
                Log.e("asdf", "get " + f1);
                Log.e("asdf", "get " + f2);
                float vol3a = Constants.vol3Lookup.get(f1);
                float vol3b = Constants.vol3Lookup.get(f2);

                pulse1 = SignalGenerator.sine2speaker(f1, f2,
                        Constants.samplingRate,
                        (int)(Constants.TONE_CALIB_LENGTH_IN_SECONDS * Constants.samplingRate),
                        vol3a,
                        0);
                pulse2 = SignalGenerator.sine2speaker(f1, f2,
                        Constants.samplingRate,
                        (int)(Constants.TONE_CALIB_LENGTH_IN_SECONDS * Constants.samplingRate),
                        0,
                        vol3b);
                for (int j = 0; j < pulse1.length; j++) {
                    output_signal[counter++]=pulse1[j];
                }
                for (int j = 0; j < pulse2.length; j++) {
                    output_signal[counter++]=pulse2[j];
                }
            }

            AudioStreamer sp = new AudioStreamer(context, output_signal,
                    output_signal.length, Constants.samplingRate,
                    AudioManager.STREAM_SYSTEM,Constants.volDefault,false);
            gsp=sp;

            int micType;
            if (Constants.AGC) {
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) != null) {
                    micType = (MediaRecorder.AudioSource.UNPROCESSED);
                } else {
                    micType = (MediaRecorder.AudioSource.VOICE_RECOGNITION);
                }
            }
            else {
                micType = MediaRecorder.AudioSource.DEFAULT;
            }

//            // TODO: make sure the recording and speaker overlap in time for sufficiently long
            OfflineRecorder orec;

            orec = new OfflineRecorder(micType, 0, 0, 0, barChart, lineChart, context,
                    (int) (output_signal.length/2+(Constants.samplingRate*Constants.PAD_CALIB_LENGTH_IN_SECONDS)),
                    false, true);
            orec.sp=sp;

            rec = orec;

            try {
                orec.start();
//                Thread.sleep(100);
                sp.play(1);
                while (orec.recording || orec.rec.getState() == AudioRecord.RECORDSTATE_STOPPED) {
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                Log.e("ex","sendtone");
                Log.e("ex",e.getMessage());
            }
            Log.e("asdf","done with vol calib");
        }

        public void checkfit() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Constants.dialog = loadingDialog();
                    Constants.dialog.show();
                }
            });

            AudioStreamer sp;
            if (Constants.calibSig.equals("chirp")) {
                short[] chirpCopy = new short[Constants.chirp.length];
                for (int i = 0; i < chirpCopy.length; i++) {
                    chirpCopy[i] = (short) (Constants.chirp[i] * 32000);
                }
//                sp.prime(chirpCopy, .4);
                sp = new AudioStreamer(context, chirpCopy, (Constants.samplingRate*Constants.CONSTANT_TONE_LENGTH_IN_SECONDS) * 2,
                        Constants.samplingRate, AudioManager.STREAM_SYSTEM,.4,true);
            }
            else {
                int checkfreq=226;
                short[] pulse = SignalGenerator.sine2speaker(checkfreq, checkfreq,
                        Constants.samplingRate,
                        Constants.CONSTANT_TONE_LENGTH_IN_SECONDS*Constants.samplingRate,
                        .8,
                        .8);
//                sp.prime(pulse, .4);
                if (Constants.phone.equals("sch")) {
                    sp = new AudioStreamer(context, pulse, (Constants.samplingRate * Constants.CONSTANT_TONE_LENGTH_IN_SECONDS) * 2,
                            Constants.samplingRate, AudioManager.STREAM_SYSTEM, .6, true);
                }
                else if (Constants.phone.equals("sch2")) {
                    sp = new AudioStreamer(context, pulse, (Constants.samplingRate * Constants.CONSTANT_TONE_LENGTH_IN_SECONDS) * 2,
                            Constants.samplingRate, AudioManager.STREAM_SYSTEM, .4, true);
                }
                else {
                    sp=null;
                }
            }
            gsp=sp;

            int micType;
            if (Constants.AGC) {
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) != null) {
                    micType = (MediaRecorder.AudioSource.UNPROCESSED);
                } else {
                    micType = (MediaRecorder.AudioSource.VOICE_RECOGNITION);
                }
            }
            else {
                micType = MediaRecorder.AudioSource.DEFAULT;
            }

            // TODO: make sure the recording and speaker overlap in time for sufficiently long
            OfflineRecorder orec;

            orec = new OfflineRecorder(micType, 0, 0, 0, barChart, lineChart, context,
                    (int) (30 * Constants.samplingRate), true, false);
            orec.sp=sp;

            while (view2==null) {

            }
            Chip passChip = view2.findViewById(R.id.rightChip);
            while (passChip==null) {
                passChip = view2.findViewById(R.id.rightChip);
            }
            orec.passChip=passChip;

            Chip failChip = view2.findViewById(R.id.leftChip);
            failChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    measure.cancel(true);
                    gsp.stopit();
                    try {
                        gsp.join();
                    } catch (InterruptedException e) {
                        Log.e("ex",e.getMessage());
                    }
                    rec.stopit();
                    try {
                        rec.join();
                    } catch (InterruptedException e) {
                        Log.e("ex",e.getMessage());
                    }
                    enableBottomBar(true);
                    measureButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    FileOperations.writeRecToDisk(getActivity(), fnameView);
                    Constants.dialog.dismiss();
                }
            });

            ProgressBar pb = view2.findViewById(R.id.progressBar2);
            orec.pb=pb;
            TextView tv=view2.findViewById(R.id.textView);
            orec.tv=tv;

            rec = orec;

            try {
                orec.start();
                sp.play(-1);
                while (orec.recording || orec.rec.getState() == AudioRecord.RECORDSTATE_STOPPED) {
                    Thread.sleep(10);
                }
//                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e("ex","sendtone");
                Log.e("ex",e.getMessage());
            }
            Log.e("asdf","done with checkfit");
        }

        public void sendTone(int freq, int fidx, int tidx, boolean ss) {
            int f1 = Constants.freqLookup.get(freq);
            int f2 = freq;

            short[] pulse;
            Log.e("asdf","get "+f1);
            Log.e("asdf","get "+f2);
            float vol3a = Constants.vol3Lookup.get(f1);
            float vol3b = Constants.vol3Lookup.get(f2);

            pulse = SignalGenerator.sine2speaker(f1, f2,
                    Constants.samplingRate,
                    Constants.CONSTANT_TONE_LENGTH_IN_SECONDS*Constants.samplingRate,
                    vol3a,
                    vol3b);

            Log.e("out","speaker "+f1+","+f2);

            float vol1 = Constants.vol1Lookup.get(f2);

            AudioStreamer sp = new AudioStreamer(context, pulse, Constants.samplingRate*Constants.CONSTANT_TONE_LENGTH_IN_SECONDS*2,
                    Constants.samplingRate, AudioManager.STREAM_SYSTEM,vol1,false);
            gsp=sp;
//            sp.prime(pulse, vol1);
//            Log.e("vol","vol1 , "+f2+","+vol1);

            int micType;
            if (Constants.AGC) {
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) != null) {
                    micType = (MediaRecorder.AudioSource.UNPROCESSED);
                } else {
                    micType = (MediaRecorder.AudioSource.VOICE_RECOGNITION);
                }
            }
            else {
                micType = MediaRecorder.AudioSource.DEFAULT;
            }

            // TODO: make sure the recording and speaker overlap in time for sufficiently long
            OfflineRecorder orec;
            orec = new OfflineRecorder(micType, fidx, tidx, freq, barChart, lineChart, context,
                    (int) (Constants.CONSTANT_TONE_LENGTH_IN_SECONDS * Constants.samplingRate), false, false);
            orec.ss=ss;
            rec = orec;

            try {
                orec.start();

                sp.play(-1);
                Log.e("debug",Constants.CONSTANT_TONE_LENGTH_IN_SECONDS+"");
                Thread.sleep((long) (Constants.CONSTANT_TONE_LENGTH_IN_SECONDS*1000));

                Log.e("asdf","stop it");
                sp.stopit();

                while (orec.recording||
                        orec.rec.getState()!=AudioRecord.RECORDSTATE_STOPPED||
                        sp.track1.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                    Thread.sleep(100);
                }

                Log.e("asdf","STOP");

            } catch (Exception e) {
                Log.e("ex","sendtone");
                Log.e("ex",e.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.MeasureFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.CurrentFragment = this;
        Constants.MeasureFragment = this;

        Log.e("asdf","MEASURE FREQS");
        for (int i = 0; i < Constants.freqs.length; i++) {
            Log.e("asdf",i+":"+Constants.freqs[i]);
        }
    }

//    public static native double[] fftnative_short(short[] data, int N);
    public static native double[] fftnative(double[] data, int N);
}
