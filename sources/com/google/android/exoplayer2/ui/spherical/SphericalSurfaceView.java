package com.google.android.exoplayer2.ui.spherical;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@TargetApi(15)
public final class SphericalSurfaceView extends GLSurfaceView {
    private static final int FIELD_OF_VIEW_DEGREES = 90;
    private static final float PX_PER_DEGREES = 25.0f;
    static final float UPRIGHT_ROLL = 3.1415927f;
    private static final float Z_FAR = 100.0f;
    private static final float Z_NEAR = 0.1f;
    private final Handler mainHandler;
    @Nullable
    private final Sensor orientationSensor;
    private final PhoneOrientationListener phoneOrientationListener;
    private final Renderer renderer;
    private final SceneRenderer scene;
    private final SensorManager sensorManager;
    @Nullable
    private Surface surface;
    @Nullable
    private SurfaceListener surfaceListener;
    @Nullable
    private SurfaceTexture surfaceTexture;
    private final TouchTracker touchTracker;
    @Nullable
    private VideoComponent videoComponent;

    private static class PhoneOrientationListener implements SensorEventListener {
        private final float[] angles = new float[3];
        private final Display display;
        private final float[] phoneInWorldSpaceMatrix = new float[16];
        private final float[] remappedPhoneMatrix = new float[16];
        private final Renderer renderer;
        private final TouchTracker touchTracker;

        public PhoneOrientationListener(Display display, TouchTracker touchTracker, Renderer renderer) {
            this.display = display;
            this.touchTracker = touchTracker;
            this.renderer = renderer;
        }

        @BinderThread
        public void onSensorChanged(SensorEvent event) {
            int xAxis;
            int yAxis;
            SensorManager.getRotationMatrixFromVector(this.remappedPhoneMatrix, event.values);
            switch (this.display.getRotation()) {
                case 1:
                    xAxis = 2;
                    yAxis = TsExtractor.TS_STREAM_TYPE_AC3;
                    break;
                case 2:
                    xAxis = TsExtractor.TS_STREAM_TYPE_AC3;
                    yAxis = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
                    break;
                case 3:
                    xAxis = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
                    yAxis = 1;
                    break;
                default:
                    xAxis = 1;
                    yAxis = 2;
                    break;
            }
            SensorManager.remapCoordinateSystem(this.remappedPhoneMatrix, xAxis, yAxis, this.phoneInWorldSpaceMatrix);
            SensorManager.remapCoordinateSystem(this.phoneInWorldSpaceMatrix, 1, 131, this.remappedPhoneMatrix);
            SensorManager.getOrientation(this.remappedPhoneMatrix, this.angles);
            float roll = this.angles[2];
            this.touchTracker.setRoll(roll);
            Matrix.rotateM(this.phoneInWorldSpaceMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
            this.renderer.setDeviceOrientation(this.phoneInWorldSpaceMatrix, roll);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public interface SurfaceListener {
        void surfaceChanged(@Nullable Surface surface);
    }

    class Renderer implements android.opengl.GLSurfaceView.Renderer, Listener {
        private final float[] deviceOrientationMatrix = new float[16];
        private float deviceRoll;
        private final float[] projectionMatrix = new float[16];
        private final SceneRenderer scene;
        private final float[] tempMatrix = new float[16];
        private float touchPitch;
        private final float[] touchPitchMatrix = new float[16];
        private final float[] touchYawMatrix = new float[16];
        private final float[] viewMatrix = new float[16];
        private final float[] viewProjectionMatrix = new float[16];

        public Renderer(SceneRenderer scene) {
            this.scene = scene;
            Matrix.setIdentityM(this.deviceOrientationMatrix, 0);
            Matrix.setIdentityM(this.touchPitchMatrix, 0);
            Matrix.setIdentityM(this.touchYawMatrix, 0);
            this.deviceRoll = SphericalSurfaceView.UPRIGHT_ROLL;
        }

        public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {
            SphericalSurfaceView.this.onSurfaceTextureAvailable(this.scene.init());
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float aspect = ((float) width) / ((float) height);
            Matrix.perspectiveM(this.projectionMatrix, 0, calculateFieldOfViewInYDirection(aspect), aspect, 0.1f, SphericalSurfaceView.Z_FAR);
        }

        public void onDrawFrame(GL10 gl) {
            synchronized (this) {
                Matrix.multiplyMM(this.tempMatrix, 0, this.deviceOrientationMatrix, 0, this.touchYawMatrix, 0);
                Matrix.multiplyMM(this.viewMatrix, 0, this.touchPitchMatrix, 0, this.tempMatrix, 0);
            }
            Matrix.multiplyMM(this.viewProjectionMatrix, 0, this.projectionMatrix, 0, this.viewMatrix, 0);
            this.scene.drawFrame(this.viewProjectionMatrix, 0);
        }

        @BinderThread
        public synchronized void setDeviceOrientation(float[] matrix, float deviceRoll) {
            System.arraycopy(matrix, 0, this.deviceOrientationMatrix, 0, this.deviceOrientationMatrix.length);
            this.deviceRoll = -deviceRoll;
            updatePitchMatrix();
        }

        @AnyThread
        private void updatePitchMatrix() {
            Matrix.setRotateM(this.touchPitchMatrix, 0, -this.touchPitch, (float) Math.cos((double) this.deviceRoll), (float) Math.sin((double) this.deviceRoll), 0.0f);
        }

        @UiThread
        public synchronized void onScrollChange(PointF scrollOffsetDegrees) {
            this.touchPitch = scrollOffsetDegrees.y;
            updatePitchMatrix();
            Matrix.setRotateM(this.touchYawMatrix, 0, -scrollOffsetDegrees.x, 0.0f, 1.0f, 0.0f);
        }

        private float calculateFieldOfViewInYDirection(float aspect) {
            if (!(aspect > 1.0f)) {
                return 90.0f;
            }
            double tanY = Math.tan(Math.toRadians(45.0d));
            double d = (double) aspect;
            Double.isNaN(d);
            return (float) (2.0d * Math.toDegrees(Math.atan(tanY / d)));
        }
    }

    public SphericalSurfaceView(Context context) {
        this(context, null);
    }

    public SphericalSurfaceView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.sensorManager = (SensorManager) Assertions.checkNotNull(context.getSystemService("sensor"));
        Sensor orientationSensor = null;
        if (Util.SDK_INT >= 18) {
            orientationSensor = this.sensorManager.getDefaultSensor(15);
        }
        if (orientationSensor == null) {
            orientationSensor = this.sensorManager.getDefaultSensor(11);
        }
        this.orientationSensor = orientationSensor;
        this.scene = new SceneRenderer();
        this.renderer = new Renderer(this.scene);
        this.touchTracker = new TouchTracker(context, this.renderer, PX_PER_DEGREES);
        this.phoneOrientationListener = new PhoneOrientationListener(((WindowManager) Assertions.checkNotNull((WindowManager) context.getSystemService("window"))).getDefaultDisplay(), this.touchTracker, this.renderer);
        setEGLContextClientVersion(2);
        setRenderer(this.renderer);
        setOnTouchListener(this.touchTracker);
    }

    public void setDefaultStereoMode(int stereoMode) {
        this.scene.setDefaultStereoMode(stereoMode);
    }

    public void setVideoComponent(@Nullable VideoComponent newVideoComponent) {
        VideoComponent videoComponent = this.videoComponent;
        if (newVideoComponent != videoComponent) {
            if (videoComponent != null) {
                Surface surface = this.surface;
                if (surface != null) {
                    videoComponent.clearVideoSurface(surface);
                }
                this.videoComponent.clearVideoFrameMetadataListener(this.scene);
                this.videoComponent.clearCameraMotionListener(this.scene);
            }
            this.videoComponent = newVideoComponent;
            videoComponent = this.videoComponent;
            if (videoComponent != null) {
                videoComponent.setVideoFrameMetadataListener(this.scene);
                this.videoComponent.setCameraMotionListener(this.scene);
                this.videoComponent.setVideoSurface(this.surface);
            }
        }
    }

    public void setSurfaceListener(@Nullable SurfaceListener listener) {
        this.surfaceListener = listener;
    }

    public void setSingleTapListener(@Nullable SingleTapListener listener) {
        this.touchTracker.setSingleTapListener(listener);
    }

    public void onResume() {
        super.onResume();
        Sensor sensor = this.orientationSensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this.phoneOrientationListener, sensor, 0);
        }
    }

