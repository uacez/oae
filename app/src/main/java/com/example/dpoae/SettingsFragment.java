package com.example.dpoae;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        initView(view);
        return view;
    }

    public void checkAction(int c, boolean isChecked) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        Log.e("asdf","checkaction "+c+","+isChecked);
        if (c==1) {
            Log.e("asdf","set check0 to "+isChecked);
            editor.putBoolean("check0", isChecked);
            Constants.freqs[0] = isChecked;
        }
        else if (c==2) {
            editor.putBoolean("check1", isChecked);
            Constants.freqs[1] = isChecked;
        }
        else if (c==3) {
            editor.putBoolean("check2", isChecked);
            Constants.freqs[2] = isChecked;
        }
        else if (c==4) {
            editor.putBoolean("check3", isChecked);
            Constants.freqs[3] = isChecked;
        }
        else if (c==5) {
            editor.putBoolean("check4", isChecked);
            Constants.freqs[4] = isChecked;
        }
        else if (c==6) {
            editor.putBoolean("check5", isChecked);
            Constants.freqs[5] = isChecked;
        }
        else if (c==7) {
            editor.putBoolean("check6", isChecked);
            Constants.freqs[6] = isChecked;
        }
        else if (c==8) {
            editor.putBoolean("check7", isChecked);
            Constants.freqs[7] = isChecked;
        }
        for (int i = 0; i < Constants.freqs.length; i++) {
            Log.e("asdf",i+":"+Constants.freqs[i]);
        }
        editor.commit();
    }

    public void initView(View view) {
        final TextInputEditText numRounds = view.findViewById(R.id.numRounds);
        final TextInputEditText constantToneLength = view.findViewById(R.id.constantToneLength);
        final TextInputEditText checkFitThresh = view.findViewById(R.id.checkFitThresh);
        final Switch earlyStoppingSwitch = view.findViewById(R.id.earlyStoppingSwitch);
        final Switch calSwitch = view.findViewById(R.id.calSwitch);
        final Switch interleavedSwitch = view.findViewById(R.id.interleavedSwitch);
        final Switch splSwitch = view.findViewById(R.id.splSwitch);
        final Switch checkFitSwitch = view.findViewById(R.id.checkFitSwitch);
        final Switch measureLoaderSwitch = view.findViewById(R.id.switch1);
        final Switch volCalibSwitch = view.findViewById(R.id.volCalibSwitch);
        final Switch resultSwitch = view.findViewById(R.id.switch2);
        final CheckBox c1 = view.findViewById(R.id.check1);
        final CheckBox c2 = view.findViewById(R.id.check2);
        final CheckBox c3 = view.findViewById(R.id.check3);
        final CheckBox c4 = view.findViewById(R.id.check4);
        final CheckBox c5 = view.findViewById(R.id.check5);
        final CheckBox c6 = view.findViewById(R.id.check6);
        final CheckBox c7 = view.findViewById(R.id.check7);
        final CheckBox c8 = view.findViewById(R.id.check8);
        final Spinner spinner = view.findViewById(R.id.spinner);
        final Button b0 = view.findViewById(R.id.button3);
        final Button b1 = view.findViewById(R.id.button4);
        final Button b2 = view.findViewById(R.id.button5);
        final TextView tv8 = view.findViewById(R.id.textView8);
        tv8.setText(Constants.phone);

        b0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAction(1,true);
                checkAction(2,true);
                checkAction(3,true);
                checkAction(4,true);

                checkAction(5,false);
                checkAction(6,false);
                checkAction(7,false);
                checkAction(8,false);
                c1.setChecked(false);
                c2.setChecked(false);
                c3.setChecked(true);
                c4.setChecked(true);
                c5.setChecked(true);
                c6.setChecked(true);
                c7.setChecked(false);
                c8.setChecked(false);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                int ss=3;
                editor.putInt("constantToneLength", ss);
                editor.commit();
                Constants.CONSTANT_TONE_LENGTH_IN_SECONDS=ss;
                constantToneLength.setText(Constants.CONSTANT_TONE_LENGTH_IN_SECONDS+"");
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAction(1,true);
                checkAction(2,true);
                checkAction(3,true);
                checkAction(4,true);

                checkAction(5,false);
                checkAction(6,false);
                checkAction(7,false);
                checkAction(8,false);
                c1.setChecked(false);
                c2.setChecked(false);
                c3.setChecked(true);
                c4.setChecked(true);
                c5.setChecked(true);
                c6.setChecked(true);
                c7.setChecked(false);
                c8.setChecked(false);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                int ss=10;
                editor.putInt("constantToneLength", ss);
                editor.commit();
                Constants.CONSTANT_TONE_LENGTH_IN_SECONDS=ss;
                constantToneLength.setText(Constants.CONSTANT_TONE_LENGTH_IN_SECONDS+"");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAction(3,true);
                checkAction(4,true);
                checkAction(5,true);
                checkAction(6,true);

                checkAction(1,true);
                checkAction(2,true);
                checkAction(7,true);
                checkAction(8,true);

                c1.setChecked(true);
                c2.setChecked(true);
                c3.setChecked(true);
                c4.setChecked(true);
                c5.setChecked(true);
                c6.setChecked(true);
                c7.setChecked(true);
                c8.setChecked(true);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                int ss=6;
                editor.putInt("constantToneLength", ss);
                editor.commit();
                Constants.CONSTANT_TONE_LENGTH_IN_SECONDS=ss;
                constantToneLength.setText(Constants.CONSTANT_TONE_LENGTH_IN_SECONDS+"");
            }
        });
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("75");
        spinnerArray.add("70");
        spinnerArray.add("65/55");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Constants.volumeSetting = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        spinner.setSelection(Constants.volumeSetting);

        Log.e("asdf","SET CHECKED ");
        c1.setChecked(Constants.freqs[4]);
        c2.setChecked(Constants.freqs[5]);
        c3.setChecked(Constants.freqs[0]);
        c4.setChecked(Constants.freqs[1]);
        c5.setChecked(Constants.freqs[2]);
        c6.setChecked(Constants.freqs[3]);
        c7.setChecked(Constants.freqs[6]);
        c8.setChecked(Constants.freqs[7]);
        numRounds.setText(Constants.MAX_TRIES+"");
        constantToneLength.setText(Constants.CONSTANT_TONE_LENGTH_IN_SECONDS+"");
        checkFitThresh.setText(Constants.SEAL_CHECK_THRESH+"");
        earlyStoppingSwitch.setChecked(Constants.EARLY_STOPPING);
        calSwitch.setChecked(Constants.CALIBRATE);
        interleavedSwitch.setChecked(Constants.INTERLEAVED);
        splSwitch.setChecked(Constants.SPL_CHECK);
        checkFitSwitch.setChecked(Constants.CHECK_FIT);
        volCalibSwitch.setChecked(Constants.VOL_CALIB);
        measureLoaderSwitch.setChecked(Constants.MEASURE_LOADER);
        resultSwitch.setChecked(Constants.SHOW_RESULT);

        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(5,isChecked);
            }
        });
        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(6,isChecked);
            }
        });
        c3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(1,isChecked);
            }
        });
        c4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(2,isChecked);
            }
        });
        c5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(3,isChecked);
            }
        });
        c6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(4,isChecked);
            }
        });
        c7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(7,isChecked);
            }
        });
        c8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAction(8,isChecked);
            }
        });

        numRounds.addTextChangedListener(new TextWatcher() {
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
                String ss = numRounds.getText().toString();
                if (ss.length()>0) {
                    editor.putInt("maxtries", Integer.parseInt(ss));
                    editor.commit();
                    Constants.MAX_TRIES=Integer.parseInt(ss);
                }
            }
        });

        constantToneLength.addTextChangedListener(new TextWatcher() {
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
                String ss = constantToneLength.getText().toString();
                if (ss.length()>0) {
//                    if (Integer.parseInt(ss) >= 2 && Integer.parseInt(ss) < 9) {
                        editor.putInt("constantToneLength", Integer.parseInt(ss));
                        editor.commit();
                        Constants.CONSTANT_TONE_LENGTH_IN_SECONDS = Integer.parseInt(ss);
//                    }
//                    else if (Integer.parseInt(ss) > 8) {
//                        constantToneLength.setText("8");
//                        editor.putInt("constantToneLength", 8);
//                        editor.commit();
//                        Constants.CONSTANT_TONE_LENGTH_IN_SECONDS = 8;
//                    }
//                    else if (Integer.parseInt(ss) < 2) {
//                        constantToneLength.setText("2");
//                        editor.putInt("constantToneLength", 2);
//                        editor.commit();
//                        Constants.CONSTANT_TONE_LENGTH_IN_SECONDS = 2;
//                    }
                }
            }
        });

        checkFitThresh.addTextChangedListener(new TextWatcher() {
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
                String ss = checkFitThresh.getText().toString();
                if (ss.length()>0) {
//                    if (Integer.parseInt(ss) <= 200 && Integer.parseInt(ss) >= 40) {
//                        editor.putInt("checkFitThresh", Integer.parseInt(ss));
//                        editor.commit();
//                        Constants.SEAL_CHECK_THRESH = Integer.parseInt(ss);
//                    }
//                    else if(Integer.parseInt(ss) > 200) {
//                        checkFitThresh.setText("200");
//                        editor.putInt("checkFitThresh", Integer.parseInt(ss));
//                        editor.commit();
//                        Constants.SEAL_CHECK_THRESH = Integer.parseInt(ss);
//                    }
//                    else if(Integer.parseInt(ss) < 40) {
//                        checkFitThresh.setText("40");
                        editor.putInt("checkFitThresh", Integer.parseInt(ss));
                        editor.commit();
                        Constants.SEAL_CHECK_THRESH = Integer.parseInt(ss);
//                    }
                }
            }
        });

        earlyStoppingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("earlystop", isChecked);
                editor.commit();
                Constants.EARLY_STOPPING = isChecked;
            }
        });
        resultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("showResult", isChecked);
                editor.commit();
                Constants.SHOW_RESULT = isChecked;
            }
        });
        calSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("calibrate", isChecked);
                editor.commit();
                Constants.CALIBRATE = isChecked;
            }
        });
        interleavedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("adaptive", isChecked);
                editor.commit();
                Constants.INTERLEAVED = isChecked;
                numRounds.setEnabled(!isChecked);
            }
        });
        splSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("spl", isChecked);
                editor.commit();
                Constants.SPL_CHECK = isChecked;
            }
        });
        checkFitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("checkFit", isChecked);
                editor.commit();
                Constants.CHECK_FIT  = isChecked;
            }
        });
        volCalibSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("volCalib", isChecked);
                editor.commit();
                Constants.VOL_CALIB  = isChecked;
            }
        });
        measureLoaderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("measureLoader", isChecked);
                editor.commit();
                Constants.MEASURE_LOADER  = isChecked;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.SettingsFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.CurrentFragment = this;
        Constants.SettingsFragment = this;
    }
}
