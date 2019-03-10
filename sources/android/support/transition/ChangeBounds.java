package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.Map;

public class ChangeBounds extends Transition {
    private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "bottomRight") {
        public void set(View view, PointF bottomRight) {
            ViewUtils.setLeftTopRightBottom(view, view.getLeft(), view.getTop(), Math.round(bottomRight.x), Math.round(bottomRight.y));
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "bottomRight") {
        public void set(ViewBounds viewBounds, PointF bottomRight) {
            viewBounds.setBottomRight(bottomRight);
        }

        public PointF get(ViewBounds viewBounds) {
            return null;
        }
    };
    private static final Property<Drawable, PointF> DRAWABLE_ORIGIN_PROPERTY = new Property<Drawable, PointF>(PointF.class, "boundsOrigin") {
        private Rect mBounds = new Rect();

        public void set(Drawable object, PointF value) {
            object.copyBounds(this.mBounds);
            this.mBounds.offsetTo(Math.round(value.x), Math.round(value.y));
            object.setBounds(this.mBounds);
        }

        public PointF get(Drawable object) {
            object.copyBounds(this.mBounds);
            return new PointF((float) this.mBounds.left, (float) this.mBounds.top);
        }
    };
    private static final Property<View, PointF> POSITION_PROPERTY = new Property<View, PointF>(PointF.class, PodDBAdapter.KEY_POSITION) {
        public void set(View view, PointF topLeft) {
            int left = Math.round(topLeft.x);
            int top = Math.round(topLeft.y);
            ViewUtils.setLeftTopRightBottom(view, left, top, view.getWidth() + left, view.getHeight() + top);
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_CLIP = "android:changeBounds:clip";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "topLeft") {
        public void set(View view, PointF topLeft) {
            ViewUtils.setLeftTopRightBottom(view, Math.round(topLeft.x), Math.round(topLeft.y), view.getRight(), view.getBottom());
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "topLeft") {
        public void set(ViewBounds viewBounds, PointF topLeft) {
            viewBounds.setTopLeft(topLeft);
        }

        public PointF get(ViewBounds viewBounds) {
            return null;
        }
    };
    private static RectEvaluator sRectEvaluator = new RectEvaluator();
    private static final String[] sTransitionProperties = new String[]{PROPNAME_BOUNDS, PROPNAME_CLIP, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
    private boolean mReparent;
    private boolean mResizeClip;
    private int[] mTempLocation;

    private static class ViewBounds {
        private int mBottom;
        private int mBottomRightCalls;
        private int mLeft;
        private int mRight;
        private int mTop;
        private int mTopLeftCalls;
        private View mView;

        ViewBounds(View view) {
            this.mView = view;
        }

        void setTopLeft(PointF topLeft) {
            this.mLeft = Math.round(topLeft.x);
            this.mTop = Math.round(topLeft.y);
            this.mTopLeftCalls++;
            if (this.mTopLeftCalls == this.mBottomRightCalls) {
                setLeftTopRightBottom();
            }
        }

        void setBottomRight(PointF bottomRight) {
            this.mRight = Math.round(bottomRight.x);
            this.mBottom = Math.round(bottomRight.y);
            this.mBottomRightCalls++;
            if (this.mTopLeftCalls == this.mBottomRightCalls) {
                setLeftTopRightBottom();
            }
        }

        private void setLeftTopRightBottom() {
            ViewUtils.setLeftTopRightBottom(this.mView, this.mLeft, this.mTop, this.mRight, this.mBottom);
            this.mTopLeftCalls = 0;
            this.mBottomRightCalls = 0;
        }
    }

    public ChangeBounds() {
        this.mTempLocation = new int[2];
        this.mResizeClip = false;
        this.mReparent = false;
    }

    public ChangeBounds(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTempLocation = new int[2];
        this.mResizeClip = false;
        this.mReparent = false;
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.CHANGE_BOUNDS);
        boolean resizeClip = TypedArrayUtils.getNamedBoolean(a, (XmlResourceParser) attrs, "resizeClip", 0, false);
        a.recycle();
        setResizeClip(resizeClip);
    }

    @Nullable
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean resizeClip) {
        this.mResizeClip = resizeClip;
    }

    public boolean getResizeClip() {
        return this.mResizeClip;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        if (!ViewCompat.isLaidOut(view) && view.getWidth() == 0) {
            if (view.getHeight() == 0) {
                return;
            }
        }
        values.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
        values.values.put(PROPNAME_PARENT, values.view.getParent());
        if (this.mReparent) {
            values.view.getLocationInWindow(this.mTempLocation);
            values.values.put(PROPNAME_WINDOW_X, Integer.valueOf(this.mTempLocation[0]));
            values.values.put(PROPNAME_WINDOW_Y, Integer.valueOf(this.mTempLocation[1]));
        }
        if (this.mResizeClip) {
            values.values.put(PROPNAME_CLIP, ViewCompat.getClipBounds(view));
        }
    }

    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private boolean parentMatches(View startParent, View endParent) {
        if (!this.mReparent) {
            return true;
        }
        boolean z = true;
        TransitionValues endValues = getMatchedTransitionValues(startParent, true);
        if (endValues == null) {
            if (startParent != endParent) {
                z = false;
            }
            return z;
        }
        if (endParent != endValues.view) {
            z = false;
        }
        return z;
    }

    @Nullable
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        Animator animator;
        ChangeBounds changeBounds = this;
        TransitionValues transitionValues = startValues;
        TransitionValues transitionValues2 = endValues;
        ViewGroup viewGroup;
        TransitionValues transitionValues3;
        if (transitionValues == null) {
            viewGroup = sceneRoot;
            transitionValues3 = transitionValues2;
            animator = null;
        } else if (transitionValues2 == null) {
            viewGroup = sceneRoot;
            transitionValues3 = transitionValues2;
            animator = null;
        } else {
            Map<String, Object> startParentVals = transitionValues.values;
            Map<String, Object> endParentVals = transitionValues2.values;
            ViewGroup startParent = (ViewGroup) startParentVals.get(PROPNAME_PARENT);
            ViewGroup endParent = (ViewGroup) endParentVals.get(PROPNAME_PARENT);
            Map<String, Object> map;
            Map<String, Object> map2;
            ViewGroup viewGroup2;
            ViewGroup viewGroup3;
            if (startParent == null) {
                viewGroup = sceneRoot;
                map = startParentVals;
                map2 = endParentVals;
                viewGroup2 = startParent;
                viewGroup3 = endParent;
                transitionValues3 = transitionValues2;
            } else if (endParent == null) {
                viewGroup = sceneRoot;
                map = startParentVals;
                map2 = endParentVals;
                viewGroup2 = startParent;
                viewGroup3 = endParent;
                transitionValues3 = transitionValues2;
            } else {
                View view = transitionValues2.view;
                int endTop;
                if (parentMatches(startParent, endParent)) {
                    Rect startClip;
                    Rect endClip;
                    int i;
                    int i2;
                    int endLeft;
                    int startTop;
                    int i3;
                    Rect endClip2;
                    ObjectAnimator clipAnimator;
                    C00888 endClip3;
                    final View view2;
                    Rect startClip2;
                    int maxWidth;
                    int endRight;
                    int i4;
                    int i5;
                    int i6;
                    Map<String, Object> map3;
                    ViewGroup viewGroup4;
                    ObjectAnimator positionAnimator;
                    int endHeight;
                    View view3;
                    int i7;
                    Path topLeftPath;
                    ObjectAnimator bottomRightAnimator;
                    AnimatorSet set;
                    AnimatorSet endParent2;
                    Path topLeftPath2;
                    ViewGroup viewGroup5;
                    final ViewGroup endLeft2;
                    Rect startBounds = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
                    Rect endBounds = (Rect) transitionValues2.values.get(PROPNAME_BOUNDS);
                    int startLeft = startBounds.left;
                    int endLeft3 = endBounds.left;
                    int startTop2 = startBounds.top;
                    endTop = endBounds.top;
                    int startRight = startBounds.right;
                    startParentVals = endBounds.right;
                    int startBottom = startBounds.bottom;
                    startParent = endBounds.bottom;
                    endParent = startRight - startLeft;
                    int startHeight = startBottom - startTop2;
                    int endWidth = startParentVals - endLeft3;
                    int endHeight2 = startParent - endTop;
                    View view4 = view;
                    Rect startClip3 = (Rect) transitionValues.values.get(PROPNAME_CLIP);
                    Rect endClip4 = (Rect) transitionValues2.values.get(PROPNAME_CLIP);
                    view = 0;
                    if (endParent != null) {
                        if (startHeight == 0) {
                        }
                        if (startLeft == endLeft3) {
                            if (startTop2 != endTop) {
                                if (startRight == startParentVals) {
                                    if (startBottom == startParent) {
                                        if (startClip3 != null) {
                                            if (startClip3.equals(endClip4)) {
                                            }
                                            view++;
                                            if (view > null) {
                                                startClip = startClip3;
                                                endClip = endClip4;
                                                if (changeBounds.mResizeClip) {
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    startClip3 = view4;
                                                    endClip4 = endParent;
                                                    endParent = Math.max(endClip4, endWidth);
                                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                                    if (startLeft == endLeft3) {
                                                        if (startTop2 != endTop) {
                                                            endLeft = endLeft3;
                                                            startBottom = 0;
                                                            startTop = startTop2;
                                                            i3 = startLeft;
                                                            startTop2 = endClip;
                                                            if (startClip != null) {
                                                                startRight = 0;
                                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                                            } else {
                                                                startRight = 0;
                                                                startLeft = startClip;
                                                            }
                                                            if (endClip != null) {
                                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                            } else {
                                                                endClip2 = endClip;
                                                            }
                                                            if (startLeft.equals(endClip2)) {
                                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                                startTop = endHeight2;
                                                                clipAnimator = null;
                                                                startTop = endLeft;
                                                                endLeft = endClip4;
                                                                endClip3 = endHeight2;
                                                                endClip = endClip2;
                                                                view2 = startClip3;
                                                                startClip2 = startLeft;
                                                                startLeft = startTop;
                                                                maxWidth = endParent;
                                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                                endWidth = endTop;
                                                                startHeight = startParentVals;
                                                                endRight = startParentVals;
                                                                startParentVals = true;
                                                                endTop = startParent;
                                                                endHeight2 = new AnimatorListenerAdapter() {
                                                                    private boolean mIsCanceled;

                                                                    public void onAnimationCancel(Animator animation) {
                                                                        this.mIsCanceled = true;
                                                                    }

                                                                    public void onAnimationEnd(Animator animation) {
                                                                        if (!this.mIsCanceled) {
                                                                            ViewCompat.setClipBounds(view2, startTop2);
                                                                            ViewUtils.setLeftTopRightBottom(view2, startLeft, endWidth, startHeight, endTop);
                                                                        }
                                                                    }
                                                                };
                                                                endParent.addListener(endClip3);
                                                            } else {
                                                                endClip = endClip2;
                                                                startClip2 = startLeft;
                                                                i4 = endWidth;
                                                                i5 = startHeight;
                                                                i6 = endTop;
                                                                map3 = startParentVals;
                                                                viewGroup4 = endParent;
                                                                startTop = endLeft;
                                                                startParentVals = true;
                                                                endLeft = endClip4;
                                                                endParent = null;
                                                            }
                                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                        }
                                                    }
                                                    positionAnimator = null;
                                                    startTop = startTop2;
                                                    endLeft = endLeft3;
                                                    startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = endClip;
                                                    } else {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    } else {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                } else {
                                                    startClip3 = view4;
                                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                                    if (view != 2) {
                                                        endHeight = endHeight2;
                                                        i = endWidth;
                                                        i2 = startHeight;
                                                        view3 = startClip3;
                                                        i3 = endParent;
                                                        if (startLeft == endLeft3) {
                                                            startClip3 = view3;
                                                        } else if (startTop2 != endTop) {
                                                            startClip3 = view3;
                                                        } else {
                                                            startClip3 = view3;
                                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                            startTop = endLeft3;
                                                            i6 = endTop;
                                                            map3 = startParentVals;
                                                            i7 = endHeight;
                                                            i5 = i2;
                                                            i4 = i;
                                                            endLeft = i3;
                                                            startParentVals = true;
                                                            endHeight = startRight;
                                                            i = startTop2;
                                                            i3 = startLeft;
                                                            i2 = startBottom;
                                                        }
                                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                        startTop = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        endLeft = i3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i3 = startLeft;
                                                        i2 = startBottom;
                                                    } else if (endParent == endWidth || startHeight != endHeight2) {
                                                        endHeight = endHeight2;
                                                        i = endWidth;
                                                        i2 = startHeight;
                                                        view4 = view;
                                                        endHeight2 = new ViewBounds(startClip3);
                                                        i3 = endParent;
                                                        topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                        startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                        view3 = startClip3;
                                                        bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                        set = new AnimatorSet();
                                                        set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                        endParent2 = set;
                                                        set.addListener(new AnimatorListenerAdapter() {
                                                            private ViewBounds mViewBounds = endHeight2;
                                                        });
                                                        topLeftPath2 = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        endHeight2 = endParent2;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        startClip3 = view3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i2 = startBottom;
                                                        view3 = i3;
                                                        i3 = startLeft;
                                                    } else {
                                                        int numChanges = view;
                                                        endHeight = endHeight2;
                                                        i2 = startHeight;
                                                        i = endWidth;
                                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                        startTop = endLeft3;
                                                        i3 = startLeft;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup5 = endParent;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i2 = startBottom;
                                                    }
                                                }
                                                if (!(startClip3.getParent() instanceof ViewGroup)) {
                                                    endLeft2 = (ViewGroup) startClip3.getParent();
                                                    ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                                    addListener(new TransitionListenerAdapter() {
                                                        boolean mCanceled = null;

                                                        public void onTransitionCancel(@NonNull Transition transition) {
                                                            ViewGroupUtils.suppressLayout(endLeft2, false);
                                                            this.mCanceled = true;
                                                        }

                                                        public void onTransitionEnd(@NonNull Transition transition) {
                                                            if (!this.mCanceled) {
                                                                ViewGroupUtils.suppressLayout(endLeft2, false);
                                                            }
                                                            transition.removeListener(this);
                                                        }

                                                        public void onTransitionPause(@NonNull Transition transition) {
                                                            ViewGroupUtils.suppressLayout(endLeft2, false);
                                                        }

                                                        public void onTransitionResume(@NonNull Transition transition) {
                                                            ViewGroupUtils.suppressLayout(endLeft2, true);
                                                        }
                                                    });
                                                }
                                                return endHeight2;
                                            }
                                            startTop = endLeft3;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            startClip = startClip3;
                                            endClip = endClip4;
                                            map3 = startParentVals;
                                            i2 = startBottom;
                                            viewGroup5 = endParent;
                                            startHeight = startValues;
                                            transitionValues3 = endValues;
                                        }
                                        if (startClip3 != null && endClip4 != null) {
                                            view++;
                                            if (view > null) {
                                                startClip = startClip3;
                                                endClip = endClip4;
                                                if (changeBounds.mResizeClip) {
                                                    startClip3 = view4;
                                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                                    if (view != 2) {
                                                        if (endParent == endWidth) {
                                                        }
                                                        endHeight = endHeight2;
                                                        i = endWidth;
                                                        i2 = startHeight;
                                                        view4 = view;
                                                        endHeight2 = new ViewBounds(startClip3);
                                                        i3 = endParent;
                                                        topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                        startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                        view3 = startClip3;
                                                        bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                        set = new AnimatorSet();
                                                        set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                        endParent2 = set;
                                                        set.addListener(/* anonymous class already generated */);
                                                        topLeftPath2 = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        endHeight2 = endParent2;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        startClip3 = view3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i2 = startBottom;
                                                        view3 = i3;
                                                        i3 = startLeft;
                                                    } else {
                                                        endHeight = endHeight2;
                                                        i = endWidth;
                                                        i2 = startHeight;
                                                        view3 = startClip3;
                                                        i3 = endParent;
                                                        if (startLeft == endLeft3) {
                                                            startClip3 = view3;
                                                        } else if (startTop2 != endTop) {
                                                            startClip3 = view3;
                                                        } else {
                                                            startClip3 = view3;
                                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                            startTop = endLeft3;
                                                            i6 = endTop;
                                                            map3 = startParentVals;
                                                            i7 = endHeight;
                                                            i5 = i2;
                                                            i4 = i;
                                                            endLeft = i3;
                                                            startParentVals = true;
                                                            endHeight = startRight;
                                                            i = startTop2;
                                                            i3 = startLeft;
                                                            i2 = startBottom;
                                                        }
                                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                        startTop = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        endLeft = i3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i3 = startLeft;
                                                        i2 = startBottom;
                                                    }
                                                } else {
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    startClip3 = view4;
                                                    endClip4 = endParent;
                                                    endParent = Math.max(endClip4, endWidth);
                                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                                    if (startLeft == endLeft3) {
                                                        if (startTop2 != endTop) {
                                                            endLeft = endLeft3;
                                                            startBottom = 0;
                                                            startTop = startTop2;
                                                            i3 = startLeft;
                                                            startTop2 = endClip;
                                                            if (startClip != null) {
                                                                startRight = 0;
                                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                                            } else {
                                                                startRight = 0;
                                                                startLeft = startClip;
                                                            }
                                                            if (endClip != null) {
                                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                            } else {
                                                                endClip2 = endClip;
                                                            }
                                                            if (startLeft.equals(endClip2)) {
                                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                                startTop = endHeight2;
                                                                clipAnimator = null;
                                                                startTop = endLeft;
                                                                endLeft = endClip4;
                                                                endClip3 = endHeight2;
                                                                endClip = endClip2;
                                                                view2 = startClip3;
                                                                startClip2 = startLeft;
                                                                startLeft = startTop;
                                                                maxWidth = endParent;
                                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                                endWidth = endTop;
                                                                startHeight = startParentVals;
                                                                endRight = startParentVals;
                                                                startParentVals = true;
                                                                endTop = startParent;
                                                                endHeight2 = /* anonymous class already generated */;
                                                                endParent.addListener(endClip3);
                                                            } else {
                                                                endClip = endClip2;
                                                                startClip2 = startLeft;
                                                                i4 = endWidth;
                                                                i5 = startHeight;
                                                                i6 = endTop;
                                                                map3 = startParentVals;
                                                                viewGroup4 = endParent;
                                                                startTop = endLeft;
                                                                startParentVals = true;
                                                                endLeft = endClip4;
                                                                endParent = null;
                                                            }
                                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                        }
                                                    }
                                                    positionAnimator = null;
                                                    startTop = startTop2;
                                                    endLeft = endLeft3;
                                                    startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = endClip;
                                                    } else {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    } else {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                }
                                                if (!(startClip3.getParent() instanceof ViewGroup)) {
                                                    endLeft2 = (ViewGroup) startClip3.getParent();
                                                    ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                                    addListener(/* anonymous class already generated */);
                                                }
                                                return endHeight2;
                                            }
                                            startTop = endLeft3;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            startClip = startClip3;
                                            endClip = endClip4;
                                            map3 = startParentVals;
                                            i2 = startBottom;
                                            viewGroup5 = endParent;
                                            startHeight = startValues;
                                            transitionValues3 = endValues;
                                        } else if (view > null) {
                                            startTop = endLeft3;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            startClip = startClip3;
                                            endClip = endClip4;
                                            map3 = startParentVals;
                                            i2 = startBottom;
                                            viewGroup5 = endParent;
                                            startHeight = startValues;
                                            transitionValues3 = endValues;
                                        } else {
                                            startClip = startClip3;
                                            endClip = endClip4;
                                            if (changeBounds.mResizeClip) {
                                                i = endWidth;
                                                i2 = startHeight;
                                                startClip3 = view4;
                                                endClip4 = endParent;
                                                endParent = Math.max(endClip4, endWidth);
                                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                                if (startLeft == endLeft3) {
                                                    if (startTop2 != endTop) {
                                                        endLeft = endLeft3;
                                                        startBottom = 0;
                                                        startTop = startTop2;
                                                        i3 = startLeft;
                                                        startTop2 = endClip;
                                                        if (startClip != null) {
                                                            startRight = 0;
                                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                                        } else {
                                                            startRight = 0;
                                                            startLeft = startClip;
                                                        }
                                                        if (endClip != null) {
                                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                        } else {
                                                            endClip2 = endClip;
                                                        }
                                                        if (startLeft.equals(endClip2)) {
                                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                                            startTop = endHeight2;
                                                            clipAnimator = null;
                                                            startTop = endLeft;
                                                            endLeft = endClip4;
                                                            endClip3 = endHeight2;
                                                            endClip = endClip2;
                                                            view2 = startClip3;
                                                            startClip2 = startLeft;
                                                            startLeft = startTop;
                                                            maxWidth = endParent;
                                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                            endWidth = endTop;
                                                            startHeight = startParentVals;
                                                            endRight = startParentVals;
                                                            startParentVals = true;
                                                            endTop = startParent;
                                                            endHeight2 = /* anonymous class already generated */;
                                                            endParent.addListener(endClip3);
                                                        } else {
                                                            endClip = endClip2;
                                                            startClip2 = startLeft;
                                                            i4 = endWidth;
                                                            i5 = startHeight;
                                                            i6 = endTop;
                                                            map3 = startParentVals;
                                                            viewGroup4 = endParent;
                                                            startTop = endLeft;
                                                            startParentVals = true;
                                                            endLeft = endClip4;
                                                            endParent = null;
                                                        }
                                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                    }
                                                }
                                                positionAnimator = null;
                                                startTop = startTop2;
                                                endLeft = endLeft3;
                                                startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                startTop2 = endClip;
                                                if (startClip != null) {
                                                    startRight = 0;
                                                    startLeft = startClip;
                                                } else {
                                                    startRight = 0;
                                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                                }
                                                if (endClip != null) {
                                                    endClip2 = endClip;
                                                } else {
                                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                }
                                                if (startLeft.equals(endClip2)) {
                                                    endClip = endClip2;
                                                    startClip2 = startLeft;
                                                    i4 = endWidth;
                                                    i5 = startHeight;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    viewGroup4 = endParent;
                                                    startTop = endLeft;
                                                    startParentVals = true;
                                                    endLeft = endClip4;
                                                    endParent = null;
                                                } else {
                                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                                    startTop = endHeight2;
                                                    clipAnimator = null;
                                                    startTop = endLeft;
                                                    endLeft = endClip4;
                                                    endClip3 = endHeight2;
                                                    endClip = endClip2;
                                                    view2 = startClip3;
                                                    startClip2 = startLeft;
                                                    startLeft = startTop;
                                                    maxWidth = endParent;
                                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                    endWidth = endTop;
                                                    startHeight = startParentVals;
                                                    endRight = startParentVals;
                                                    startParentVals = true;
                                                    endTop = startParent;
                                                    endHeight2 = /* anonymous class already generated */;
                                                    endParent.addListener(endClip3);
                                                }
                                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                            } else {
                                                startClip3 = view4;
                                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                                if (view != 2) {
                                                    endHeight = endHeight2;
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    view3 = startClip3;
                                                    i3 = endParent;
                                                    if (startLeft == endLeft3) {
                                                        startClip3 = view3;
                                                    } else if (startTop2 != endTop) {
                                                        startClip3 = view3;
                                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                        startTop = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        endLeft = i3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i3 = startLeft;
                                                        i2 = startBottom;
                                                    } else {
                                                        startClip3 = view3;
                                                    }
                                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                    startTop = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    endLeft = i3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i3 = startLeft;
                                                    i2 = startBottom;
                                                } else {
                                                    if (endParent == endWidth) {
                                                    }
                                                    endHeight = endHeight2;
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    view4 = view;
                                                    endHeight2 = new ViewBounds(startClip3);
                                                    i3 = endParent;
                                                    topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                    startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                    view3 = startClip3;
                                                    bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                    set = new AnimatorSet();
                                                    set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                    endParent2 = set;
                                                    set.addListener(/* anonymous class already generated */);
                                                    topLeftPath2 = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    endHeight2 = endParent2;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    startClip3 = view3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i2 = startBottom;
                                                    view3 = i3;
                                                    i3 = startLeft;
                                                }
                                            }
                                            if (!(startClip3.getParent() instanceof ViewGroup)) {
                                                endLeft2 = (ViewGroup) startClip3.getParent();
                                                ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                                addListener(/* anonymous class already generated */);
                                            }
                                            return endHeight2;
                                        }
                                    }
                                }
                                view++;
                                if (startClip3 != null) {
                                    if (startClip3.equals(endClip4)) {
                                    }
                                    view++;
                                    if (view > null) {
                                        startTop = endLeft3;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        map3 = startParentVals;
                                        i2 = startBottom;
                                        viewGroup5 = endParent;
                                        startHeight = startValues;
                                        transitionValues3 = endValues;
                                    } else {
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        if (changeBounds.mResizeClip) {
                                            i = endWidth;
                                            i2 = startHeight;
                                            startClip3 = view4;
                                            endClip4 = endParent;
                                            endParent = Math.max(endClip4, endWidth);
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                            if (startLeft == endLeft3) {
                                                if (startTop2 != endTop) {
                                                    endLeft = endLeft3;
                                                    startBottom = 0;
                                                    startTop = startTop2;
                                                    i3 = startLeft;
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    } else {
                                                        endClip2 = endClip;
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    } else {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                }
                                            }
                                            positionAnimator = null;
                                            startTop = startTop2;
                                            endLeft = endLeft3;
                                            startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = startClip;
                                            } else {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            }
                                            if (endClip != null) {
                                                endClip2 = endClip;
                                            } else {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            } else {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        } else {
                                            startClip3 = view4;
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                            if (view != 2) {
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view3 = startClip3;
                                                i3 = endParent;
                                                if (startLeft == endLeft3) {
                                                    startClip3 = view3;
                                                } else if (startTop2 != endTop) {
                                                    startClip3 = view3;
                                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                    startTop = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    endLeft = i3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i3 = startLeft;
                                                    i2 = startBottom;
                                                } else {
                                                    startClip3 = view3;
                                                }
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            } else {
                                                if (endParent == endWidth) {
                                                }
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view4 = view;
                                                endHeight2 = new ViewBounds(startClip3);
                                                i3 = endParent;
                                                topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                view3 = startClip3;
                                                bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                set = new AnimatorSet();
                                                set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                endParent2 = set;
                                                set.addListener(/* anonymous class already generated */);
                                                topLeftPath2 = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                endHeight2 = endParent2;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                startClip3 = view3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i2 = startBottom;
                                                view3 = i3;
                                                i3 = startLeft;
                                            }
                                        }
                                        if (!(startClip3.getParent() instanceof ViewGroup)) {
                                            endLeft2 = (ViewGroup) startClip3.getParent();
                                            ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                            addListener(/* anonymous class already generated */);
                                        }
                                        return endHeight2;
                                    }
                                }
                                if (startClip3 != null) {
                                }
                                if (view > null) {
                                    startClip = startClip3;
                                    endClip = endClip4;
                                    if (changeBounds.mResizeClip) {
                                        startClip3 = view4;
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                        if (view != 2) {
                                            if (endParent == endWidth) {
                                            }
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view4 = view;
                                            endHeight2 = new ViewBounds(startClip3);
                                            i3 = endParent;
                                            topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                            startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                            view3 = startClip3;
                                            bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            set = new AnimatorSet();
                                            set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                            endParent2 = set;
                                            set.addListener(/* anonymous class already generated */);
                                            topLeftPath2 = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            endHeight2 = endParent2;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            startClip3 = view3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i2 = startBottom;
                                            view3 = i3;
                                            i3 = startLeft;
                                        } else {
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view3 = startClip3;
                                            i3 = endParent;
                                            if (startLeft == endLeft3) {
                                                startClip3 = view3;
                                            } else if (startTop2 != endTop) {
                                                startClip3 = view3;
                                            } else {
                                                startClip3 = view3;
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            }
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        }
                                    } else {
                                        i = endWidth;
                                        i2 = startHeight;
                                        startClip3 = view4;
                                        endClip4 = endParent;
                                        endParent = Math.max(endClip4, endWidth);
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                        if (startLeft == endLeft3) {
                                            if (startTop2 != endTop) {
                                                endLeft = endLeft3;
                                                startBottom = 0;
                                                startTop = startTop2;
                                                i3 = startLeft;
                                                startTop2 = endClip;
                                                if (startClip != null) {
                                                    startRight = 0;
                                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                                } else {
                                                    startRight = 0;
                                                    startLeft = startClip;
                                                }
                                                if (endClip != null) {
                                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                } else {
                                                    endClip2 = endClip;
                                                }
                                                if (startLeft.equals(endClip2)) {
                                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                                    startTop = endHeight2;
                                                    clipAnimator = null;
                                                    startTop = endLeft;
                                                    endLeft = endClip4;
                                                    endClip3 = endHeight2;
                                                    endClip = endClip2;
                                                    view2 = startClip3;
                                                    startClip2 = startLeft;
                                                    startLeft = startTop;
                                                    maxWidth = endParent;
                                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                    endWidth = endTop;
                                                    startHeight = startParentVals;
                                                    endRight = startParentVals;
                                                    startParentVals = true;
                                                    endTop = startParent;
                                                    endHeight2 = /* anonymous class already generated */;
                                                    endParent.addListener(endClip3);
                                                } else {
                                                    endClip = endClip2;
                                                    startClip2 = startLeft;
                                                    i4 = endWidth;
                                                    i5 = startHeight;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    viewGroup4 = endParent;
                                                    startTop = endLeft;
                                                    startParentVals = true;
                                                    endLeft = endClip4;
                                                    endParent = null;
                                                }
                                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                            }
                                        }
                                        positionAnimator = null;
                                        startTop = startTop2;
                                        endLeft = endLeft3;
                                        startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = startClip;
                                        } else {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        }
                                        if (endClip != null) {
                                            endClip2 = endClip;
                                        } else {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        } else {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                    if (!(startClip3.getParent() instanceof ViewGroup)) {
                                        endLeft2 = (ViewGroup) startClip3.getParent();
                                        ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                        addListener(/* anonymous class already generated */);
                                    }
                                    return endHeight2;
                                }
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            }
                        }
                        view = 0 + 1;
                        if (startRight == startParentVals) {
                            if (startBottom == startParent) {
                                if (startClip3 != null) {
                                    if (startClip3.equals(endClip4)) {
                                    }
                                    view++;
                                    if (view > null) {
                                        startTop = endLeft3;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        map3 = startParentVals;
                                        i2 = startBottom;
                                        viewGroup5 = endParent;
                                        startHeight = startValues;
                                        transitionValues3 = endValues;
                                    } else {
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        if (changeBounds.mResizeClip) {
                                            i = endWidth;
                                            i2 = startHeight;
                                            startClip3 = view4;
                                            endClip4 = endParent;
                                            endParent = Math.max(endClip4, endWidth);
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                            if (startLeft == endLeft3) {
                                                if (startTop2 != endTop) {
                                                    endLeft = endLeft3;
                                                    startBottom = 0;
                                                    startTop = startTop2;
                                                    i3 = startLeft;
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    } else {
                                                        endClip2 = endClip;
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    } else {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                }
                                            }
                                            positionAnimator = null;
                                            startTop = startTop2;
                                            endLeft = endLeft3;
                                            startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = startClip;
                                            } else {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            }
                                            if (endClip != null) {
                                                endClip2 = endClip;
                                            } else {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            } else {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        } else {
                                            startClip3 = view4;
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                            if (view != 2) {
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view3 = startClip3;
                                                i3 = endParent;
                                                if (startLeft == endLeft3) {
                                                    startClip3 = view3;
                                                } else if (startTop2 != endTop) {
                                                    startClip3 = view3;
                                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                    startTop = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    endLeft = i3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i3 = startLeft;
                                                    i2 = startBottom;
                                                } else {
                                                    startClip3 = view3;
                                                }
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            } else {
                                                if (endParent == endWidth) {
                                                }
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view4 = view;
                                                endHeight2 = new ViewBounds(startClip3);
                                                i3 = endParent;
                                                topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                view3 = startClip3;
                                                bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                set = new AnimatorSet();
                                                set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                endParent2 = set;
                                                set.addListener(/* anonymous class already generated */);
                                                topLeftPath2 = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                endHeight2 = endParent2;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                startClip3 = view3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i2 = startBottom;
                                                view3 = i3;
                                                i3 = startLeft;
                                            }
                                        }
                                        if (!(startClip3.getParent() instanceof ViewGroup)) {
                                            endLeft2 = (ViewGroup) startClip3.getParent();
                                            ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                            addListener(/* anonymous class already generated */);
                                        }
                                        return endHeight2;
                                    }
                                }
                                if (startClip3 != null) {
                                }
                                if (view > null) {
                                    startClip = startClip3;
                                    endClip = endClip4;
                                    if (changeBounds.mResizeClip) {
                                        startClip3 = view4;
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                        if (view != 2) {
                                            if (endParent == endWidth) {
                                            }
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view4 = view;
                                            endHeight2 = new ViewBounds(startClip3);
                                            i3 = endParent;
                                            topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                            startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                            view3 = startClip3;
                                            bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            set = new AnimatorSet();
                                            set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                            endParent2 = set;
                                            set.addListener(/* anonymous class already generated */);
                                            topLeftPath2 = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            endHeight2 = endParent2;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            startClip3 = view3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i2 = startBottom;
                                            view3 = i3;
                                            i3 = startLeft;
                                        } else {
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view3 = startClip3;
                                            i3 = endParent;
                                            if (startLeft == endLeft3) {
                                                startClip3 = view3;
                                            } else if (startTop2 != endTop) {
                                                startClip3 = view3;
                                            } else {
                                                startClip3 = view3;
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            }
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        }
                                    } else {
                                        i = endWidth;
                                        i2 = startHeight;
                                        startClip3 = view4;
                                        endClip4 = endParent;
                                        endParent = Math.max(endClip4, endWidth);
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                        if (startLeft == endLeft3) {
                                            if (startTop2 != endTop) {
                                                endLeft = endLeft3;
                                                startBottom = 0;
                                                startTop = startTop2;
                                                i3 = startLeft;
                                                startTop2 = endClip;
                                                if (startClip != null) {
                                                    startRight = 0;
                                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                                } else {
                                                    startRight = 0;
                                                    startLeft = startClip;
                                                }
                                                if (endClip != null) {
                                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                } else {
                                                    endClip2 = endClip;
                                                }
                                                if (startLeft.equals(endClip2)) {
                                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                                    startTop = endHeight2;
                                                    clipAnimator = null;
                                                    startTop = endLeft;
                                                    endLeft = endClip4;
                                                    endClip3 = endHeight2;
                                                    endClip = endClip2;
                                                    view2 = startClip3;
                                                    startClip2 = startLeft;
                                                    startLeft = startTop;
                                                    maxWidth = endParent;
                                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                    endWidth = endTop;
                                                    startHeight = startParentVals;
                                                    endRight = startParentVals;
                                                    startParentVals = true;
                                                    endTop = startParent;
                                                    endHeight2 = /* anonymous class already generated */;
                                                    endParent.addListener(endClip3);
                                                } else {
                                                    endClip = endClip2;
                                                    startClip2 = startLeft;
                                                    i4 = endWidth;
                                                    i5 = startHeight;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    viewGroup4 = endParent;
                                                    startTop = endLeft;
                                                    startParentVals = true;
                                                    endLeft = endClip4;
                                                    endParent = null;
                                                }
                                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                            }
                                        }
                                        positionAnimator = null;
                                        startTop = startTop2;
                                        endLeft = endLeft3;
                                        startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = startClip;
                                        } else {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        }
                                        if (endClip != null) {
                                            endClip2 = endClip;
                                        } else {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        } else {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                    if (!(startClip3.getParent() instanceof ViewGroup)) {
                                        endLeft2 = (ViewGroup) startClip3.getParent();
                                        ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                        addListener(/* anonymous class already generated */);
                                    }
                                    return endHeight2;
                                }
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            }
                        }
                        view++;
                        if (startClip3 != null) {
                            if (startClip3.equals(endClip4)) {
                            }
                            view++;
                            if (view > null) {
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            } else {
                                startClip = startClip3;
                                endClip = endClip4;
                                if (changeBounds.mResizeClip) {
                                    i = endWidth;
                                    i2 = startHeight;
                                    startClip3 = view4;
                                    endClip4 = endParent;
                                    endParent = Math.max(endClip4, endWidth);
                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                    if (startLeft == endLeft3) {
                                        if (startTop2 != endTop) {
                                            endLeft = endLeft3;
                                            startBottom = 0;
                                            startTop = startTop2;
                                            i3 = startLeft;
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            } else {
                                                startRight = 0;
                                                startLeft = startClip;
                                            }
                                            if (endClip != null) {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            } else {
                                                endClip2 = endClip;
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            } else {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        }
                                    }
                                    positionAnimator = null;
                                    startTop = startTop2;
                                    endLeft = endLeft3;
                                    startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                    startTop2 = endClip;
                                    if (startClip != null) {
                                        startRight = 0;
                                        startLeft = startClip;
                                    } else {
                                        startRight = 0;
                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                    }
                                    if (endClip != null) {
                                        endClip2 = endClip;
                                    } else {
                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                    }
                                    if (startLeft.equals(endClip2)) {
                                        endClip = endClip2;
                                        startClip2 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        viewGroup4 = endParent;
                                        startTop = endLeft;
                                        startParentVals = true;
                                        endLeft = endClip4;
                                        endParent = null;
                                    } else {
                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                        startTop = endHeight2;
                                        clipAnimator = null;
                                        startTop = endLeft;
                                        endLeft = endClip4;
                                        endClip3 = endHeight2;
                                        endClip = endClip2;
                                        view2 = startClip3;
                                        startClip2 = startLeft;
                                        startLeft = startTop;
                                        maxWidth = endParent;
                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                        endWidth = endTop;
                                        startHeight = startParentVals;
                                        endRight = startParentVals;
                                        startParentVals = true;
                                        endTop = startParent;
                                        endHeight2 = /* anonymous class already generated */;
                                        endParent.addListener(endClip3);
                                    }
                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                } else {
                                    startClip3 = view4;
                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                    if (view != 2) {
                                        endHeight = endHeight2;
                                        i = endWidth;
                                        i2 = startHeight;
                                        view3 = startClip3;
                                        i3 = endParent;
                                        if (startLeft == endLeft3) {
                                            startClip3 = view3;
                                        } else if (startTop2 != endTop) {
                                            startClip3 = view3;
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        } else {
                                            startClip3 = view3;
                                        }
                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        endLeft = i3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i2 = startBottom;
                                    } else {
                                        if (endParent == endWidth) {
                                        }
                                        endHeight = endHeight2;
                                        i = endWidth;
                                        i2 = startHeight;
                                        view4 = view;
                                        endHeight2 = new ViewBounds(startClip3);
                                        i3 = endParent;
                                        topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                        startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                        view3 = startClip3;
                                        bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                        set = new AnimatorSet();
                                        set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                        endParent2 = set;
                                        set.addListener(/* anonymous class already generated */);
                                        topLeftPath2 = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        endHeight2 = endParent2;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        startClip3 = view3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i2 = startBottom;
                                        view3 = i3;
                                        i3 = startLeft;
                                    }
                                }
                                if (!(startClip3.getParent() instanceof ViewGroup)) {
                                    endLeft2 = (ViewGroup) startClip3.getParent();
                                    ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                    addListener(/* anonymous class already generated */);
                                }
                                return endHeight2;
                            }
                        }
                        if (startClip3 != null) {
                        }
                        if (view > null) {
                            startClip = startClip3;
                            endClip = endClip4;
                            if (changeBounds.mResizeClip) {
                                startClip3 = view4;
                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                if (view != 2) {
                                    if (endParent == endWidth) {
                                    }
                                    endHeight = endHeight2;
                                    i = endWidth;
                                    i2 = startHeight;
                                    view4 = view;
                                    endHeight2 = new ViewBounds(startClip3);
                                    i3 = endParent;
                                    topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                    startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                    view3 = startClip3;
                                    bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                    set = new AnimatorSet();
                                    set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                    endParent2 = set;
                                    set.addListener(/* anonymous class already generated */);
                                    topLeftPath2 = endLeft3;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    endHeight2 = endParent2;
                                    i7 = endHeight;
                                    i5 = i2;
                                    i4 = i;
                                    startClip3 = view3;
                                    startParentVals = true;
                                    endHeight = startRight;
                                    i = startTop2;
                                    i2 = startBottom;
                                    view3 = i3;
                                    i3 = startLeft;
                                } else {
                                    endHeight = endHeight2;
                                    i = endWidth;
                                    i2 = startHeight;
                                    view3 = startClip3;
                                    i3 = endParent;
                                    if (startLeft == endLeft3) {
                                        startClip3 = view3;
                                    } else if (startTop2 != endTop) {
                                        startClip3 = view3;
                                    } else {
                                        startClip3 = view3;
                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                        startTop = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        endLeft = i3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i2 = startBottom;
                                    }
                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                    startTop = endLeft3;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    i7 = endHeight;
                                    i5 = i2;
                                    i4 = i;
                                    endLeft = i3;
                                    startParentVals = true;
                                    endHeight = startRight;
                                    i = startTop2;
                                    i3 = startLeft;
                                    i2 = startBottom;
                                }
                            } else {
                                i = endWidth;
                                i2 = startHeight;
                                startClip3 = view4;
                                endClip4 = endParent;
                                endParent = Math.max(endClip4, endWidth);
                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                if (startLeft == endLeft3) {
                                    if (startTop2 != endTop) {
                                        endLeft = endLeft3;
                                        startBottom = 0;
                                        startTop = startTop2;
                                        i3 = startLeft;
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        } else {
                                            startRight = 0;
                                            startLeft = startClip;
                                        }
                                        if (endClip != null) {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        } else {
                                            endClip2 = endClip;
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        } else {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                }
                                positionAnimator = null;
                                startTop = startTop2;
                                endLeft = endLeft3;
                                startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                startTop2 = endClip;
                                if (startClip != null) {
                                    startRight = 0;
                                    startLeft = startClip;
                                } else {
                                    startRight = 0;
                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                }
                                if (endClip != null) {
                                    endClip2 = endClip;
                                } else {
                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                }
                                if (startLeft.equals(endClip2)) {
                                    endClip = endClip2;
                                    startClip2 = startLeft;
                                    i4 = endWidth;
                                    i5 = startHeight;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    viewGroup4 = endParent;
                                    startTop = endLeft;
                                    startParentVals = true;
                                    endLeft = endClip4;
                                    endParent = null;
                                } else {
                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                    startTop = endHeight2;
                                    clipAnimator = null;
                                    startTop = endLeft;
                                    endLeft = endClip4;
                                    endClip3 = endHeight2;
                                    endClip = endClip2;
                                    view2 = startClip3;
                                    startClip2 = startLeft;
                                    startLeft = startTop;
                                    maxWidth = endParent;
                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                    endWidth = endTop;
                                    startHeight = startParentVals;
                                    endRight = startParentVals;
                                    startParentVals = true;
                                    endTop = startParent;
                                    endHeight2 = /* anonymous class already generated */;
                                    endParent.addListener(endClip3);
                                }
                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                            }
                            if (!(startClip3.getParent() instanceof ViewGroup)) {
                                endLeft2 = (ViewGroup) startClip3.getParent();
                                ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                addListener(/* anonymous class already generated */);
                            }
                            return endHeight2;
                        }
                        startTop = endLeft3;
                        endHeight = startRight;
                        i = startTop2;
                        i3 = startLeft;
                        i4 = endWidth;
                        i5 = startHeight;
                        i6 = endTop;
                        startClip = startClip3;
                        endClip = endClip4;
                        map3 = startParentVals;
                        i2 = startBottom;
                        viewGroup5 = endParent;
                        startHeight = startValues;
                        transitionValues3 = endValues;
                    }
                    if (endWidth == 0 || endHeight2 == 0) {
                        if (startClip3 != null) {
                            if (startClip3.equals(endClip4)) {
                            }
                            view++;
                            if (view > null) {
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            } else {
                                startClip = startClip3;
                                endClip = endClip4;
                                if (changeBounds.mResizeClip) {
                                    i = endWidth;
                                    i2 = startHeight;
                                    startClip3 = view4;
                                    endClip4 = endParent;
                                    endParent = Math.max(endClip4, endWidth);
                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                    if (startLeft == endLeft3) {
                                        if (startTop2 != endTop) {
                                            endLeft = endLeft3;
                                            startBottom = 0;
                                            startTop = startTop2;
                                            i3 = startLeft;
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            } else {
                                                startRight = 0;
                                                startLeft = startClip;
                                            }
                                            if (endClip != null) {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            } else {
                                                endClip2 = endClip;
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            } else {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        }
                                    }
                                    positionAnimator = null;
                                    startTop = startTop2;
                                    endLeft = endLeft3;
                                    startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                    startTop2 = endClip;
                                    if (startClip != null) {
                                        startRight = 0;
                                        startLeft = startClip;
                                    } else {
                                        startRight = 0;
                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                    }
                                    if (endClip != null) {
                                        endClip2 = endClip;
                                    } else {
                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                    }
                                    if (startLeft.equals(endClip2)) {
                                        endClip = endClip2;
                                        startClip2 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        viewGroup4 = endParent;
                                        startTop = endLeft;
                                        startParentVals = true;
                                        endLeft = endClip4;
                                        endParent = null;
                                    } else {
                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                        startTop = endHeight2;
                                        clipAnimator = null;
                                        startTop = endLeft;
                                        endLeft = endClip4;
                                        endClip3 = endHeight2;
                                        endClip = endClip2;
                                        view2 = startClip3;
                                        startClip2 = startLeft;
                                        startLeft = startTop;
                                        maxWidth = endParent;
                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                        endWidth = endTop;
                                        startHeight = startParentVals;
                                        endRight = startParentVals;
                                        startParentVals = true;
                                        endTop = startParent;
                                        endHeight2 = /* anonymous class already generated */;
                                        endParent.addListener(endClip3);
                                    }
                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                } else {
                                    startClip3 = view4;
                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                    if (view != 2) {
                                        endHeight = endHeight2;
                                        i = endWidth;
                                        i2 = startHeight;
                                        view3 = startClip3;
                                        i3 = endParent;
                                        if (startLeft == endLeft3) {
                                            startClip3 = view3;
                                        } else if (startTop2 != endTop) {
                                            startClip3 = view3;
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        } else {
                                            startClip3 = view3;
                                        }
                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        endLeft = i3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i2 = startBottom;
                                    } else {
                                        if (endParent == endWidth) {
                                        }
                                        endHeight = endHeight2;
                                        i = endWidth;
                                        i2 = startHeight;
                                        view4 = view;
                                        endHeight2 = new ViewBounds(startClip3);
                                        i3 = endParent;
                                        topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                        startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                        view3 = startClip3;
                                        bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                        set = new AnimatorSet();
                                        set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                        endParent2 = set;
                                        set.addListener(/* anonymous class already generated */);
                                        topLeftPath2 = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        endHeight2 = endParent2;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        startClip3 = view3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i2 = startBottom;
                                        view3 = i3;
                                        i3 = startLeft;
                                    }
                                }
                                if (!(startClip3.getParent() instanceof ViewGroup)) {
                                    endLeft2 = (ViewGroup) startClip3.getParent();
                                    ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                    addListener(/* anonymous class already generated */);
                                }
                                return endHeight2;
                            }
                        }
                        if (startClip3 != null) {
                        }
                        if (view > null) {
                            startClip = startClip3;
                            endClip = endClip4;
                            if (changeBounds.mResizeClip) {
                                startClip3 = view4;
                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                if (view != 2) {
                                    if (endParent == endWidth) {
                                    }
                                    endHeight = endHeight2;
                                    i = endWidth;
                                    i2 = startHeight;
                                    view4 = view;
                                    endHeight2 = new ViewBounds(startClip3);
                                    i3 = endParent;
                                    topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                    startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                    view3 = startClip3;
                                    bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                    set = new AnimatorSet();
                                    set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                    endParent2 = set;
                                    set.addListener(/* anonymous class already generated */);
                                    topLeftPath2 = endLeft3;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    endHeight2 = endParent2;
                                    i7 = endHeight;
                                    i5 = i2;
                                    i4 = i;
                                    startClip3 = view3;
                                    startParentVals = true;
                                    endHeight = startRight;
                                    i = startTop2;
                                    i2 = startBottom;
                                    view3 = i3;
                                    i3 = startLeft;
                                } else {
                                    endHeight = endHeight2;
                                    i = endWidth;
                                    i2 = startHeight;
                                    view3 = startClip3;
                                    i3 = endParent;
                                    if (startLeft == endLeft3) {
                                        startClip3 = view3;
                                    } else if (startTop2 != endTop) {
                                        startClip3 = view3;
                                    } else {
                                        startClip3 = view3;
                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                        startTop = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        endLeft = i3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i2 = startBottom;
                                    }
                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                    startTop = endLeft3;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    i7 = endHeight;
                                    i5 = i2;
                                    i4 = i;
                                    endLeft = i3;
                                    startParentVals = true;
                                    endHeight = startRight;
                                    i = startTop2;
                                    i3 = startLeft;
                                    i2 = startBottom;
                                }
                            } else {
                                i = endWidth;
                                i2 = startHeight;
                                startClip3 = view4;
                                endClip4 = endParent;
                                endParent = Math.max(endClip4, endWidth);
                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                if (startLeft == endLeft3) {
                                    if (startTop2 != endTop) {
                                        endLeft = endLeft3;
                                        startBottom = 0;
                                        startTop = startTop2;
                                        i3 = startLeft;
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        } else {
                                            startRight = 0;
                                            startLeft = startClip;
                                        }
                                        if (endClip != null) {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        } else {
                                            endClip2 = endClip;
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        } else {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                }
                                positionAnimator = null;
                                startTop = startTop2;
                                endLeft = endLeft3;
                                startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                startTop2 = endClip;
                                if (startClip != null) {
                                    startRight = 0;
                                    startLeft = startClip;
                                } else {
                                    startRight = 0;
                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                }
                                if (endClip != null) {
                                    endClip2 = endClip;
                                } else {
                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                }
                                if (startLeft.equals(endClip2)) {
                                    endClip = endClip2;
                                    startClip2 = startLeft;
                                    i4 = endWidth;
                                    i5 = startHeight;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    viewGroup4 = endParent;
                                    startTop = endLeft;
                                    startParentVals = true;
                                    endLeft = endClip4;
                                    endParent = null;
                                } else {
                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                    startTop = endHeight2;
                                    clipAnimator = null;
                                    startTop = endLeft;
                                    endLeft = endClip4;
                                    endClip3 = endHeight2;
                                    endClip = endClip2;
                                    view2 = startClip3;
                                    startClip2 = startLeft;
                                    startLeft = startTop;
                                    maxWidth = endParent;
                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                    endWidth = endTop;
                                    startHeight = startParentVals;
                                    endRight = startParentVals;
                                    startParentVals = true;
                                    endTop = startParent;
                                    endHeight2 = /* anonymous class already generated */;
                                    endParent.addListener(endClip3);
                                }
                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                            }
                            if (!(startClip3.getParent() instanceof ViewGroup)) {
                                endLeft2 = (ViewGroup) startClip3.getParent();
                                ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                addListener(/* anonymous class already generated */);
                            }
                            return endHeight2;
                        }
                        startTop = endLeft3;
                        endHeight = startRight;
                        i = startTop2;
                        i3 = startLeft;
                        i4 = endWidth;
                        i5 = startHeight;
                        i6 = endTop;
                        startClip = startClip3;
                        endClip = endClip4;
                        map3 = startParentVals;
                        i2 = startBottom;
                        viewGroup5 = endParent;
                        startHeight = startValues;
                        transitionValues3 = endValues;
                    } else {
                        if (startLeft == endLeft3) {
                            if (startTop2 != endTop) {
                                if (startRight == startParentVals) {
                                    if (startBottom == startParent) {
                                        if (startClip3 != null) {
                                            if (startClip3.equals(endClip4)) {
                                            }
                                            view++;
                                            if (view > null) {
                                                startTop = endLeft3;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                startClip = startClip3;
                                                endClip = endClip4;
                                                map3 = startParentVals;
                                                i2 = startBottom;
                                                viewGroup5 = endParent;
                                                startHeight = startValues;
                                                transitionValues3 = endValues;
                                            } else {
                                                startClip = startClip3;
                                                endClip = endClip4;
                                                if (changeBounds.mResizeClip) {
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    startClip3 = view4;
                                                    endClip4 = endParent;
                                                    endParent = Math.max(endClip4, endWidth);
                                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                                    if (startLeft == endLeft3) {
                                                        if (startTop2 != endTop) {
                                                            endLeft = endLeft3;
                                                            startBottom = 0;
                                                            startTop = startTop2;
                                                            i3 = startLeft;
                                                            startTop2 = endClip;
                                                            if (startClip != null) {
                                                                startRight = 0;
                                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                                            } else {
                                                                startRight = 0;
                                                                startLeft = startClip;
                                                            }
                                                            if (endClip != null) {
                                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                            } else {
                                                                endClip2 = endClip;
                                                            }
                                                            if (startLeft.equals(endClip2)) {
                                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                                startTop = endHeight2;
                                                                clipAnimator = null;
                                                                startTop = endLeft;
                                                                endLeft = endClip4;
                                                                endClip3 = endHeight2;
                                                                endClip = endClip2;
                                                                view2 = startClip3;
                                                                startClip2 = startLeft;
                                                                startLeft = startTop;
                                                                maxWidth = endParent;
                                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                                endWidth = endTop;
                                                                startHeight = startParentVals;
                                                                endRight = startParentVals;
                                                                startParentVals = true;
                                                                endTop = startParent;
                                                                endHeight2 = /* anonymous class already generated */;
                                                                endParent.addListener(endClip3);
                                                            } else {
                                                                endClip = endClip2;
                                                                startClip2 = startLeft;
                                                                i4 = endWidth;
                                                                i5 = startHeight;
                                                                i6 = endTop;
                                                                map3 = startParentVals;
                                                                viewGroup4 = endParent;
                                                                startTop = endLeft;
                                                                startParentVals = true;
                                                                endLeft = endClip4;
                                                                endParent = null;
                                                            }
                                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                        }
                                                    }
                                                    positionAnimator = null;
                                                    startTop = startTop2;
                                                    endLeft = endLeft3;
                                                    startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = endClip;
                                                    } else {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    } else {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                } else {
                                                    startClip3 = view4;
                                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                                    if (view != 2) {
                                                        endHeight = endHeight2;
                                                        i = endWidth;
                                                        i2 = startHeight;
                                                        view3 = startClip3;
                                                        i3 = endParent;
                                                        if (startLeft == endLeft3) {
                                                            startClip3 = view3;
                                                        } else if (startTop2 != endTop) {
                                                            startClip3 = view3;
                                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                            startTop = endLeft3;
                                                            i6 = endTop;
                                                            map3 = startParentVals;
                                                            i7 = endHeight;
                                                            i5 = i2;
                                                            i4 = i;
                                                            endLeft = i3;
                                                            startParentVals = true;
                                                            endHeight = startRight;
                                                            i = startTop2;
                                                            i3 = startLeft;
                                                            i2 = startBottom;
                                                        } else {
                                                            startClip3 = view3;
                                                        }
                                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                        startTop = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        endLeft = i3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i3 = startLeft;
                                                        i2 = startBottom;
                                                    } else {
                                                        if (endParent == endWidth) {
                                                        }
                                                        endHeight = endHeight2;
                                                        i = endWidth;
                                                        i2 = startHeight;
                                                        view4 = view;
                                                        endHeight2 = new ViewBounds(startClip3);
                                                        i3 = endParent;
                                                        topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                        startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                        view3 = startClip3;
                                                        bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                        set = new AnimatorSet();
                                                        set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                        endParent2 = set;
                                                        set.addListener(/* anonymous class already generated */);
                                                        topLeftPath2 = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        endHeight2 = endParent2;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        startClip3 = view3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i2 = startBottom;
                                                        view3 = i3;
                                                        i3 = startLeft;
                                                    }
                                                }
                                                if (!(startClip3.getParent() instanceof ViewGroup)) {
                                                    endLeft2 = (ViewGroup) startClip3.getParent();
                                                    ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                                    addListener(/* anonymous class already generated */);
                                                }
                                                return endHeight2;
                                            }
                                        }
                                        if (startClip3 != null) {
                                        }
                                        if (view > null) {
                                            startClip = startClip3;
                                            endClip = endClip4;
                                            if (changeBounds.mResizeClip) {
                                                startClip3 = view4;
                                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                                if (view != 2) {
                                                    if (endParent == endWidth) {
                                                    }
                                                    endHeight = endHeight2;
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    view4 = view;
                                                    endHeight2 = new ViewBounds(startClip3);
                                                    i3 = endParent;
                                                    topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                    startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                    view3 = startClip3;
                                                    bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                    set = new AnimatorSet();
                                                    set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                    endParent2 = set;
                                                    set.addListener(/* anonymous class already generated */);
                                                    topLeftPath2 = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    endHeight2 = endParent2;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    startClip3 = view3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i2 = startBottom;
                                                    view3 = i3;
                                                    i3 = startLeft;
                                                } else {
                                                    endHeight = endHeight2;
                                                    i = endWidth;
                                                    i2 = startHeight;
                                                    view3 = startClip3;
                                                    i3 = endParent;
                                                    if (startLeft == endLeft3) {
                                                        startClip3 = view3;
                                                    } else if (startTop2 != endTop) {
                                                        startClip3 = view3;
                                                    } else {
                                                        startClip3 = view3;
                                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                        startTop = endLeft3;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        i7 = endHeight;
                                                        i5 = i2;
                                                        i4 = i;
                                                        endLeft = i3;
                                                        startParentVals = true;
                                                        endHeight = startRight;
                                                        i = startTop2;
                                                        i3 = startLeft;
                                                        i2 = startBottom;
                                                    }
                                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                    startTop = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    endLeft = i3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i3 = startLeft;
                                                    i2 = startBottom;
                                                }
                                            } else {
                                                i = endWidth;
                                                i2 = startHeight;
                                                startClip3 = view4;
                                                endClip4 = endParent;
                                                endParent = Math.max(endClip4, endWidth);
                                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                                if (startLeft == endLeft3) {
                                                    if (startTop2 != endTop) {
                                                        endLeft = endLeft3;
                                                        startBottom = 0;
                                                        startTop = startTop2;
                                                        i3 = startLeft;
                                                        startTop2 = endClip;
                                                        if (startClip != null) {
                                                            startRight = 0;
                                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                                        } else {
                                                            startRight = 0;
                                                            startLeft = startClip;
                                                        }
                                                        if (endClip != null) {
                                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                        } else {
                                                            endClip2 = endClip;
                                                        }
                                                        if (startLeft.equals(endClip2)) {
                                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                                            startTop = endHeight2;
                                                            clipAnimator = null;
                                                            startTop = endLeft;
                                                            endLeft = endClip4;
                                                            endClip3 = endHeight2;
                                                            endClip = endClip2;
                                                            view2 = startClip3;
                                                            startClip2 = startLeft;
                                                            startLeft = startTop;
                                                            maxWidth = endParent;
                                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                            endWidth = endTop;
                                                            startHeight = startParentVals;
                                                            endRight = startParentVals;
                                                            startParentVals = true;
                                                            endTop = startParent;
                                                            endHeight2 = /* anonymous class already generated */;
                                                            endParent.addListener(endClip3);
                                                        } else {
                                                            endClip = endClip2;
                                                            startClip2 = startLeft;
                                                            i4 = endWidth;
                                                            i5 = startHeight;
                                                            i6 = endTop;
                                                            map3 = startParentVals;
                                                            viewGroup4 = endParent;
                                                            startTop = endLeft;
                                                            startParentVals = true;
                                                            endLeft = endClip4;
                                                            endParent = null;
                                                        }
                                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                    }
                                                }
                                                positionAnimator = null;
                                                startTop = startTop2;
                                                endLeft = endLeft3;
                                                startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                startTop2 = endClip;
                                                if (startClip != null) {
                                                    startRight = 0;
                                                    startLeft = startClip;
                                                } else {
                                                    startRight = 0;
                                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                                }
                                                if (endClip != null) {
                                                    endClip2 = endClip;
                                                } else {
                                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                }
                                                if (startLeft.equals(endClip2)) {
                                                    endClip = endClip2;
                                                    startClip2 = startLeft;
                                                    i4 = endWidth;
                                                    i5 = startHeight;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    viewGroup4 = endParent;
                                                    startTop = endLeft;
                                                    startParentVals = true;
                                                    endLeft = endClip4;
                                                    endParent = null;
                                                } else {
                                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                                    startTop = endHeight2;
                                                    clipAnimator = null;
                                                    startTop = endLeft;
                                                    endLeft = endClip4;
                                                    endClip3 = endHeight2;
                                                    endClip = endClip2;
                                                    view2 = startClip3;
                                                    startClip2 = startLeft;
                                                    startLeft = startTop;
                                                    maxWidth = endParent;
                                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                    endWidth = endTop;
                                                    startHeight = startParentVals;
                                                    endRight = startParentVals;
                                                    startParentVals = true;
                                                    endTop = startParent;
                                                    endHeight2 = /* anonymous class already generated */;
                                                    endParent.addListener(endClip3);
                                                }
                                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                            }
                                            if (!(startClip3.getParent() instanceof ViewGroup)) {
                                                endLeft2 = (ViewGroup) startClip3.getParent();
                                                ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                                addListener(/* anonymous class already generated */);
                                            }
                                            return endHeight2;
                                        }
                                        startTop = endLeft3;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        map3 = startParentVals;
                                        i2 = startBottom;
                                        viewGroup5 = endParent;
                                        startHeight = startValues;
                                        transitionValues3 = endValues;
                                    }
                                }
                                view++;
                                if (startClip3 != null) {
                                    if (startClip3.equals(endClip4)) {
                                    }
                                    view++;
                                    if (view > null) {
                                        startTop = endLeft3;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        map3 = startParentVals;
                                        i2 = startBottom;
                                        viewGroup5 = endParent;
                                        startHeight = startValues;
                                        transitionValues3 = endValues;
                                    } else {
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        if (changeBounds.mResizeClip) {
                                            i = endWidth;
                                            i2 = startHeight;
                                            startClip3 = view4;
                                            endClip4 = endParent;
                                            endParent = Math.max(endClip4, endWidth);
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                            if (startLeft == endLeft3) {
                                                if (startTop2 != endTop) {
                                                    endLeft = endLeft3;
                                                    startBottom = 0;
                                                    startTop = startTop2;
                                                    i3 = startLeft;
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    } else {
                                                        endClip2 = endClip;
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    } else {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                }
                                            }
                                            positionAnimator = null;
                                            startTop = startTop2;
                                            endLeft = endLeft3;
                                            startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = startClip;
                                            } else {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            }
                                            if (endClip != null) {
                                                endClip2 = endClip;
                                            } else {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            } else {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        } else {
                                            startClip3 = view4;
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                            if (view != 2) {
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view3 = startClip3;
                                                i3 = endParent;
                                                if (startLeft == endLeft3) {
                                                    startClip3 = view3;
                                                } else if (startTop2 != endTop) {
                                                    startClip3 = view3;
                                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                    startTop = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    endLeft = i3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i3 = startLeft;
                                                    i2 = startBottom;
                                                } else {
                                                    startClip3 = view3;
                                                }
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            } else {
                                                if (endParent == endWidth) {
                                                }
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view4 = view;
                                                endHeight2 = new ViewBounds(startClip3);
                                                i3 = endParent;
                                                topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                view3 = startClip3;
                                                bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                set = new AnimatorSet();
                                                set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                endParent2 = set;
                                                set.addListener(/* anonymous class already generated */);
                                                topLeftPath2 = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                endHeight2 = endParent2;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                startClip3 = view3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i2 = startBottom;
                                                view3 = i3;
                                                i3 = startLeft;
                                            }
                                        }
                                        if (!(startClip3.getParent() instanceof ViewGroup)) {
                                            endLeft2 = (ViewGroup) startClip3.getParent();
                                            ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                            addListener(/* anonymous class already generated */);
                                        }
                                        return endHeight2;
                                    }
                                }
                                if (startClip3 != null) {
                                }
                                if (view > null) {
                                    startClip = startClip3;
                                    endClip = endClip4;
                                    if (changeBounds.mResizeClip) {
                                        startClip3 = view4;
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                        if (view != 2) {
                                            if (endParent == endWidth) {
                                            }
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view4 = view;
                                            endHeight2 = new ViewBounds(startClip3);
                                            i3 = endParent;
                                            topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                            startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                            view3 = startClip3;
                                            bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            set = new AnimatorSet();
                                            set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                            endParent2 = set;
                                            set.addListener(/* anonymous class already generated */);
                                            topLeftPath2 = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            endHeight2 = endParent2;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            startClip3 = view3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i2 = startBottom;
                                            view3 = i3;
                                            i3 = startLeft;
                                        } else {
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view3 = startClip3;
                                            i3 = endParent;
                                            if (startLeft == endLeft3) {
                                                startClip3 = view3;
                                            } else if (startTop2 != endTop) {
                                                startClip3 = view3;
                                            } else {
                                                startClip3 = view3;
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            }
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        }
                                    } else {
                                        i = endWidth;
                                        i2 = startHeight;
                                        startClip3 = view4;
                                        endClip4 = endParent;
                                        endParent = Math.max(endClip4, endWidth);
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                        if (startLeft == endLeft3) {
                                            if (startTop2 != endTop) {
                                                endLeft = endLeft3;
                                                startBottom = 0;
                                                startTop = startTop2;
                                                i3 = startLeft;
                                                startTop2 = endClip;
                                                if (startClip != null) {
                                                    startRight = 0;
                                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                                } else {
                                                    startRight = 0;
                                                    startLeft = startClip;
                                                }
                                                if (endClip != null) {
                                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                } else {
                                                    endClip2 = endClip;
                                                }
                                                if (startLeft.equals(endClip2)) {
                                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                                    startTop = endHeight2;
                                                    clipAnimator = null;
                                                    startTop = endLeft;
                                                    endLeft = endClip4;
                                                    endClip3 = endHeight2;
                                                    endClip = endClip2;
                                                    view2 = startClip3;
                                                    startClip2 = startLeft;
                                                    startLeft = startTop;
                                                    maxWidth = endParent;
                                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                    endWidth = endTop;
                                                    startHeight = startParentVals;
                                                    endRight = startParentVals;
                                                    startParentVals = true;
                                                    endTop = startParent;
                                                    endHeight2 = /* anonymous class already generated */;
                                                    endParent.addListener(endClip3);
                                                } else {
                                                    endClip = endClip2;
                                                    startClip2 = startLeft;
                                                    i4 = endWidth;
                                                    i5 = startHeight;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    viewGroup4 = endParent;
                                                    startTop = endLeft;
                                                    startParentVals = true;
                                                    endLeft = endClip4;
                                                    endParent = null;
                                                }
                                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                            }
                                        }
                                        positionAnimator = null;
                                        startTop = startTop2;
                                        endLeft = endLeft3;
                                        startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = startClip;
                                        } else {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        }
                                        if (endClip != null) {
                                            endClip2 = endClip;
                                        } else {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        } else {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                    if (!(startClip3.getParent() instanceof ViewGroup)) {
                                        endLeft2 = (ViewGroup) startClip3.getParent();
                                        ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                        addListener(/* anonymous class already generated */);
                                    }
                                    return endHeight2;
                                }
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            }
                        }
                        view = 0 + 1;
                        if (startRight == startParentVals) {
                            if (startBottom == startParent) {
                                if (startClip3 != null) {
                                    if (startClip3.equals(endClip4)) {
                                    }
                                    view++;
                                    if (view > null) {
                                        startTop = endLeft3;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        map3 = startParentVals;
                                        i2 = startBottom;
                                        viewGroup5 = endParent;
                                        startHeight = startValues;
                                        transitionValues3 = endValues;
                                    } else {
                                        startClip = startClip3;
                                        endClip = endClip4;
                                        if (changeBounds.mResizeClip) {
                                            i = endWidth;
                                            i2 = startHeight;
                                            startClip3 = view4;
                                            endClip4 = endParent;
                                            endParent = Math.max(endClip4, endWidth);
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                            if (startLeft == endLeft3) {
                                                if (startTop2 != endTop) {
                                                    endLeft = endLeft3;
                                                    startBottom = 0;
                                                    startTop = startTop2;
                                                    i3 = startLeft;
                                                    startTop2 = endClip;
                                                    if (startClip != null) {
                                                        startRight = 0;
                                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                                    } else {
                                                        startRight = 0;
                                                        startLeft = startClip;
                                                    }
                                                    if (endClip != null) {
                                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                    } else {
                                                        endClip2 = endClip;
                                                    }
                                                    if (startLeft.equals(endClip2)) {
                                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                                        startTop = endHeight2;
                                                        clipAnimator = null;
                                                        startTop = endLeft;
                                                        endLeft = endClip4;
                                                        endClip3 = endHeight2;
                                                        endClip = endClip2;
                                                        view2 = startClip3;
                                                        startClip2 = startLeft;
                                                        startLeft = startTop;
                                                        maxWidth = endParent;
                                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                        endWidth = endTop;
                                                        startHeight = startParentVals;
                                                        endRight = startParentVals;
                                                        startParentVals = true;
                                                        endTop = startParent;
                                                        endHeight2 = /* anonymous class already generated */;
                                                        endParent.addListener(endClip3);
                                                    } else {
                                                        endClip = endClip2;
                                                        startClip2 = startLeft;
                                                        i4 = endWidth;
                                                        i5 = startHeight;
                                                        i6 = endTop;
                                                        map3 = startParentVals;
                                                        viewGroup4 = endParent;
                                                        startTop = endLeft;
                                                        startParentVals = true;
                                                        endLeft = endClip4;
                                                        endParent = null;
                                                    }
                                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                                }
                                            }
                                            positionAnimator = null;
                                            startTop = startTop2;
                                            endLeft = endLeft3;
                                            startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = startClip;
                                            } else {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            }
                                            if (endClip != null) {
                                                endClip2 = endClip;
                                            } else {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            } else {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        } else {
                                            startClip3 = view4;
                                            ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                            if (view != 2) {
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view3 = startClip3;
                                                i3 = endParent;
                                                if (startLeft == endLeft3) {
                                                    startClip3 = view3;
                                                } else if (startTop2 != endTop) {
                                                    startClip3 = view3;
                                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                    startTop = endLeft3;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    i7 = endHeight;
                                                    i5 = i2;
                                                    i4 = i;
                                                    endLeft = i3;
                                                    startParentVals = true;
                                                    endHeight = startRight;
                                                    i = startTop2;
                                                    i3 = startLeft;
                                                    i2 = startBottom;
                                                } else {
                                                    startClip3 = view3;
                                                }
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            } else {
                                                if (endParent == endWidth) {
                                                }
                                                endHeight = endHeight2;
                                                i = endWidth;
                                                i2 = startHeight;
                                                view4 = view;
                                                endHeight2 = new ViewBounds(startClip3);
                                                i3 = endParent;
                                                topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                                startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                                view3 = startClip3;
                                                bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                set = new AnimatorSet();
                                                set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                                endParent2 = set;
                                                set.addListener(/* anonymous class already generated */);
                                                topLeftPath2 = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                endHeight2 = endParent2;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                startClip3 = view3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i2 = startBottom;
                                                view3 = i3;
                                                i3 = startLeft;
                                            }
                                        }
                                        if (!(startClip3.getParent() instanceof ViewGroup)) {
                                            endLeft2 = (ViewGroup) startClip3.getParent();
                                            ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                            addListener(/* anonymous class already generated */);
                                        }
                                        return endHeight2;
                                    }
                                }
                                if (startClip3 != null) {
                                }
                                if (view > null) {
                                    startClip = startClip3;
                                    endClip = endClip4;
                                    if (changeBounds.mResizeClip) {
                                        startClip3 = view4;
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                        if (view != 2) {
                                            if (endParent == endWidth) {
                                            }
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view4 = view;
                                            endHeight2 = new ViewBounds(startClip3);
                                            i3 = endParent;
                                            topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                            startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                            view3 = startClip3;
                                            bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            set = new AnimatorSet();
                                            set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                            endParent2 = set;
                                            set.addListener(/* anonymous class already generated */);
                                            topLeftPath2 = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            endHeight2 = endParent2;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            startClip3 = view3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i2 = startBottom;
                                            view3 = i3;
                                            i3 = startLeft;
                                        } else {
                                            endHeight = endHeight2;
                                            i = endWidth;
                                            i2 = startHeight;
                                            view3 = startClip3;
                                            i3 = endParent;
                                            if (startLeft == endLeft3) {
                                                startClip3 = view3;
                                            } else if (startTop2 != endTop) {
                                                startClip3 = view3;
                                            } else {
                                                startClip3 = view3;
                                                endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                                startTop = endLeft3;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                i7 = endHeight;
                                                i5 = i2;
                                                i4 = i;
                                                endLeft = i3;
                                                startParentVals = true;
                                                endHeight = startRight;
                                                i = startTop2;
                                                i3 = startLeft;
                                                i2 = startBottom;
                                            }
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        }
                                    } else {
                                        i = endWidth;
                                        i2 = startHeight;
                                        startClip3 = view4;
                                        endClip4 = endParent;
                                        endParent = Math.max(endClip4, endWidth);
                                        ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                        if (startLeft == endLeft3) {
                                            if (startTop2 != endTop) {
                                                endLeft = endLeft3;
                                                startBottom = 0;
                                                startTop = startTop2;
                                                i3 = startLeft;
                                                startTop2 = endClip;
                                                if (startClip != null) {
                                                    startRight = 0;
                                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                                } else {
                                                    startRight = 0;
                                                    startLeft = startClip;
                                                }
                                                if (endClip != null) {
                                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                                } else {
                                                    endClip2 = endClip;
                                                }
                                                if (startLeft.equals(endClip2)) {
                                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                                    startTop = endHeight2;
                                                    clipAnimator = null;
                                                    startTop = endLeft;
                                                    endLeft = endClip4;
                                                    endClip3 = endHeight2;
                                                    endClip = endClip2;
                                                    view2 = startClip3;
                                                    startClip2 = startLeft;
                                                    startLeft = startTop;
                                                    maxWidth = endParent;
                                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                    endWidth = endTop;
                                                    startHeight = startParentVals;
                                                    endRight = startParentVals;
                                                    startParentVals = true;
                                                    endTop = startParent;
                                                    endHeight2 = /* anonymous class already generated */;
                                                    endParent.addListener(endClip3);
                                                } else {
                                                    endClip = endClip2;
                                                    startClip2 = startLeft;
                                                    i4 = endWidth;
                                                    i5 = startHeight;
                                                    i6 = endTop;
                                                    map3 = startParentVals;
                                                    viewGroup4 = endParent;
                                                    startTop = endLeft;
                                                    startParentVals = true;
                                                    endLeft = endClip4;
                                                    endParent = null;
                                                }
                                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                            }
                                        }
                                        positionAnimator = null;
                                        startTop = startTop2;
                                        endLeft = endLeft3;
                                        startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = startClip;
                                        } else {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        }
                                        if (endClip != null) {
                                            endClip2 = endClip;
                                        } else {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        } else {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                    if (!(startClip3.getParent() instanceof ViewGroup)) {
                                        endLeft2 = (ViewGroup) startClip3.getParent();
                                        ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                        addListener(/* anonymous class already generated */);
                                    }
                                    return endHeight2;
                                }
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            }
                        }
                        view++;
                        if (startClip3 != null) {
                            if (startClip3.equals(endClip4)) {
                            }
                            view++;
                            if (view > null) {
                                startTop = endLeft3;
                                endHeight = startRight;
                                i = startTop2;
                                i3 = startLeft;
                                i4 = endWidth;
                                i5 = startHeight;
                                i6 = endTop;
                                startClip = startClip3;
                                endClip = endClip4;
                                map3 = startParentVals;
                                i2 = startBottom;
                                viewGroup5 = endParent;
                                startHeight = startValues;
                                transitionValues3 = endValues;
                            } else {
                                startClip = startClip3;
                                endClip = endClip4;
                                if (changeBounds.mResizeClip) {
                                    i = endWidth;
                                    i2 = startHeight;
                                    startClip3 = view4;
                                    endClip4 = endParent;
                                    endParent = Math.max(endClip4, endWidth);
                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                    if (startLeft == endLeft3) {
                                        if (startTop2 != endTop) {
                                            endLeft = endLeft3;
                                            startBottom = 0;
                                            startTop = startTop2;
                                            i3 = startLeft;
                                            startTop2 = endClip;
                                            if (startClip != null) {
                                                startRight = 0;
                                                startLeft = new Rect(0, 0, endClip4, startHeight);
                                            } else {
                                                startRight = 0;
                                                startLeft = startClip;
                                            }
                                            if (endClip != null) {
                                                endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                            } else {
                                                endClip2 = endClip;
                                            }
                                            if (startLeft.equals(endClip2)) {
                                                ViewCompat.setClipBounds(startClip3, startLeft);
                                                startTop = endHeight2;
                                                clipAnimator = null;
                                                startTop = endLeft;
                                                endLeft = endClip4;
                                                endClip3 = endHeight2;
                                                endClip = endClip2;
                                                view2 = startClip3;
                                                startClip2 = startLeft;
                                                startLeft = startTop;
                                                maxWidth = endParent;
                                                endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                                endWidth = endTop;
                                                startHeight = startParentVals;
                                                endRight = startParentVals;
                                                startParentVals = true;
                                                endTop = startParent;
                                                endHeight2 = /* anonymous class already generated */;
                                                endParent.addListener(endClip3);
                                            } else {
                                                endClip = endClip2;
                                                startClip2 = startLeft;
                                                i4 = endWidth;
                                                i5 = startHeight;
                                                i6 = endTop;
                                                map3 = startParentVals;
                                                viewGroup4 = endParent;
                                                startTop = endLeft;
                                                startParentVals = true;
                                                endLeft = endClip4;
                                                endParent = null;
                                            }
                                            endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                        }
                                    }
                                    positionAnimator = null;
                                    startTop = startTop2;
                                    endLeft = endLeft3;
                                    startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                    startTop2 = endClip;
                                    if (startClip != null) {
                                        startRight = 0;
                                        startLeft = startClip;
                                    } else {
                                        startRight = 0;
                                        startLeft = new Rect(0, 0, endClip4, startHeight);
                                    }
                                    if (endClip != null) {
                                        endClip2 = endClip;
                                    } else {
                                        endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                    }
                                    if (startLeft.equals(endClip2)) {
                                        endClip = endClip2;
                                        startClip2 = startLeft;
                                        i4 = endWidth;
                                        i5 = startHeight;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        viewGroup4 = endParent;
                                        startTop = endLeft;
                                        startParentVals = true;
                                        endLeft = endClip4;
                                        endParent = null;
                                    } else {
                                        ViewCompat.setClipBounds(startClip3, startLeft);
                                        startTop = endHeight2;
                                        clipAnimator = null;
                                        startTop = endLeft;
                                        endLeft = endClip4;
                                        endClip3 = endHeight2;
                                        endClip = endClip2;
                                        view2 = startClip3;
                                        startClip2 = startLeft;
                                        startLeft = startTop;
                                        maxWidth = endParent;
                                        endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                        endWidth = endTop;
                                        startHeight = startParentVals;
                                        endRight = startParentVals;
                                        startParentVals = true;
                                        endTop = startParent;
                                        endHeight2 = /* anonymous class already generated */;
                                        endParent.addListener(endClip3);
                                    }
                                    endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                } else {
                                    startClip3 = view4;
                                    ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                    if (view != 2) {
                                        endHeight = endHeight2;
                                        i = endWidth;
                                        i2 = startHeight;
                                        view3 = startClip3;
                                        i3 = endParent;
                                        if (startLeft == endLeft3) {
                                            startClip3 = view3;
                                        } else if (startTop2 != endTop) {
                                            startClip3 = view3;
                                            endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                            startTop = endLeft3;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            i7 = endHeight;
                                            i5 = i2;
                                            i4 = i;
                                            endLeft = i3;
                                            startParentVals = true;
                                            endHeight = startRight;
                                            i = startTop2;
                                            i3 = startLeft;
                                            i2 = startBottom;
                                        } else {
                                            startClip3 = view3;
                                        }
                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                        startTop = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        endLeft = i3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i2 = startBottom;
                                    } else {
                                        if (endParent == endWidth) {
                                        }
                                        endHeight = endHeight2;
                                        i = endWidth;
                                        i2 = startHeight;
                                        view4 = view;
                                        endHeight2 = new ViewBounds(startClip3);
                                        i3 = endParent;
                                        topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                        startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                        view3 = startClip3;
                                        bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                        set = new AnimatorSet();
                                        set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                        endParent2 = set;
                                        set.addListener(/* anonymous class already generated */);
                                        topLeftPath2 = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        endHeight2 = endParent2;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        startClip3 = view3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i2 = startBottom;
                                        view3 = i3;
                                        i3 = startLeft;
                                    }
                                }
                                if (!(startClip3.getParent() instanceof ViewGroup)) {
                                    endLeft2 = (ViewGroup) startClip3.getParent();
                                    ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                    addListener(/* anonymous class already generated */);
                                }
                                return endHeight2;
                            }
                        }
                        if (startClip3 != null) {
                        }
                        if (view > null) {
                            startClip = startClip3;
                            endClip = endClip4;
                            if (changeBounds.mResizeClip) {
                                startClip3 = view4;
                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startRight, startBottom);
                                if (view != 2) {
                                    if (endParent == endWidth) {
                                    }
                                    endHeight = endHeight2;
                                    i = endWidth;
                                    i2 = startHeight;
                                    view4 = view;
                                    endHeight2 = new ViewBounds(startClip3);
                                    i3 = endParent;
                                    topLeftPath = getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop);
                                    startHeight = ObjectAnimatorUtils.ofPointF(endHeight2, TOP_LEFT_PROPERTY, topLeftPath);
                                    view3 = startClip3;
                                    bottomRightAnimator = ObjectAnimatorUtils.ofPointF(endHeight2, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                    set = new AnimatorSet();
                                    set.playTogether(new Animator[]{startHeight, bottomRightAnimator});
                                    endParent2 = set;
                                    set.addListener(/* anonymous class already generated */);
                                    topLeftPath2 = endLeft3;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    endHeight2 = endParent2;
                                    i7 = endHeight;
                                    i5 = i2;
                                    i4 = i;
                                    startClip3 = view3;
                                    startParentVals = true;
                                    endHeight = startRight;
                                    i = startTop2;
                                    i2 = startBottom;
                                    view3 = i3;
                                    i3 = startLeft;
                                } else {
                                    endHeight = endHeight2;
                                    i = endWidth;
                                    i2 = startHeight;
                                    view3 = startClip3;
                                    i3 = endParent;
                                    if (startLeft == endLeft3) {
                                        startClip3 = view3;
                                    } else if (startTop2 != endTop) {
                                        startClip3 = view3;
                                    } else {
                                        startClip3 = view3;
                                        endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) startParentVals, (float) startParent));
                                        startTop = endLeft3;
                                        i6 = endTop;
                                        map3 = startParentVals;
                                        i7 = endHeight;
                                        i5 = i2;
                                        i4 = i;
                                        endLeft = i3;
                                        startParentVals = true;
                                        endHeight = startRight;
                                        i = startTop2;
                                        i3 = startLeft;
                                        i2 = startBottom;
                                    }
                                    endHeight2 = ObjectAnimatorUtils.ofPointF(startClip3, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                    startTop = endLeft3;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    i7 = endHeight;
                                    i5 = i2;
                                    i4 = i;
                                    endLeft = i3;
                                    startParentVals = true;
                                    endHeight = startRight;
                                    i = startTop2;
                                    i3 = startLeft;
                                    i2 = startBottom;
                                }
                            } else {
                                i = endWidth;
                                i2 = startHeight;
                                startClip3 = view4;
                                endClip4 = endParent;
                                endParent = Math.max(endClip4, endWidth);
                                ViewUtils.setLeftTopRightBottom(startClip3, startLeft, startTop2, startLeft + endParent, startTop2 + Math.max(startHeight, endHeight2));
                                if (startLeft == endLeft3) {
                                    if (startTop2 != endTop) {
                                        endLeft = endLeft3;
                                        startBottom = 0;
                                        startTop = startTop2;
                                        i3 = startLeft;
                                        startTop2 = endClip;
                                        if (startClip != null) {
                                            startRight = 0;
                                            startLeft = new Rect(0, 0, endClip4, startHeight);
                                        } else {
                                            startRight = 0;
                                            startLeft = startClip;
                                        }
                                        if (endClip != null) {
                                            endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                        } else {
                                            endClip2 = endClip;
                                        }
                                        if (startLeft.equals(endClip2)) {
                                            ViewCompat.setClipBounds(startClip3, startLeft);
                                            startTop = endHeight2;
                                            clipAnimator = null;
                                            startTop = endLeft;
                                            endLeft = endClip4;
                                            endClip3 = endHeight2;
                                            endClip = endClip2;
                                            view2 = startClip3;
                                            startClip2 = startLeft;
                                            startLeft = startTop;
                                            maxWidth = endParent;
                                            endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                            endWidth = endTop;
                                            startHeight = startParentVals;
                                            endRight = startParentVals;
                                            startParentVals = true;
                                            endTop = startParent;
                                            endHeight2 = /* anonymous class already generated */;
                                            endParent.addListener(endClip3);
                                        } else {
                                            endClip = endClip2;
                                            startClip2 = startLeft;
                                            i4 = endWidth;
                                            i5 = startHeight;
                                            i6 = endTop;
                                            map3 = startParentVals;
                                            viewGroup4 = endParent;
                                            startTop = endLeft;
                                            startParentVals = true;
                                            endLeft = endClip4;
                                            endParent = null;
                                        }
                                        endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                                    }
                                }
                                positionAnimator = null;
                                startTop = startTop2;
                                endLeft = endLeft3;
                                startBottom = ObjectAnimatorUtils.ofPointF(startClip3, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft, (float) startTop2, (float) endLeft3, (float) endTop));
                                startTop2 = endClip;
                                if (startClip != null) {
                                    startRight = 0;
                                    startLeft = startClip;
                                } else {
                                    startRight = 0;
                                    startLeft = new Rect(0, 0, endClip4, startHeight);
                                }
                                if (endClip != null) {
                                    endClip2 = endClip;
                                } else {
                                    endClip2 = new Rect(startRight, startRight, endWidth, endHeight2);
                                }
                                if (startLeft.equals(endClip2)) {
                                    endClip = endClip2;
                                    startClip2 = startLeft;
                                    i4 = endWidth;
                                    i5 = startHeight;
                                    i6 = endTop;
                                    map3 = startParentVals;
                                    viewGroup4 = endParent;
                                    startTop = endLeft;
                                    startParentVals = true;
                                    endLeft = endClip4;
                                    endParent = null;
                                } else {
                                    ViewCompat.setClipBounds(startClip3, startLeft);
                                    startTop = endHeight2;
                                    clipAnimator = null;
                                    startTop = endLeft;
                                    endLeft = endClip4;
                                    endClip3 = endHeight2;
                                    endClip = endClip2;
                                    view2 = startClip3;
                                    startClip2 = startLeft;
                                    startLeft = startTop;
                                    maxWidth = endParent;
                                    endParent = ObjectAnimator.ofObject(startClip3, "clipBounds", sRectEvaluator, new Object[]{startLeft, endClip2});
                                    endWidth = endTop;
                                    startHeight = startParentVals;
                                    endRight = startParentVals;
                                    startParentVals = true;
                                    endTop = startParent;
                                    endHeight2 = /* anonymous class already generated */;
                                    endParent.addListener(endClip3);
                                }
                                endHeight2 = TransitionUtils.mergeAnimators(startBottom, endParent);
                            }
                            if (!(startClip3.getParent() instanceof ViewGroup)) {
                                endLeft2 = (ViewGroup) startClip3.getParent();
                                ViewGroupUtils.suppressLayout(endLeft2, startParentVals);
                                addListener(/* anonymous class already generated */);
                            }
                            return endHeight2;
                        }
                        startTop = endLeft3;
                        endHeight = startRight;
                        i = startTop2;
                        i3 = startLeft;
                        i4 = endWidth;
                        i5 = startHeight;
                        i6 = endTop;
                        startClip = startClip3;
                        endClip = endClip4;
                        map3 = startParentVals;
                        i2 = startBottom;
                        viewGroup5 = endParent;
                        startHeight = startValues;
                        transitionValues3 = endValues;
                    }
                } else {
                    map2 = endParentVals;
                    viewGroup2 = startParent;
                    viewGroup3 = endParent;
                    View view5 = view;
                    TransitionValues transitionValues4 = startValues;
                    endTop = ((Integer) transitionValues4.values.get(PROPNAME_WINDOW_X)).intValue();
                    int startY = ((Integer) transitionValues4.values.get(PROPNAME_WINDOW_Y)).intValue();
                    transitionValues3 = endValues;
                    startParent = ((Integer) transitionValues3.values.get(PROPNAME_WINDOW_X)).intValue();
                    endParent = ((Integer) transitionValues3.values.get(PROPNAME_WINDOW_Y)).intValue();
                    if (endTop == startParent) {
                        if (startY != endParent) {
                        }
                    }
                    sceneRoot.getLocationInWindow(changeBounds.mTempLocation);
                    Bitmap bitmap = Bitmap.createBitmap(view5.getWidth(), view5.getHeight(), Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    view5.draw(canvas);
                    final Drawable bitmapDrawable = new BitmapDrawable(bitmap);
                    float transitionAlpha = ViewUtils.getTransitionAlpha(view5);
                    ViewUtils.setTransitionAlpha(view5, 0.0f);
                    ViewUtils.getOverlay(sceneRoot).add(bitmapDrawable);
                    PathMotion pathMotion = getPathMotion();
                    int[] iArr = changeBounds.mTempLocation;
                    PropertyValuesHolder origin = PropertyValuesHolderUtils.ofPointF(DRAWABLE_ORIGIN_PROPERTY, pathMotion.getPath((float) (endTop - iArr[0]), (float) (startY - iArr[1]), (float) (startParent - iArr[0]), (float) (endParent - iArr[1])));
                    final ViewGroup viewGroup6 = sceneRoot;
                    Drawable drawable = bitmapDrawable;
                    AnonymousClass10 anonymousClass10 = r0;
                    final View view6 = view5;
                    ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(bitmapDrawable, new PropertyValuesHolder[]{origin});
                    final float f = transitionAlpha;
                    AnonymousClass10 anonymousClass102 = new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            ViewUtils.getOverlay(viewGroup6).remove(bitmapDrawable);
                            ViewUtils.setTransitionAlpha(view6, f);
                        }
                    };
                    anim.addListener(anonymousClass10);
                    return anim;
                }
                return null;
            }
            return null;
        }
        return animator;
    }
}
