package android.support.wearable.watchface.decomposition;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchFaceDecomposition implements Parcelable {
    public static final Creator<WatchFaceDecomposition> CREATOR = new C04821();
    private static final String FIELD_COMPLICATIONS = "complications";
    private static final String FIELD_FONTS = "fonts";
    private static final String FIELD_IMAGES = "images";
    private static final String FIELD_NUMBERS = "numbers";
    public static final int MAX_COMPONENT_ID = 100000;
    private final List<ComplicationComponent> complications;
    private final List<FontComponent> fonts;
    private final List<ImageComponent> images;
    private final List<NumberComponent> numbers;

    /* renamed from: android.support.wearable.watchface.decomposition.WatchFaceDecomposition$1 */
    class C04821 implements Creator<WatchFaceDecomposition> {
        C04821() {
        }

        public WatchFaceDecomposition createFromParcel(Parcel source) {
            return new WatchFaceDecomposition(source);
        }

        public WatchFaceDecomposition[] newArray(int size) {
            return new WatchFaceDecomposition[size];
        }
    }

    public static class Builder {
        private final List<ComplicationComponent> complications = new ArrayList();
        private final List<FontComponent> fonts = new ArrayList();
        private final List<ImageComponent> images = new ArrayList();
        private final List<NumberComponent> numbers = new ArrayList();

        public Builder addImageComponents(ImageComponent... imageComponents) {
            Collections.addAll(this.images, imageComponents);
            return this;
        }

        public Builder addNumberComponents(NumberComponent... numberComponents) {
            Collections.addAll(this.numbers, numberComponents);
            return this;
        }

        public Builder addFontComponents(FontComponent... fontComponents) {
            Collections.addAll(this.fonts, fontComponents);
            return this;
        }

        public Builder addComplicationComponents(ComplicationComponent... complicationComponents) {
            Collections.addAll(this.complications, complicationComponents);
            return this;
        }

        public WatchFaceDecomposition build() {
            if (areAllComponentIdsUnique(this.images, this.numbers, this.fonts, this.complications)) {
                checkComponentIds(this.images, this.numbers, this.fonts, this.complications);
                return new WatchFaceDecomposition(this.images, this.numbers, this.fonts, this.complications);
            }
            throw new IllegalStateException("Duplicate component ids found.");
        }

        private boolean areAllComponentIdsUnique(List<? extends Component>... componentLists) {
            SparseBooleanArray ids = new SparseBooleanArray();
            for (List<? extends Component> componentList : componentLists) {
                if (!allNewIds(componentList, ids)) {
                    return false;
                }
            }
            return true;
        }

        private void checkComponentIds(List<? extends Component>... componentLists) {
            int componentId;
            for (List<? extends Component> componentList : componentLists) {
                for (Component component : componentList) {
                    if (component.getComponentId() > 100000) {
                        componentId = component.getComponentId();
                        StringBuilder stringBuilder = new StringBuilder(38);
                        stringBuilder.append("Component id ");
                        stringBuilder.append(componentId);
                        stringBuilder.append(" above maximum");
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
            }
        }

        private <T extends Component> boolean allNewIds(List<T> componentList, SparseBooleanArray currentIds) {
            for (T component : componentList) {
                int id = component.getComponentId();
                if (currentIds.get(id)) {
                    return false;
                }
                currentIds.put(id, true);
            }
            return true;
        }
    }

    interface Component {
        int getComponentId();
    }

    public interface DrawnComponent extends Component {
        int getZOrder();
    }

    private WatchFaceDecomposition(List<ImageComponent> images, List<NumberComponent> numbers, List<FontComponent> fonts, List<ComplicationComponent> complications) {
        this.images = Collections.unmodifiableList(images);
        this.numbers = Collections.unmodifiableList(numbers);
        this.fonts = Collections.unmodifiableList(fonts);
        this.complications = Collections.unmodifiableList(complications);
    }

    private WatchFaceDecomposition(Parcel in) {
        Bundle fields = in.readBundle(getClass().getClassLoader());
        List<ImageComponent> images = fields.getParcelableArrayList(FIELD_IMAGES);
        List<NumberComponent> numbers = fields.getParcelableArrayList(FIELD_NUMBERS);
        List<FontComponent> fonts = fields.getParcelableArrayList(FIELD_FONTS);
        List<ComplicationComponent> complications = fields.getParcelableArrayList(FIELD_COMPLICATIONS);
        this.images = images == null ? Collections.emptyList() : images;
        this.numbers = numbers == null ? Collections.emptyList() : numbers;
        this.fonts = fonts == null ? Collections.emptyList() : fonts;
        this.complications = complications == null ? Collections.emptyList() : complications;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle fields = new Bundle();
        fields.putParcelableArrayList(FIELD_IMAGES, new ArrayList(this.images));
        fields.putParcelableArrayList(FIELD_NUMBERS, new ArrayList(this.numbers));
        fields.putParcelableArrayList(FIELD_FONTS, new ArrayList(this.fonts));
        fields.putParcelableArrayList(FIELD_COMPLICATIONS, new ArrayList(this.complications));
        dest.writeBundle(fields);
    }

    public List<ImageComponent> getImageComponents() {
        return this.images;
    }

    public List<NumberComponent> getNumberComponents() {
        return this.numbers;
    }

    public List<FontComponent> getFontComponents() {
        return this.fonts;
    }

    public List<ComplicationComponent> getComplicationComponents() {
        return this.complications;
    }
}
