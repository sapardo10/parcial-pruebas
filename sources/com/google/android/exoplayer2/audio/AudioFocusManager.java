package com.google.android.exoplayer2.audio;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioFocusRequest.Builder;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

public final class AudioFocusManager {
    private static final int AUDIO_FOCUS_STATE_HAVE_FOCUS = 1;
    private static final int AUDIO_FOCUS_STATE_LOSS_TRANSIENT = 2;
    private static final int AUDIO_FOCUS_STATE_LOSS_TRANSIENT_DUCK = 3;
    private static final int AUDIO_FOCUS_STATE_LOST_FOCUS = -1;
    private static final int AUDIO_FOCUS_STATE_NO_FOCUS = 0;
    public static final int PLAYER_COMMAND_DO_NOT_PLAY = -1;
    public static final int PLAYER_COMMAND_PLAY_WHEN_READY = 1;
    public static final int PLAYER_COMMAND_WAIT_FOR_CALLBACK = 0;
    private static final String TAG = "AudioFocusManager";
    private static final float VOLUME_MULTIPLIER_DEFAULT = 1.0f;
    private static final float VOLUME_MULTIPLIER_DUCK = 0.2f;
    @Nullable
    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;
    private int audioFocusState;
    @Nullable
    private final AudioManager audioManager;
    private int focusGain;
    private final AudioFocusListener focusListener;
    private final PlayerControl playerControl;
    private boolean rebuildAudioFocusRequest;
    private float volumeMultiplier = 1.0f;

    private class AudioFocusListener implements OnAudioFocusChangeListener {
        private AudioFocusListener() {
        }

        public void onAudioFocusChange(int focusChange) {
            if (focusChange != 1) {
                switch (focusChange) {
                    case -3:
                        if (!AudioFocusManager.this.willPauseWhenDucked()) {
                            AudioFocusManager.this.audioFocusState = 3;
                            break;
                        } else {
                            AudioFocusManager.this.audioFocusState = 2;
                            break;
                        }
                    case -2:
                        AudioFocusManager.this.audioFocusState = 2;
                        break;
                    case -1:
                        AudioFocusManager.this.audioFocusState = -1;
                        break;
                    default:
                        String str = AudioFocusManager.TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unknown focus change type: ");
                        stringBuilder.append(focusChange);
                        Log.m10w(str, stringBuilder.toString());
                        return;
                }
            }
            AudioFocusManager.this.audioFocusState = 1;
            switch (AudioFocusManager.this.audioFocusState) {
                case -1:
                    AudioFocusManager.this.playerControl.executePlayerCommand(-1);
                    AudioFocusManager.this.abandonAudioFocus(true);
                    break;
                case 0:
                    break;
                case 1:
                    AudioFocusManager.this.playerControl.executePlayerCommand(1);
                    break;
                case 2:
                    AudioFocusManager.this.playerControl.executePlayerCommand(0);
                    break;
                case 3:
                    break;
                default:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Unknown audio focus state: ");
                    stringBuilder.append(AudioFocusManager.this.audioFocusState);
                    throw new IllegalStateException(stringBuilder.toString());
            }
            float volumeMultiplier = AudioFocusManager.this.audioFocusState == 3 ? AudioFocusManager.VOLUME_MULTIPLIER_DUCK : 1.0f;
            if (AudioFocusManager.this.volumeMultiplier != volumeMultiplier) {
                AudioFocusManager.this.volumeMultiplier = volumeMultiplier;
                AudioFocusManager.this.playerControl.setVolumeMultiplier(volumeMultiplier);
            }
        }
    }

    public interface PlayerControl {
        void executePlayerCommand(int i);

        void setVolumeMultiplier(float f);
    }

    public AudioFocusManager(@Nullable Context context, PlayerControl playerControl) {
        AudioManager audioManager;
        if (context == null) {
            audioManager = null;
        } else {
            audioManager = (AudioManager) context.getApplicationContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        }
        this.audioManager = audioManager;
        this.playerControl = playerControl;
        this.focusListener = new AudioFocusListener();
        this.audioFocusState = 0;
    }

    public float getVolumeMultiplier() {
        return this.volumeMultiplier;
    }

    public int setAudioAttributes(@Nullable AudioAttributes audioAttributes, boolean playWhenReady, int playerState) {
        int i = 1;
        if (this.audioAttributes == null && audioAttributes == null) {
            if (!playWhenReady) {
                i = -1;
            }
            return i;
        }
        int i2;
        Assertions.checkNotNull(this.audioManager, "SimpleExoPlayer must be created with a context to handle audio focus.");
        if (!Util.areEqual(this.audioAttributes, audioAttributes)) {
            boolean z;
            this.audioAttributes = audioAttributes;
            this.focusGain = convertAudioAttributesToFocusGain(audioAttributes);
            i2 = this.focusGain;
            if (i2 != 1) {
                if (i2 != 0) {
                    z = false;
                    Assertions.checkArgument(z, "Automatic handling of audio focus is only available for USAGE_MEDIA and USAGE_GAME.");
                    if (!playWhenReady && (playerState == 2 || playerState == 3)) {
                        return requestAudioFocus();
                    }
                }
            }
            z = true;
            Assertions.checkArgument(z, "Automatic handling of audio focus is only available for USAGE_MEDIA and USAGE_GAME.");
            if (!playWhenReady) {
            }
        }
        if (playerState == 1) {
            i2 = handleIdle(playWhenReady);
        } else {
            i2 = handlePrepare(playWhenReady);
        }
        return i2;
    }

