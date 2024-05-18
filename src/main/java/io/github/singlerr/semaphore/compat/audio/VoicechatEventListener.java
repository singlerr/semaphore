/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat.audio;

import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import io.github.singlerr.semaphore.sound.AudioPlayer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VoicechatEventListener {

    private int sampleRate = AudioPlayer.SAMPLE_RATE;

    public void onReceiveStaticSound(ClientReceiveSoundEvent event) {}

    //    private short[] filter(short[] rawAudio){
    //        int fs = AudioPlayer.SAMPLE_RATE;
    //        //First we need to apply band stop filter, order = 5
    //        short[] lowPassResult = lowPassFilter(rawAudio, )
    //    }
    //
    //    private short[] bandStopFilter(short[] signal, int order, double lowCutoff, double highCutoff) throws
    // IllegalArgumentException{
    //        if (lowCutoff >= highCutoff) {
    //            throw new IllegalArgumentException("Lower Cutoff Frequency cannot be more than the Higher Cutoff
    // Frequency");
    //        }
    //        double centreFreq = (highCutoff + lowCutoff)/2.0;
    //        double width = Math.abs(highCutoff - lowCutoff);
    //        short[] output = new short[signal.length];
    //        uk.me.berndporr.iirj.Butterworth bs = new uk.me.berndporr.iirj.Butterworth();
    //        bs.bandStop(order, sampleRate, centreFreq, width);
    //        for (int i=0; i<output.length; i++) {
    //            output[i] = (short) bs.filter(signal[i]);
    //        }
    //        return output;
    //    }
    //    private short[] lowPassFilter(short[] signal, int order, double cutoffFreq) {
    //        short[] output = new short[signal.length];
    //        uk.me.berndporr.iirj.Butterworth lp = new uk.me.berndporr.iirj.Butterworth();
    //        lp.lowPass(order, sampleRate, cutoffFreq);
    //        for (int i =0; i<output.length; i++) {
    //            output[i] = (short) lp.filter(signal[i]);
    //        }
    //        return output;
    //    }

}
