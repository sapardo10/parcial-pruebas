package de.danoeh.antennapod.core.storage;

class PodDBAdapter$SingletonHolder {
    private static final PodDBAdapter dbAdapter = new PodDBAdapter(null);
    private static final PodDBAdapter$PodDBHelper dbHelper = new PodDBAdapter$PodDBHelper(PodDBAdapter.access$000(), PodDBAdapter.DATABASE_NAME, null);

    private PodDBAdapter$SingletonHolder() {
    }
}
