package com.example.dpoae;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class FileOperations {

    public static short[] readrawasset_binary(Context context, int id) {
        InputStream inp = context.getResources().openRawResource(id);
        ArrayList<Integer> ll = new ArrayList<>();
        int counter=0;
        int byteRead=0;
        try {
            while ((byteRead = inp.read()) != -1) {
                ll.add(byteRead);
                counter += 1;
//                if (counter % 1000 == 0) {
//                    Log.e("asdf", counter + "");
//                }
            }
            inp.close();
        }
        catch(Exception e) {
            Log.e("asdf",e.getMessage());
        }
        short[] ar = new short[ll.size()/2];

        counter=0;
        for (int i = 0; i < ll.size(); i+=2) {
            int out=ll.get(i)+ll.get(i+1)*256;
            if (out > 32767) {
                out=out-65536;
            }
            ar[counter++]=(short)out;
        }

        return ar;
    }

    public static double[] readrawasset(Context context, int id) {

        Scanner inp = new Scanner(context.getResources().openRawResource(id));
        LinkedList<Double> ll = new LinkedList<Double>();
        while (inp.hasNextLine()) {
            ll.add(Double.parseDouble(inp.nextLine()));
        }
        inp.close();
        double[] ar = new double[ll.size()];
        int counter = 0;
        for (Double d : ll) {
            ar[counter++] = d;
        }
        ll.clear();

        return ar;
    }

    public static double[] readfromfile(Activity av, String filename) {
        LinkedList<Double> ll = new LinkedList<Double>();

        try {
            String dir = av.getExternalFilesDir(null).toString();
            File file = new File(dir + File.separator + filename);
            BufferedReader buf = new BufferedReader(new FileReader(file));

            String line;
            while ((line = buf.readLine()) != null && line.length() != 0) {
                ll.add(Double.parseDouble(line));
            }

            buf.close();
        } catch (Exception e) {
            Log.e("ble",e.getMessage());
        }

        double[] ar = new double[ll.size()];
        int counter = 0;
        for (Double d : ll) {
            ar[counter++] = d;
        }
        ll.clear();
        return ar;
    }

    public static short[] readfromfile_short(Activity av, String filename) {
        LinkedList<Short> ll = new LinkedList<>();

        try {
            String dir = av.getExternalFilesDir(null).toString();
            File file = new File(dir + File.separator + filename);
            BufferedReader buf = new BufferedReader(new FileReader(file));

            String line;
            while ((line = buf.readLine()) != null && line.length() != 0) {
                ll.add(Short.parseShort(line));
            }

            buf.close();
        } catch (Exception e) {
            Log.e("ble",e.getMessage());
        }

        short[] ar = new short[ll.size()];
        int counter = 0;
        for (Short d : ll) {
            ar[counter++] = d;
        }
        ll.clear();
        return ar;
    }

    public static void writeFileToDisk(Activity av,LinkedList<Integer>envs) {
        try {
            String dir = av.getExternalFilesDir(null).toString();
            File path = new File(dir);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(dir, Constants.filename+"-env.txt");
            BufferedWriter outfile = new BufferedWriter(new FileWriter(file,false));
            for (Integer i : envs) {
                outfile.append("" + i);
                outfile.newLine();
            }
            outfile.flush();
            outfile.close();
        } catch(Exception e) {
            Log.e("ex", "writeRecToDisk");
            Log.e("ex", e.getMessage());
        }
    }

    public static void writeFileToDisk(Activity av,short[]envs,boolean checkfit,boolean calib) {
        try {
            String dir = av.getExternalFilesDir(null).toString();
            File path = new File(dir);
            if (!path.exists()) {
                path.mkdirs();
            }
            String filename=Constants.filename;
            if (calib) {
                filename+="-volcalib.txt";
            }
            else if (checkfit) {
                filename+="-env2.txt";
            }
            Log.e("asdf","write file "+filename);

            File file = new File(dir, filename);
            BufferedWriter outfile = new BufferedWriter(new FileWriter(file,false));
            for (short i : envs) {
                outfile.append("" + i);
                outfile.newLine();
            }
            outfile.flush();
            outfile.close();
            Log.e("asdf","finish write file "+filename);
        } catch(Exception e) {
            Log.e("ex", "writeRecToDisk");
            Log.e("ex", e.getMessage());
        }
    }

    public static void writeRecToDisk(Activity av, TextView fnameView) {
        try {
            String dir = av.getExternalFilesDir(null).toString();
            File path = new File(dir);
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(dir, Constants.filename+".txt");

            BufferedWriter outfile = new BufferedWriter(new FileWriter(file,false));
            int cc = 1;
            for (short[] buff : Constants.fullrec) {
                Log.e("out","writing "+cc+" out of "+Constants.fullrec.size());
                for (int i = 0; i < buff.length; i++) {
                    outfile.append("" + buff[i]);
                    outfile.newLine();
                }
                cc+=1;
            }
            outfile.flush();
            outfile.close();
        } catch(Exception e) {
            Log.e("ex", "writeRecToDisk");
            Log.e("ex", e.getMessage());
        }
    }
}
