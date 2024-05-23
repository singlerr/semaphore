/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound.utils;

import de.maxhenkel.voicechat.voice.client.speaker.Speaker;
import de.maxhenkel.voicechat.voice.client.speaker.SpeakerManager;
import de.maxhenkel.voicechat.voice.common.NamedThreadPoolFactory;
import io.github.singlerr.semaphore.utils.ExtensionsKt;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import java.io.BufferedInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraft.util.ResourceLocation;

@Log4j2
@UtilityClass
public class AudioPlayer {

    public final int SAMPLE_RATE = 48000;
    private final int FRAME_SIZE = (SAMPLE_RATE / 1000) * 20;

    private final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor(new NamedThreadPoolFactory("AudioPlayerThread"));

    private final Map<ResourceLocation, short[]> caches = Collections.synchronizedMap(new WeakHashMap<>());

    @Getter
    private final AudioWrapper currentAudio = new AudioWrapper();

    private Speaker speaker;

    public void init() throws Exception {
        speaker = SpeakerManager.createSpeaker(null, UUID.randomUUID());
        speaker.open();
    }

    public void loadSound(ResourceLocationBuilder resourceLocationBuilder) throws Exception {
        ResourceLocation resourceLocation = resourceLocationBuilder.build();
        log.info("Loading audio sound {}", resourceLocation);
        short[] cache = AudioConverter.convert(
                new BufferedInputStream(ExtensionsKt.asInputStream(resourceLocation)), AudioConverter.AudioType.WAV);
        caches.put(resourceLocation, cache);
    }

    public static void stop() {
        synchronized (currentAudio) {
            currentAudio.setRunning(false);
            EXECUTOR.shutdownNow();
        }
    }

    public static void play(ResourceLocationBuilder builder, Supplier<Double> volumeSupplier) throws Exception {
        synchronized (currentAudio) {
            currentAudio.setRunning(true);
            currentAudio.setId(builder.build());
        }
        EXECUTOR.submit(() -> {
            try {
                ResourceLocation loc = builder.build();
                short[] data;
                if (!caches.containsKey(loc)) {
                    data = AudioConverter.convert(ExtensionsKt.asInputStream(loc), AudioConverter.AudioType.WAV);
                    caches.put(loc, data);
                } else {
                    data = caches.get(loc);
                }
                AudioSupplier supplier = new AudioSupplier(data);
                short[] frame;
                while ((frame = supplier.get()) != null) {
                    synchronized (volumeSupplier) {
                        speaker.play(frame, volumeSupplier.get().floatValue(), "none");
                    }
                    Thread.sleep(20L);
                }
            } catch (Exception e) {
                log.error(e);
            }

            synchronized (currentAudio) {
                currentAudio.setRunning(false);
            }
        });
    }

    private final class AudioSupplier implements Supplier<short[]> {

        private final short[] audioData;
        private final short[] frame;
        private int framePosition;

        public AudioSupplier(short[] audioData) {
            this.audioData = audioData;
            this.frame = new short[FRAME_SIZE];
        }

        @Override
        public short[] get() {
            if (framePosition >= audioData.length) {
                return null;
            }

            Arrays.fill(frame, (short) 0);
            System.arraycopy(
                    audioData, framePosition, frame, 0, Math.min(frame.length, audioData.length - framePosition));
            framePosition += frame.length;
            return frame;
        }
    }

    @Data
    public static class AudioWrapper {
        private boolean isRunning;

        private ResourceLocation id;
    }
}
