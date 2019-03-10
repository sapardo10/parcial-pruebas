package com.robotium.solo;

import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RobotiumUtils {
    public static <T extends View> ArrayList<T> removeInvisibleViews(Iterable<T> viewList) {
        ArrayList<T> tmpViewList = new ArrayList();
        for (T view : viewList) {
            if (view != null && view.isShown()) {
                tmpViewList.add(view);
            }
        }
        return tmpViewList;
    }

    public static <T> ArrayList<T> filterViews(Class<T> classToFilterBy, Iterable<?> viewList) {
        ArrayList<T> filteredViews = new ArrayList();
        for (Object view : viewList) {
            if (view != null && classToFilterBy.isAssignableFrom(view.getClass())) {
                filteredViews.add(classToFilterBy.cast(view));
            }
        }
        return filteredViews;
    }

    public static ArrayList<View> filterViewsToSet(Class<View>[] classSet, Iterable<View> viewList) {
        ArrayList<View> filteredViews = new ArrayList();
        for (View view : viewList) {
            if (view != null) {
                for (Class<View> filter : classSet) {
                    if (filter.isAssignableFrom(view.getClass())) {
                        filteredViews.add(view);
                        break;
                    }
                }
            }
        }
        return filteredViews;
    }

    public static void sortViewsByLocationOnScreen(List<? extends View> views) {
        Collections.sort(views, new ViewLocationComparator());
    }

    public static void sortViewsByLocationOnScreen(List<? extends View> views, boolean yAxisFirst) {
        Collections.sort(views, new ViewLocationComparator(yAxisFirst));
    }

    public static int getNumberOfMatches(String regex, TextView view, Set<TextView> uniqueTextViews) {
        if (view == null) {
            return uniqueTextViews.size();
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            pattern = Pattern.compile(regex, 16);
        }
        if (pattern.matcher(view.getText().toString()).find()) {
            uniqueTextViews.add(view);
        }
        if (view.getError() != null) {
            if (pattern.matcher(view.getError().toString()).find()) {
                uniqueTextViews.add(view);
            }
        }
        if (view.getText().toString().equals("") && view.getHint() != null) {
            if (pattern.matcher(view.getHint().toString()).find()) {
                uniqueTextViews.add(view);
            }
        }
        return uniqueTextViews.size();
    }

    public static <T extends TextView> List<T> filterViewsByText(Iterable<T> views, String regex) {
        return filterViewsByText((Iterable) views, Pattern.compile(regex));
    }

    public static <T extends TextView> List<T> filterViewsByText(Iterable<T> views, Pattern regex) {
        ArrayList<T> filteredViews = new ArrayList();
        for (T view : views) {
            if (view != null && regex.matcher(view.getText()).matches()) {
                filteredViews.add(view);
            }
        }
        return filteredViews;
    }
}