    public void onPause() {
        if (this.orientationSensor != null) {
            this.sensorManager.unregisterListener(this.phoneOrientationListener);
        }
        super.onPause();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mainHandler.post(new -$$Lambda$SphericalSurfaceView$IhaXIqfpp9iCqyi6i6bEIB2VCio());
    }

    public static /* synthetic */ void lambda$onDetachedFromWindow$0(SphericalSurfaceView sphericalSurfaceView) {
        if (sphericalSurfaceView.surface != null) {
            SurfaceListener surfaceListener = sphericalSurfaceView.surfaceListener;
            if (surfaceListener != null) {
                surfaceListener.surfaceChanged(null);
            }
            releaseSurface(sphericalSurfaceView.surfaceTexture, sphericalSurfaceView.surface);
            sphericalSurfaceView.surfaceTexture = null;
            sphericalSurfaceView.surface = null;
        }
    }

    private void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture) {
        this.mainHandler.post(new -$$Lambda$SphericalSurfaceView$6n4Tp0yadhyexFmfBUZ25TM8HJ4(this, surfaceTexture));
    }

    public static /* synthetic */ void lambda$onSurfaceTextureAvailable$1(SphericalSurfaceView sphericalSurfaceView, SurfaceTexture surfaceTexture) {
        SurfaceTexture oldSurfaceTexture = sphericalSurfaceView.surfaceTexture;
        Surface oldSurface = sphericalSurfaceView.surface;
        sphericalSurfaceView.surfaceTexture = surfaceTexture;
        sphericalSurfaceView.surface = new Surface(surfaceTexture);
        SurfaceListener surfaceListener = sphericalSurfaceView.surfaceListener;
        if (surfaceListener != null) {
            surfaceListener.surfaceChanged(sphericalSurfaceView.surface);
        }
        releaseSurface(oldSurfaceTexture, oldSurface);
    }

    private static void releaseSurface(@Nullable SurfaceTexture oldSurfaceTexture, @Nullable Surface oldSurface) {
        if (oldSurfaceTexture != null) {
            oldSurfaceTexture.release();
        }
        if (oldSurface != null) {
            oldSurface.release();
        }
    }
}
