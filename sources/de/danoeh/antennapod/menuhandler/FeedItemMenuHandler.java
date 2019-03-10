package de.danoeh.antennapod.menuhandler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Action;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Builder;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.debug.R;

public class FeedItemMenuHandler {
    private static final String TAG = "FeedItemMenuHandler";

    public interface MenuInterface {
        void setItemVisibility(int i, boolean z);
    }

    private FeedItemMenuHandler() {
    }

    public static boolean onPrepareMenu(MenuInterface mi, FeedItem selectedItem, boolean showExtendedMenu, @Nullable LongList queueAccess) {
        if (selectedItem == null) {
            return false;
        }
        boolean fileDownloaded;
        boolean isFavorite;
        boolean hasMedia = selectedItem.getMedia() != null;
        boolean isPlaying = hasMedia && selectedItem.getState() == State.PLAYING;
        if (!isPlaying) {
            mi.setItemVisibility(R.id.skip_episode_item, false);
        }
        boolean isInQueue = selectedItem.isTagged(FeedItem.TAG_QUEUE);
        if (!(queueAccess == null || queueAccess.size() == 0)) {
            if (queueAccess.get(0) != selectedItem.getId()) {
                if (!(queueAccess == null || queueAccess.size() == 0)) {
                    if (queueAccess.get(queueAccess.size() - 1) == selectedItem.getId()) {
                        if (isInQueue) {
                            mi.setItemVisibility(R.id.remove_from_queue_item, false);
                        }
                        if (!isInQueue) {
                            if (selectedItem.getMedia() != null) {
                                if (showExtendedMenu) {
                                    if (ShareUtils.hasLinkToShare(selectedItem)) {
                                        if (showExtendedMenu && hasMedia) {
                                            if (selectedItem.getMedia().getDownload_url() == null) {
                                                if (hasMedia) {
                                                    if (selectedItem.getMedia().getPosition() > 0) {
                                                        fileDownloaded = hasMedia && selectedItem.getMedia().fileExists();
                                                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                                        if (selectedItem.isPlayed()) {
                                                            mi.setItemVisibility(R.id.mark_unread_item, false);
                                                        } else {
                                                            mi.setItemVisibility(R.id.mark_read_item, false);
                                                        }
                                                        if (selectedItem.getMedia() != null) {
                                                            if (selectedItem.getMedia().getPosition() != 0) {
                                                                if (UserPreferences.isEnableAutodownload()) {
                                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                                } else if (selectedItem.getAutoDownload()) {
                                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                                } else {
                                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                                }
                                                                if (selectedItem.getPaymentLink() != null) {
                                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                                        return true;
                                                                    }
                                                                }
                                                                mi.setItemVisibility(R.id.support_item, false);
                                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                                return true;
                                                            }
                                                        }
                                                        mi.setItemVisibility(R.id.reset_position, false);
                                                        if (UserPreferences.isEnableAutodownload()) {
                                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                        } else if (selectedItem.getAutoDownload()) {
                                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                        } else {
                                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                                        }
                                                        if (selectedItem.getPaymentLink() != null) {
                                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                                return true;
                                                            }
                                                        }
                                                        mi.setItemVisibility(R.id.support_item, false);
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                                if (!hasMedia) {
                                                }
                                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                                if (selectedItem.isPlayed()) {
                                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                                }
                                                if (selectedItem.getMedia() != null) {
                                                    if (selectedItem.getMedia().getPosition() != 0) {
                                                        if (UserPreferences.isEnableAutodownload()) {
                                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                        } else if (selectedItem.getAutoDownload()) {
                                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                                        } else {
                                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                        }
                                                        if (selectedItem.getPaymentLink() != null) {
                                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                                return true;
                                                            }
                                                        }
                                                        mi.setItemVisibility(R.id.support_item, false);
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.reset_position, false);
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.share_download_url_item, false);
                                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                        if (hasMedia) {
                                            if (selectedItem.getMedia().getPosition() > 0) {
                                                if (hasMedia) {
                                                }
                                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                                if (selectedItem.isPlayed()) {
                                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                                }
                                                if (selectedItem.getMedia() != null) {
                                                    if (selectedItem.getMedia().getPosition() != 0) {
                                                        if (UserPreferences.isEnableAutodownload()) {
                                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                        } else if (selectedItem.getAutoDownload()) {
                                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                                        } else {
                                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                        }
                                                        if (selectedItem.getPaymentLink() != null) {
                                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                                return true;
                                                            }
                                                        }
                                                        mi.setItemVisibility(R.id.support_item, false);
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.reset_position, false);
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                        if (hasMedia) {
                                        }
                                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                        if (selectedItem.isPlayed()) {
                                            mi.setItemVisibility(R.id.mark_unread_item, false);
                                        } else {
                                            mi.setItemVisibility(R.id.mark_read_item, false);
                                        }
                                        if (selectedItem.getMedia() != null) {
                                            if (selectedItem.getMedia().getPosition() != 0) {
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.reset_position, false);
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.visit_website_item, false);
                                mi.setItemVisibility(R.id.share_link_item, false);
                                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                if (selectedItem.getMedia().getDownload_url() == null) {
                                    if (hasMedia) {
                                        if (selectedItem.getMedia().getPosition() > 0) {
                                            if (hasMedia) {
                                            }
                                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                            if (selectedItem.isPlayed()) {
                                                mi.setItemVisibility(R.id.mark_read_item, false);
                                            } else {
                                                mi.setItemVisibility(R.id.mark_unread_item, false);
                                            }
                                            if (selectedItem.getMedia() != null) {
                                                if (selectedItem.getMedia().getPosition() != 0) {
                                                    if (UserPreferences.isEnableAutodownload()) {
                                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                    } else if (selectedItem.getAutoDownload()) {
                                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    } else {
                                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                    }
                                                    if (selectedItem.getPaymentLink() != null) {
                                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                            return true;
                                                        }
                                                    }
                                                    mi.setItemVisibility(R.id.support_item, false);
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.reset_position, false);
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                                mi.setItemVisibility(R.id.share_download_url_item, false);
                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                if (hasMedia) {
                                    if (selectedItem.getMedia().getPosition() > 0) {
                                        if (hasMedia) {
                                        }
                                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                        if (selectedItem.isPlayed()) {
                                            mi.setItemVisibility(R.id.mark_read_item, false);
                                        } else {
                                            mi.setItemVisibility(R.id.mark_unread_item, false);
                                        }
                                        if (selectedItem.getMedia() != null) {
                                            if (selectedItem.getMedia().getPosition() != 0) {
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.reset_position, false);
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.add_to_queue_item, false);
                        if (showExtendedMenu) {
                            if (ShareUtils.hasLinkToShare(selectedItem)) {
                                if (selectedItem.getMedia().getDownload_url() == null) {
                                    mi.setItemVisibility(R.id.share_download_url_item, false);
                                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                    if (hasMedia) {
                                        if (selectedItem.getMedia().getPosition() > 0) {
                                            if (hasMedia) {
                                            }
                                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                            if (selectedItem.isPlayed()) {
                                                mi.setItemVisibility(R.id.mark_read_item, false);
                                            } else {
                                                mi.setItemVisibility(R.id.mark_unread_item, false);
                                            }
                                            if (selectedItem.getMedia() != null) {
                                                if (selectedItem.getMedia().getPosition() != 0) {
                                                    if (UserPreferences.isEnableAutodownload()) {
                                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                    } else if (selectedItem.getAutoDownload()) {
                                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    } else {
                                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                    }
                                                    if (selectedItem.getPaymentLink() != null) {
                                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                            return true;
                                                        }
                                                    }
                                                    mi.setItemVisibility(R.id.support_item, false);
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.reset_position, false);
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                                if (hasMedia) {
                                    if (selectedItem.getMedia().getPosition() > 0) {
                                        if (hasMedia) {
                                        }
                                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                        if (selectedItem.isPlayed()) {
                                            mi.setItemVisibility(R.id.mark_read_item, false);
                                        } else {
                                            mi.setItemVisibility(R.id.mark_unread_item, false);
                                        }
                                        if (selectedItem.getMedia() != null) {
                                            if (selectedItem.getMedia().getPosition() != 0) {
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.reset_position, false);
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.visit_website_item, false);
                        mi.setItemVisibility(R.id.share_link_item, false);
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        if (selectedItem.getMedia().getDownload_url() == null) {
                            if (hasMedia) {
                                if (selectedItem.getMedia().getPosition() > 0) {
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.share_link_with_position_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                        mi.setItemVisibility(R.id.share_download_url_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                            if (selectedItem.getMedia().getPosition() > 0) {
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.move_to_bottom_item, false);
                if (isInQueue) {
                    mi.setItemVisibility(R.id.remove_from_queue_item, false);
                }
                if (isInQueue) {
                    if (selectedItem.getMedia() != null) {
                        if (showExtendedMenu) {
                            if (ShareUtils.hasLinkToShare(selectedItem)) {
                                if (selectedItem.getMedia().getDownload_url() == null) {
                                    mi.setItemVisibility(R.id.share_download_url_item, false);
                                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                    if (hasMedia) {
                                        if (selectedItem.getMedia().getPosition() > 0) {
                                            if (hasMedia) {
                                            }
                                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                            if (selectedItem.isPlayed()) {
                                                mi.setItemVisibility(R.id.mark_read_item, false);
                                            } else {
                                                mi.setItemVisibility(R.id.mark_unread_item, false);
                                            }
                                            if (selectedItem.getMedia() != null) {
                                                if (selectedItem.getMedia().getPosition() != 0) {
                                                    if (UserPreferences.isEnableAutodownload()) {
                                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                    } else if (selectedItem.getAutoDownload()) {
                                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    } else {
                                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                    }
                                                    if (selectedItem.getPaymentLink() != null) {
                                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                            return true;
                                                        }
                                                    }
                                                    mi.setItemVisibility(R.id.support_item, false);
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.reset_position, false);
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                                if (hasMedia) {
                                    if (selectedItem.getMedia().getPosition() > 0) {
                                        if (hasMedia) {
                                        }
                                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                        if (selectedItem.isPlayed()) {
                                            mi.setItemVisibility(R.id.mark_read_item, false);
                                        } else {
                                            mi.setItemVisibility(R.id.mark_unread_item, false);
                                        }
                                        if (selectedItem.getMedia() != null) {
                                            if (selectedItem.getMedia().getPosition() != 0) {
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.reset_position, false);
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.visit_website_item, false);
                        mi.setItemVisibility(R.id.share_link_item, false);
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        if (selectedItem.getMedia().getDownload_url() == null) {
                            if (hasMedia) {
                                if (selectedItem.getMedia().getPosition() > 0) {
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.share_link_with_position_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                        mi.setItemVisibility(R.id.share_download_url_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                            if (selectedItem.getMedia().getPosition() > 0) {
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.add_to_queue_item, false);
                if (showExtendedMenu) {
                    if (ShareUtils.hasLinkToShare(selectedItem)) {
                        if (selectedItem.getMedia().getDownload_url() == null) {
                            mi.setItemVisibility(R.id.share_download_url_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                                if (selectedItem.getMedia().getPosition() > 0) {
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.share_link_with_position_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                        if (hasMedia) {
                            if (selectedItem.getMedia().getPosition() > 0) {
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.visit_website_item, false);
                mi.setItemVisibility(R.id.share_link_item, false);
                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                if (selectedItem.getMedia().getDownload_url() == null) {
                    if (hasMedia) {
                        if (selectedItem.getMedia().getPosition() > 0) {
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
                mi.setItemVisibility(R.id.share_download_url_item, false);
                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                if (hasMedia) {
                    if (selectedItem.getMedia().getPosition() > 0) {
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                if (hasMedia) {
                }
                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                if (selectedItem.isPlayed()) {
                    mi.setItemVisibility(R.id.mark_unread_item, false);
                } else {
                    mi.setItemVisibility(R.id.mark_read_item, false);
                }
                if (selectedItem.getMedia() != null) {
                    if (selectedItem.getMedia().getPosition() != 0) {
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.reset_position, false);
                if (UserPreferences.isEnableAutodownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else if (selectedItem.getAutoDownload()) {
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                }
                if (selectedItem.getPaymentLink() != null) {
                    if (selectedItem.getFlattrStatus().flattrable()) {
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.support_item, false);
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
        }
        mi.setItemVisibility(R.id.move_to_top_item, false);
        if (queueAccess.get(queueAccess.size() - 1) == selectedItem.getId()) {
            if (isInQueue) {
                mi.setItemVisibility(R.id.remove_from_queue_item, false);
            }
            if (isInQueue) {
                if (selectedItem.getMedia() != null) {
                    if (showExtendedMenu) {
                        if (ShareUtils.hasLinkToShare(selectedItem)) {
                            if (selectedItem.getMedia().getDownload_url() == null) {
                                mi.setItemVisibility(R.id.share_download_url_item, false);
                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                if (hasMedia) {
                                    if (selectedItem.getMedia().getPosition() > 0) {
                                        if (hasMedia) {
                                        }
                                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                        if (selectedItem.isPlayed()) {
                                            mi.setItemVisibility(R.id.mark_read_item, false);
                                        } else {
                                            mi.setItemVisibility(R.id.mark_unread_item, false);
                                        }
                                        if (selectedItem.getMedia() != null) {
                                            if (selectedItem.getMedia().getPosition() != 0) {
                                                if (UserPreferences.isEnableAutodownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                } else if (selectedItem.getAutoDownload()) {
                                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                                } else {
                                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                                }
                                                if (selectedItem.getPaymentLink() != null) {
                                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                        return true;
                                                    }
                                                }
                                                mi.setItemVisibility(R.id.support_item, false);
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.reset_position, false);
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                            if (hasMedia) {
                                if (selectedItem.getMedia().getPosition() > 0) {
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.share_link_with_position_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.visit_website_item, false);
                    mi.setItemVisibility(R.id.share_link_item, false);
                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                    if (selectedItem.getMedia().getDownload_url() == null) {
                        if (hasMedia) {
                            if (selectedItem.getMedia().getPosition() > 0) {
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                    mi.setItemVisibility(R.id.share_download_url_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                        if (selectedItem.getMedia().getPosition() > 0) {
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.add_to_queue_item, false);
            if (showExtendedMenu) {
                if (ShareUtils.hasLinkToShare(selectedItem)) {
                    if (selectedItem.getMedia().getDownload_url() == null) {
                        mi.setItemVisibility(R.id.share_download_url_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                            if (selectedItem.getMedia().getPosition() > 0) {
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                    if (hasMedia) {
                        if (selectedItem.getMedia().getPosition() > 0) {
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.visit_website_item, false);
            mi.setItemVisibility(R.id.share_link_item, false);
            mi.setItemVisibility(R.id.share_link_with_position_item, false);
            if (selectedItem.getMedia().getDownload_url() == null) {
                if (hasMedia) {
                    if (selectedItem.getMedia().getPosition() > 0) {
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                if (hasMedia) {
                }
                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                if (selectedItem.isPlayed()) {
                    mi.setItemVisibility(R.id.mark_unread_item, false);
                } else {
                    mi.setItemVisibility(R.id.mark_read_item, false);
                }
                if (selectedItem.getMedia() != null) {
                    if (selectedItem.getMedia().getPosition() != 0) {
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.reset_position, false);
                if (UserPreferences.isEnableAutodownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else if (selectedItem.getAutoDownload()) {
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                }
                if (selectedItem.getPaymentLink() != null) {
                    if (selectedItem.getFlattrStatus().flattrable()) {
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.support_item, false);
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
            mi.setItemVisibility(R.id.share_download_url_item, false);
            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
            if (hasMedia) {
                if (selectedItem.getMedia().getPosition() > 0) {
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.share_link_with_position_item, false);
            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
            if (hasMedia) {
            }
            mi.setItemVisibility(R.id.share_file, fileDownloaded);
            if (selectedItem.isPlayed()) {
                mi.setItemVisibility(R.id.mark_unread_item, false);
            } else {
                mi.setItemVisibility(R.id.mark_read_item, false);
            }
            if (selectedItem.getMedia() != null) {
                if (selectedItem.getMedia().getPosition() != 0) {
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.reset_position, false);
            if (UserPreferences.isEnableAutodownload()) {
                mi.setItemVisibility(R.id.activate_auto_download, false);
                mi.setItemVisibility(R.id.deactivate_auto_download, false);
            } else if (selectedItem.getAutoDownload()) {
                mi.setItemVisibility(R.id.deactivate_auto_download, false);
            } else {
                mi.setItemVisibility(R.id.activate_auto_download, false);
            }
            if (selectedItem.getPaymentLink() != null) {
                if (selectedItem.getFlattrStatus().flattrable()) {
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.support_item, false);
            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
            return true;
        }
        mi.setItemVisibility(R.id.move_to_bottom_item, false);
        if (isInQueue) {
            mi.setItemVisibility(R.id.remove_from_queue_item, false);
        }
        if (isInQueue) {
            if (selectedItem.getMedia() != null) {
                if (showExtendedMenu) {
                    if (ShareUtils.hasLinkToShare(selectedItem)) {
                        if (selectedItem.getMedia().getDownload_url() == null) {
                            mi.setItemVisibility(R.id.share_download_url_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                                if (selectedItem.getMedia().getPosition() > 0) {
                                    if (hasMedia) {
                                    }
                                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                    if (selectedItem.isPlayed()) {
                                        mi.setItemVisibility(R.id.mark_read_item, false);
                                    } else {
                                        mi.setItemVisibility(R.id.mark_unread_item, false);
                                    }
                                    if (selectedItem.getMedia() != null) {
                                        if (selectedItem.getMedia().getPosition() != 0) {
                                            if (UserPreferences.isEnableAutodownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            } else if (selectedItem.getAutoDownload()) {
                                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                            } else {
                                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                            }
                                            if (selectedItem.getPaymentLink() != null) {
                                                if (selectedItem.getFlattrStatus().flattrable()) {
                                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                    return true;
                                                }
                                            }
                                            mi.setItemVisibility(R.id.support_item, false);
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.reset_position, false);
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.share_link_with_position_item, false);
                            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                        if (hasMedia) {
                            if (selectedItem.getMedia().getPosition() > 0) {
                                if (hasMedia) {
                                }
                                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                                if (selectedItem.isPlayed()) {
                                    mi.setItemVisibility(R.id.mark_read_item, false);
                                } else {
                                    mi.setItemVisibility(R.id.mark_unread_item, false);
                                }
                                if (selectedItem.getMedia() != null) {
                                    if (selectedItem.getMedia().getPosition() != 0) {
                                        if (UserPreferences.isEnableAutodownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        } else if (selectedItem.getAutoDownload()) {
                                            mi.setItemVisibility(R.id.activate_auto_download, false);
                                        } else {
                                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                        }
                                        if (selectedItem.getPaymentLink() != null) {
                                            if (selectedItem.getFlattrStatus().flattrable()) {
                                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                                return true;
                                            }
                                        }
                                        mi.setItemVisibility(R.id.support_item, false);
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.reset_position, false);
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.share_link_with_position_item, false);
                        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.visit_website_item, false);
                mi.setItemVisibility(R.id.share_link_item, false);
                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                if (selectedItem.getMedia().getDownload_url() == null) {
                    if (hasMedia) {
                        if (selectedItem.getMedia().getPosition() > 0) {
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
                mi.setItemVisibility(R.id.share_download_url_item, false);
                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                if (hasMedia) {
                    if (selectedItem.getMedia().getPosition() > 0) {
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                if (hasMedia) {
                }
                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                if (selectedItem.isPlayed()) {
                    mi.setItemVisibility(R.id.mark_unread_item, false);
                } else {
                    mi.setItemVisibility(R.id.mark_read_item, false);
                }
                if (selectedItem.getMedia() != null) {
                    if (selectedItem.getMedia().getPosition() != 0) {
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.reset_position, false);
                if (UserPreferences.isEnableAutodownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else if (selectedItem.getAutoDownload()) {
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                }
                if (selectedItem.getPaymentLink() != null) {
                    if (selectedItem.getFlattrStatus().flattrable()) {
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.support_item, false);
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
        }
        mi.setItemVisibility(R.id.add_to_queue_item, false);
        if (showExtendedMenu) {
            if (ShareUtils.hasLinkToShare(selectedItem)) {
                if (selectedItem.getMedia().getDownload_url() == null) {
                    mi.setItemVisibility(R.id.share_download_url_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                        if (selectedItem.getMedia().getPosition() > 0) {
                            if (hasMedia) {
                            }
                            mi.setItemVisibility(R.id.share_file, fileDownloaded);
                            if (selectedItem.isPlayed()) {
                                mi.setItemVisibility(R.id.mark_read_item, false);
                            } else {
                                mi.setItemVisibility(R.id.mark_unread_item, false);
                            }
                            if (selectedItem.getMedia() != null) {
                                if (selectedItem.getMedia().getPosition() != 0) {
                                    if (UserPreferences.isEnableAutodownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    } else if (selectedItem.getAutoDownload()) {
                                        mi.setItemVisibility(R.id.activate_auto_download, false);
                                    } else {
                                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                    }
                                    if (selectedItem.getPaymentLink() != null) {
                                        if (selectedItem.getFlattrStatus().flattrable()) {
                                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                            return true;
                                        }
                                    }
                                    mi.setItemVisibility(R.id.support_item, false);
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.reset_position, false);
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.share_link_with_position_item, false);
                    mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
                if (hasMedia) {
                    if (selectedItem.getMedia().getPosition() > 0) {
                        if (hasMedia) {
                        }
                        mi.setItemVisibility(R.id.share_file, fileDownloaded);
                        if (selectedItem.isPlayed()) {
                            mi.setItemVisibility(R.id.mark_read_item, false);
                        } else {
                            mi.setItemVisibility(R.id.mark_unread_item, false);
                        }
                        if (selectedItem.getMedia() != null) {
                            if (selectedItem.getMedia().getPosition() != 0) {
                                if (UserPreferences.isEnableAutodownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                } else if (selectedItem.getAutoDownload()) {
                                    mi.setItemVisibility(R.id.activate_auto_download, false);
                                } else {
                                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                                }
                                if (selectedItem.getPaymentLink() != null) {
                                    if (selectedItem.getFlattrStatus().flattrable()) {
                                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                        return true;
                                    }
                                }
                                mi.setItemVisibility(R.id.support_item, false);
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.reset_position, false);
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.share_link_with_position_item, false);
                mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
                if (hasMedia) {
                }
                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                if (selectedItem.isPlayed()) {
                    mi.setItemVisibility(R.id.mark_unread_item, false);
                } else {
                    mi.setItemVisibility(R.id.mark_read_item, false);
                }
                if (selectedItem.getMedia() != null) {
                    if (selectedItem.getMedia().getPosition() != 0) {
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.reset_position, false);
                if (UserPreferences.isEnableAutodownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else if (selectedItem.getAutoDownload()) {
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                }
                if (selectedItem.getPaymentLink() != null) {
                    if (selectedItem.getFlattrStatus().flattrable()) {
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.support_item, false);
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
        }
        mi.setItemVisibility(R.id.visit_website_item, false);
        mi.setItemVisibility(R.id.share_link_item, false);
        mi.setItemVisibility(R.id.share_link_with_position_item, false);
        if (selectedItem.getMedia().getDownload_url() == null) {
            if (hasMedia) {
                if (selectedItem.getMedia().getPosition() > 0) {
                    if (hasMedia) {
                    }
                    mi.setItemVisibility(R.id.share_file, fileDownloaded);
                    if (selectedItem.isPlayed()) {
                        mi.setItemVisibility(R.id.mark_read_item, false);
                    } else {
                        mi.setItemVisibility(R.id.mark_unread_item, false);
                    }
                    if (selectedItem.getMedia() != null) {
                        if (selectedItem.getMedia().getPosition() != 0) {
                            if (UserPreferences.isEnableAutodownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            } else if (selectedItem.getAutoDownload()) {
                                mi.setItemVisibility(R.id.activate_auto_download, false);
                            } else {
                                mi.setItemVisibility(R.id.deactivate_auto_download, false);
                            }
                            if (selectedItem.getPaymentLink() != null) {
                                if (selectedItem.getFlattrStatus().flattrable()) {
                                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                    return true;
                                }
                            }
                            mi.setItemVisibility(R.id.support_item, false);
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.reset_position, false);
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.share_link_with_position_item, false);
            mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
            if (hasMedia) {
            }
            mi.setItemVisibility(R.id.share_file, fileDownloaded);
            if (selectedItem.isPlayed()) {
                mi.setItemVisibility(R.id.mark_unread_item, false);
            } else {
                mi.setItemVisibility(R.id.mark_read_item, false);
            }
            if (selectedItem.getMedia() != null) {
                if (selectedItem.getMedia().getPosition() != 0) {
                    if (UserPreferences.isEnableAutodownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    } else if (selectedItem.getAutoDownload()) {
                        mi.setItemVisibility(R.id.activate_auto_download, false);
                    } else {
                        mi.setItemVisibility(R.id.deactivate_auto_download, false);
                    }
                    if (selectedItem.getPaymentLink() != null) {
                        if (selectedItem.getFlattrStatus().flattrable()) {
                            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                            return true;
                        }
                    }
                    mi.setItemVisibility(R.id.support_item, false);
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.reset_position, false);
            if (UserPreferences.isEnableAutodownload()) {
                mi.setItemVisibility(R.id.activate_auto_download, false);
                mi.setItemVisibility(R.id.deactivate_auto_download, false);
            } else if (selectedItem.getAutoDownload()) {
                mi.setItemVisibility(R.id.deactivate_auto_download, false);
            } else {
                mi.setItemVisibility(R.id.activate_auto_download, false);
            }
            if (selectedItem.getPaymentLink() != null) {
                if (selectedItem.getFlattrStatus().flattrable()) {
                    isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                    mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                    mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                    mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                    return true;
                }
            }
            mi.setItemVisibility(R.id.support_item, false);
            isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
            mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
            mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
            mi.setItemVisibility(R.id.remove_item, fileDownloaded);
            return true;
        }
        mi.setItemVisibility(R.id.share_download_url_item, false);
        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
        if (hasMedia) {
            if (selectedItem.getMedia().getPosition() > 0) {
                if (hasMedia) {
                }
                mi.setItemVisibility(R.id.share_file, fileDownloaded);
                if (selectedItem.isPlayed()) {
                    mi.setItemVisibility(R.id.mark_read_item, false);
                } else {
                    mi.setItemVisibility(R.id.mark_unread_item, false);
                }
                if (selectedItem.getMedia() != null) {
                    if (selectedItem.getMedia().getPosition() != 0) {
                        if (UserPreferences.isEnableAutodownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        } else if (selectedItem.getAutoDownload()) {
                            mi.setItemVisibility(R.id.activate_auto_download, false);
                        } else {
                            mi.setItemVisibility(R.id.deactivate_auto_download, false);
                        }
                        if (selectedItem.getPaymentLink() != null) {
                            if (selectedItem.getFlattrStatus().flattrable()) {
                                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                                return true;
                            }
                        }
                        mi.setItemVisibility(R.id.support_item, false);
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.reset_position, false);
                if (UserPreferences.isEnableAutodownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else if (selectedItem.getAutoDownload()) {
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                }
                if (selectedItem.getPaymentLink() != null) {
                    if (selectedItem.getFlattrStatus().flattrable()) {
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.support_item, false);
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
        }
        mi.setItemVisibility(R.id.share_link_with_position_item, false);
        mi.setItemVisibility(R.id.share_download_url_with_position_item, false);
        if (hasMedia) {
        }
        mi.setItemVisibility(R.id.share_file, fileDownloaded);
        if (selectedItem.isPlayed()) {
            mi.setItemVisibility(R.id.mark_unread_item, false);
        } else {
            mi.setItemVisibility(R.id.mark_read_item, false);
        }
        if (selectedItem.getMedia() != null) {
            if (selectedItem.getMedia().getPosition() != 0) {
                if (UserPreferences.isEnableAutodownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                } else if (selectedItem.getAutoDownload()) {
                    mi.setItemVisibility(R.id.activate_auto_download, false);
                } else {
                    mi.setItemVisibility(R.id.deactivate_auto_download, false);
                }
                if (selectedItem.getPaymentLink() != null) {
                    if (selectedItem.getFlattrStatus().flattrable()) {
                        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                        return true;
                    }
                }
                mi.setItemVisibility(R.id.support_item, false);
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
        }
        mi.setItemVisibility(R.id.reset_position, false);
        if (UserPreferences.isEnableAutodownload()) {
            mi.setItemVisibility(R.id.activate_auto_download, false);
            mi.setItemVisibility(R.id.deactivate_auto_download, false);
        } else if (selectedItem.getAutoDownload()) {
            mi.setItemVisibility(R.id.deactivate_auto_download, false);
        } else {
            mi.setItemVisibility(R.id.activate_auto_download, false);
        }
        if (selectedItem.getPaymentLink() != null) {
            if (selectedItem.getFlattrStatus().flattrable()) {
                isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
                mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
                mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
                mi.setItemVisibility(R.id.remove_item, fileDownloaded);
                return true;
            }
        }
        mi.setItemVisibility(R.id.support_item, false);
        isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);
        mi.setItemVisibility(R.id.add_to_favorites_item, isFavorite ^ 1);
        mi.setItemVisibility(R.id.remove_from_favorites_item, isFavorite);
        mi.setItemVisibility(R.id.remove_item, fileDownloaded);
        return true;
    }

    public static boolean onPrepareMenu(MenuInterface mi, FeedItem selectedItem, boolean showExtendedMenu, LongList queueAccess, int... excludeIds) {
        boolean rc = onPrepareMenu(mi, selectedItem, showExtendedMenu, queueAccess);
        if (rc && excludeIds != null) {
            for (int id : excludeIds) {
                mi.setItemVisibility(id, false);
            }
        }
        return rc;
    }

    public static boolean onMenuItemClicked(Context context, int menuItemId, FeedItem selectedItem) {
        switch (menuItemId) {
            case R.id.activate_auto_download:
                selectedItem.setAutoDownload(true);
                DBWriter.setFeedItemAutoDownload(selectedItem, true);
                break;
            case R.id.add_to_favorites_item:
                DBWriter.addFavoriteItem(selectedItem);
                break;
            case R.id.add_to_queue_item:
                DBWriter.addQueueItem(context, selectedItem);
                break;
            case R.id.deactivate_auto_download:
                selectedItem.setAutoDownload(false);
                DBWriter.setFeedItemAutoDownload(selectedItem, false);
                break;
            case R.id.mark_read_item:
                selectedItem.setPlayed(true);
                DBWriter.markItemPlayed(selectedItem, 1, false);
                if (!GpodnetPreferences.loggedIn()) {
                    break;
                }
                FeedMedia media = selectedItem.getMedia();
                if (media != null) {
                    GpodnetPreferences.enqueueEpisodeAction(new Builder(selectedItem, Action.PLAY).currentDeviceId().currentTimestamp().started(media.getDuration() / 1000).position(media.getDuration() / 1000).total(media.getDuration() / 1000).build());
                }
                break;
            case R.id.mark_unread_item:
                selectedItem.setPlayed(false);
                DBWriter.markItemPlayed(selectedItem, 0, false);
                if (GpodnetPreferences.loggedIn() && selectedItem.getMedia() != null) {
                    GpodnetPreferences.enqueueEpisodeAction(new Builder(selectedItem, Action.NEW).currentDeviceId().currentTimestamp().build());
                    break;
                }
                break;
            case R.id.remove_from_favorites_item:
                DBWriter.removeFavoriteItem(selectedItem);
                break;
            case R.id.remove_from_queue_item:
                DBWriter.removeQueueItem(context, selectedItem, true);
                break;
            case R.id.remove_item:
                DBWriter.deleteFeedMediaOfItem(context, selectedItem.getMedia().getId());
                break;
            case R.id.reset_position:
                selectedItem.getMedia().setPosition(0);
                DBWriter.markItemPlayed(selectedItem, 0, true);
                break;
            case R.id.share_download_url_item:
                ShareUtils.shareFeedItemDownloadLink(context, selectedItem);
                break;
            case R.id.share_download_url_with_position_item:
                ShareUtils.shareFeedItemDownloadLink(context, selectedItem, true);
                break;
            case R.id.share_file:
                ShareUtils.shareFeedItemFile(context, selectedItem.getMedia());
                break;
            case R.id.share_link_item:
                ShareUtils.shareFeedItemLink(context, selectedItem);
                break;
            case R.id.share_link_with_position_item:
                ShareUtils.shareFeedItemLink(context, selectedItem, true);
                break;
            case R.id.skip_episode_item:
                IntentUtils.sendLocalBroadcast(context, PlaybackService.ACTION_SKIP_CURRENT_EPISODE);
                break;
            case R.id.support_item:
                DBTasks.flattrItemIfLoggedIn(context, selectedItem);
                break;
            case R.id.visit_website_item:
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(FeedItemUtil.getLinkWithFallback(selectedItem)));
                if (!IntentUtils.isCallable(context, intent)) {
                    Toast.makeText(context, context.getString(R.string.download_error_malformed_url), 0).show();
                    break;
                }
                context.startActivity(intent);
                break;
            default:
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown menuItemId: ");
                stringBuilder.append(menuItemId);
                Log.d(str, stringBuilder.toString());
                return false;
        }
        return true;
    }
}
