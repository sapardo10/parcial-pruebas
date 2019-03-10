package com.google.android.exoplayer2.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

public final class AudioCapabilitiesReceiver {
    @Nullable
    AudioCapabilities audioCapabilities;
    private final Context context;
    @Nullable
    private final Handler handler;
    private final Listener listener;
    @Nullable
    private final BroadcastReceiver receiver;

    private final class HdmiAudioPlugBroadcastReceiver extends BroadcastReceiver {
        private HdmiAudioPlugBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                AudioCapabilities newAudioCapabilities = AudioCapabilities.getCapabilities(intent);
                if (!newAudioCapabilities.equals(AudioCapabilitiesReceiver.this.audioCapabilities)) {
                    AudioCapabilitiesReceiver audioCapabilitiesReceiver = AudioCapabilitiesReceiver.this;
                    audioCapabilitiesReceiver.audioCapabilities = newAudioCapabilities;
                    audioCapabilitiesReceiver.listener.onAudioCapabilitiesChanged(newAudioCapabilities);
                }
            }
        }
    }

    public interface Listener {
        void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities);
    }

    public AudioCapabilitiesReceiver(Context context, Listener listener) {
        this(context, null, listener);
    }

    public AudioCapabilitiesReceiver(Context context, @Nullable Handler handler, Listener listener) {
        this.context = (Context) Assertions.checkNotNull(context);
        this.handler = handler;
        this.listener = (Listener) Assertions.checkNotNull(listener);
        this.receiver = Util.SDK_INT >= 21 ? new HdmiAudioPlugBroadcastReceiver() : null;
    }

    public AudioCapabilities register() {
        Intent stickyIntent = null;
        if (this.receiver != null) {
            IntentFilter intentFilter = new IntentFilter("android.media.action.HDMI_AUDIO_PLUG");
            Handler handler = this.handler;
            if (handler != null) {
                stickyIntent = this.context.registerReceiver(this.receiver, intentFilter, null, handler);
            } else {
                stickyIntent = this.context.registerReceiver(this.receiver, intentFilter);
            }
        }
        this.audioCapabilities = AudioCapabilities.getCapabilities(stickyIntent);
        return this.audioCapabilities;
    }

    public void unregister() {
        BroadcastReceiver broadcastReceiver = this.receiver;
        if (broadcastReceiver != null) {
            this.context.unregisterReceiver(broadcastReceiver);
        }
    }
}
