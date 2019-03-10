package android.support.transition;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.transition.Transition.EpicenterCallback;
import android.support.transition.Transition.TransitionListener;
import android.support.v4.app.FragmentTransitionImpl;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({Scope.LIBRARY_GROUP})
public class FragmentTransitionSupport extends FragmentTransitionImpl {
    public boolean canHandle(Object transition) {
        return transition instanceof Transition;
    }

    public Object cloneTransition(Object transition) {
        if (transition != null) {
            return ((Transition) transition).clone();
        }
        return null;
    }

    public Object wrapTransitionInSet(Object transition) {
        if (transition == null) {
            return null;
        }
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition((Transition) transition);
        return transitionSet;
    }

    public void setSharedElementTargets(Object transitionObj, View nonExistentView, ArrayList<View> sharedViews) {
        TransitionSet transition = (TransitionSet) transitionObj;
        List<View> views = transition.getTargets();
        views.clear();
        int count = sharedViews.size();
        for (int i = 0; i < count; i++) {
            FragmentTransitionImpl.bfsAddViewChildren(views, (View) sharedViews.get(i));
        }
        views.add(nonExistentView);
        sharedViews.add(nonExistentView);
        addTargets(transition, sharedViews);
    }

    public void setEpicenter(Object transitionObj, View view) {
        if (view != null) {
            Transition transition = (Transition) transitionObj;
            final Rect epicenter = new Rect();
            getBoundsOnScreen(view, epicenter);
            transition.setEpicenterCallback(new EpicenterCallback() {
                public Rect onGetEpicenter(@NonNull Transition transition) {
                    return epicenter;
                }
            });
        }
    }

    public void addTargets(Object transitionObj, ArrayList<View> views) {
        Transition transition = (Transition) transitionObj;
        if (transition != null) {
            int numTransitions;
            int i;
            if (transition instanceof TransitionSet) {
                TransitionSet set = (TransitionSet) transition;
                numTransitions = set.getTransitionCount();
                for (i = 0; i < numTransitions; i++) {
                    addTargets(set.getTransitionAt(i), views);
                }
            } else if (!hasSimpleTarget(transition)) {
                if (FragmentTransitionImpl.isNullOrEmpty(transition.getTargets())) {
                    numTransitions = views.size();
                    for (i = 0; i < numTransitions; i++) {
                        transition.addTarget((View) views.get(i));
                    }
                }
            }
        }
    }

    private static boolean hasSimpleTarget(Transition transition) {
        if (FragmentTransitionImpl.isNullOrEmpty(transition.getTargetIds())) {
            if (FragmentTransitionImpl.isNullOrEmpty(transition.getTargetNames())) {
                if (FragmentTransitionImpl.isNullOrEmpty(transition.getTargetTypes())) {
                    return false;
                }
            }
        }
        return true;
    }

    public Object mergeTransitionsTogether(Object transition1, Object transition2, Object transition3) {
        TransitionSet transitionSet = new TransitionSet();
        if (transition1 != null) {
            transitionSet.addTransition((Transition) transition1);
        }
        if (transition2 != null) {
            transitionSet.addTransition((Transition) transition2);
        }
        if (transition3 != null) {
            transitionSet.addTransition((Transition) transition3);
        }
        return transitionSet;
    }

    public void scheduleHideFragmentView(Object exitTransitionObj, final View fragmentView, final ArrayList<View> exitingViews) {
        ((Transition) exitTransitionObj).addListener(new TransitionListener() {
            public void onTransitionStart(@NonNull Transition transition) {
            }

            public void onTransitionEnd(@NonNull Transition transition) {
                transition.removeListener(this);
                fragmentView.setVisibility(8);
                int numViews = exitingViews.size();
                for (int i = 0; i < numViews; i++) {
                    ((View) exitingViews.get(i)).setVisibility(0);
                }
            }

            public void onTransitionCancel(@NonNull Transition transition) {
            }

            public void onTransitionPause(@NonNull Transition transition) {
            }

            public void onTransitionResume(@NonNull Transition transition) {
            }
        });
    }

