/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import uk.me.berndporr.iirj.Butterworth;

/***
 * Got from Walkie-Talkie mod
 * @author flaton1
 */
public final class RadioFilter {
    private static final int SAMPLE_RATE = 48000;

    private final Butterworth HIGHPASS = new Butterworth();
    private final Butterworth BANDPASS = new Butterworth();

    private static RadioFilter instance;

    public static RadioFilter getInstance() {
        if (instance == null) return ((instance = new RadioFilter()));
        return instance;
    }

    private RadioFilter() {
        HIGHPASS.highPass(4, SAMPLE_RATE, 2000);
        BANDPASS.bandPass(4, SAMPLE_RATE, 50, 2600);
    }

    public short[] apply(short[] rawData) {
        short[] audioData = rawData;
        double[] doubleData = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            doubleData[i] = audioData[i];

            doubleData[i] = HIGHPASS.filter(doubleData[i]);
            // doubleData[i] = BANDPASS.filter(doubleData[i]);
            doubleData[i] = volume(doubleData[i], 10d);
        }

        for (int i = 0; i < doubleData.length; i++) {
            audioData[i] = (short) doubleData[i];
        }
        return audioData;
    }

    private double volume(double audio, double level) {
        return audio * level;
    }
}
