package com.example.dpoae;

public class SignalGenerator {

    public static short[] sine2speaker(double f1, double f2,int Samplingfreq, int samplen, double v1, double v2) {
        short[] signal = new short[samplen*2];
        short[] signal1;
        short[] signal2;

        signal1 = SineWaveSpeaker(samplen,f1,Samplingfreq,v1);
        signal2 = SineWaveSpeaker(samplen,f2,Samplingfreq,v2);

        int counter = 0;
        for (int i = 0; i < signal.length; i+=2) {
            signal[i] = signal1[counter];
            signal[i+1] = signal2[counter];
            counter += 1;
        }

        return signal;
    }

    public static short[] genChirpStereo() {
        short[] c=generateChirpSpeaker(1800, 4400, .5, Constants.samplingRate, 0);
        short[] signal = new short[c.length*2];
        int counter = 0;
        for (int i = 0; i < signal.length; i+=2) {
            signal[i] = c[counter];
            signal[i+1] = c[counter];
            counter += 1;
        }

        return signal;
    }

    public static short[] genChirp() {
        return generateChirpSpeaker(1800, 4400, .5, Constants.samplingRate, 0);
    }

    public static short[] generateChirpSpeaker(double startFreq, double endFreq, double time, double fs, double initialPhase) {

        int N = (int) (time * fs);
        short[] ans = new short[N];
        double f = startFreq;
        double k = (endFreq - startFreq) / time;
        for (int i = 0; i < N; i++) {
            double t = (double) i / fs;
            double phase = initialPhase + 2*Math.PI*(startFreq * t + 0.5 * k * t * t);
            phase = AngularMath.Normalize(phase);
            ans[i] = (short) (Math.sin(phase) * 32000);
        }

        return ans;
    }

    public static short[] SineWaveSpeaker(int len, double freq, double samplingFreq, double vol) {
        short[] sin = new short[len];
        double initialPhase = AngularMath.Normalize(0);
        double dphase = 2 * Math.PI * (double)freq / samplingFreq;
        double phase = initialPhase;

        for (int i = 0; i < len; i++) {
            sin[i] = (short)(Math.sin(phase)*(32000*vol));
            phase += dphase;
            phase = AngularMath.Normalize(phase);
        }
        return sin;
    }
}