    public int handlePrepare(boolean playWhenReady) {
        if (this.audioManager == null) {
            return 1;
        }
        return playWhenReady ? requestAudioFocus() : -1;
    }

    public int handleSetPlayWhenReady(boolean playWhenReady, int playerState) {
        if (this.audioManager == null) {
            return 1;
        }
        if (playWhenReady) {
            return playerState == 1 ? handleIdle(playWhenReady) : requestAudioFocus();
        }
        abandonAudioFocus();
        return -1;
    }

    public void handleStop() {
        if (this.audioManager != null) {
            abandonAudioFocus(true);
        }
    }

    private int handleIdle(boolean playWhenReady) {
        return playWhenReady ? 1 : -1;
    }

    private int requestAudioFocus() {
        int i = 1;
        if (this.focusGain == 0) {
            if (this.audioFocusState != 0) {
                abandonAudioFocus(true);
            }
            return 1;
        }
        int focusRequestResult;
        if (this.audioFocusState == 0) {
            if (Util.SDK_INT >= 26) {
                focusRequestResult = requestAudioFocusV26();
            } else {
                focusRequestResult = requestAudioFocusDefault();
            }
            this.audioFocusState = focusRequestResult == 1 ? 1 : 0;
        }
        focusRequestResult = this.audioFocusState;
        if (focusRequestResult == 0) {
            return -1;
        }
        if (focusRequestResult == 2) {
            i = 0;
        }
        return i;
    }

    private void abandonAudioFocus() {
        abandonAudioFocus(false);
    }

    private void abandonAudioFocus(boolean forceAbandon) {
        if (this.focusGain != 0 || this.audioFocusState != 0) {
            if (this.focusGain == 1 && this.audioFocusState != -1) {
                if (!forceAbandon) {
                }
            }
            if (Util.SDK_INT >= 26) {
                abandonAudioFocusV26();
            } else {
                abandonAudioFocusDefault();
            }
            this.audioFocusState = 0;
        }
    }

    private int requestAudioFocusDefault() {
        return ((AudioManager) Assertions.checkNotNull(this.audioManager)).requestAudioFocus(this.focusListener, Util.getStreamTypeForAudioUsage(((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).usage), this.focusGain);
    }

    @RequiresApi(26)
    private int requestAudioFocusV26() {
        if (this.audioFocusRequest != null) {
            if (!this.rebuildAudioFocusRequest) {
                return ((AudioManager) Assertions.checkNotNull(this.audioManager)).requestAudioFocus(this.audioFocusRequest);
            }
        }
        AudioFocusRequest audioFocusRequest = this.audioFocusRequest;
        this.audioFocusRequest = (audioFocusRequest == null ? new Builder(this.focusGain) : new Builder(audioFocusRequest)).setAudioAttributes(((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).getAudioAttributesV21()).setWillPauseWhenDucked(willPauseWhenDucked()).setOnAudioFocusChangeListener(this.focusListener).build();
        this.rebuildAudioFocusRequest = false;
        return ((AudioManager) Assertions.checkNotNull(this.audioManager)).requestAudioFocus(this.audioFocusRequest);
    }

    private void abandonAudioFocusDefault() {
        ((AudioManager) Assertions.checkNotNull(this.audioManager)).abandonAudioFocus(this.focusListener);
    }

    @RequiresApi(26)
    private void abandonAudioFocusV26() {
        if (this.audioFocusRequest != null) {
            ((AudioManager) Assertions.checkNotNull(this.audioManager)).abandonAudioFocusRequest(this.audioFocusRequest);
        }
    }

    private boolean willPauseWhenDucked() {
        AudioAttributes audioAttributes = this.audioAttributes;
        return audioAttributes != null && audioAttributes.contentType == 1;
    }

    private static int convertAudioAttributesToFocusGain(@Nullable AudioAttributes audioAttributes) {
        if (audioAttributes == null) {
            return 0;
        }
        switch (audioAttributes.usage) {
            case 0:
                Log.m10w(TAG, "Specify a proper usage in the audio attributes for audio focus handling. Using AUDIOFOCUS_GAIN by default.");
                return 1;
            case 1:
            case 14:
                return 1;
            case 2:
            case 4:
                return 2;
            case 3:
                return 0;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
                return 3;
            case 11:
                return audioAttributes.contentType == 1 ? 2 : 3;
            case 16:
                if (Util.SDK_INT >= 19) {
                    return 4;
                }
                return 2;
            default:
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unidentified audio usage: ");
                stringBuilder.append(audioAttributes.usage);
                Log.m10w(str, stringBuilder.toString());
                return 0;
        }
    }
}