    public Object mergeTransitionsInSequence(Object exitTransitionObj, Object enterTransitionObj, Object sharedElementTransitionObj) {
        Transition staggered = null;
        Transition exitTransition = (Transition) exitTransitionObj;
        Transition enterTransition = (Transition) enterTransitionObj;
        Transition sharedElementTransition = (Transition) sharedElementTransitionObj;
        if (exitTransition != null && enterTransition != null) {
            staggered = new TransitionSet().addTransition(exitTransition).addTransition(enterTransition).setOrdering(1);
        } else if (exitTransition != null) {
            staggered = exitTransition;
        } else if (enterTransition != null) {
            staggered = enterTransition;
        }
        if (sharedElementTransition == null) {
            return staggered;
        }
        TransitionSet together = new TransitionSet();
        if (staggered != null) {
            together.addTransition(staggered);
        }
        together.addTransition(sharedElementTransition);
        return together;
    }

    public void beginDelayedTransition(ViewGroup sceneRoot, Object transition) {
        TransitionManager.beginDelayedTransition(sceneRoot, (Transition) transition);
    }

    public void scheduleRemoveTargets(Object overallTransitionObj, Object enterTransition, ArrayList<View> enteringViews, Object exitTransition, ArrayList<View> exitingViews, Object sharedElementTransition, ArrayList<View> sharedElementsIn) {
        final Object obj = enterTransition;
        final ArrayList<View> arrayList = enteringViews;
        final Object obj2 = exitTransition;
        final ArrayList<View> arrayList2 = exitingViews;
        final Object obj3 = sharedElementTransition;
        final ArrayList<View> arrayList3 = sharedElementsIn;
        ((Transition) overallTransitionObj).addListener(new TransitionListener() {
            public void onTransitionStart(@NonNull Transition transition) {
                Object obj = obj;
                if (obj != null) {
                    FragmentTransitionSupport.this.replaceTargets(obj, arrayList, null);
                }
                obj = obj2;
                if (obj != null) {
                    FragmentTransitionSupport.this.replaceTargets(obj, arrayList2, null);
                }
                obj = obj3;
                if (obj != null) {
                    FragmentTransitionSupport.this.replaceTargets(obj, arrayList3, null);
                }
            }

            public void onTransitionEnd(@NonNull Transition transition) {
            }

            public void onTransitionCancel(@NonNull Transition transition) {
            }

            public void onTransitionPause(@NonNull Transition transition) {
            }

            public void onTransitionResume(@NonNull Transition transition) {
            }
        });
    }

    public void swapSharedElementTargets(Object sharedElementTransitionObj, ArrayList<View> sharedElementsOut, ArrayList<View> sharedElementsIn) {
        TransitionSet sharedElementTransition = (TransitionSet) sharedElementTransitionObj;
        if (sharedElementTransition != null) {
            sharedElementTransition.getTargets().clear();
            sharedElementTransition.getTargets().addAll(sharedElementsIn);
            replaceTargets(sharedElementTransition, sharedElementsOut, sharedElementsIn);
        }
    }

    public void replaceTargets(Object transitionObj, ArrayList<View> oldTargets, ArrayList<View> newTargets) {
        Transition transition = (Transition) transitionObj;
        int numTransitions;
        int i;
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            numTransitions = set.getTransitionCount();
            for (i = 0; i < numTransitions; i++) {
                replaceTargets(set.getTransitionAt(i), oldTargets, newTargets);
            }
        } else if (!hasSimpleTarget(transition)) {
            List<View> targets = transition.getTargets();
            if (targets.size() != oldTargets.size()) {
                return;
            }
            if (targets.containsAll(oldTargets)) {
                numTransitions = newTargets == null ? 0 : newTargets.size();
                for (i = 0; i < numTransitions; i++) {
                    transition.addTarget((View) newTargets.get(i));
                }
                for (i = oldTargets.size() - 1; i >= 0; i--) {
                    transition.removeTarget((View) oldTargets.get(i));
                }
            }
        }
    }

    public void addTarget(Object transitionObj, View view) {
        if (transitionObj != null) {
            ((Transition) transitionObj).addTarget(view);
        }
    }

    public void removeTarget(Object transitionObj, View view) {
        if (transitionObj != null) {
            ((Transition) transitionObj).removeTarget(view);
        }
    }

    public void setEpicenter(Object transitionObj, final Rect epicenter) {
        if (transitionObj != null) {
            ((Transition) transitionObj).setEpicenterCallback(new EpicenterCallback() {
                public Rect onGetEpicenter(@NonNull Transition transition) {
                    Rect rect = epicenter;
                    if (rect != null) {
                        if (!rect.isEmpty()) {
                            return epicenter;
                        }
                    }
                    return null;
                }
            });
        }
    }
}
