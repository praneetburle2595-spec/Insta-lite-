package com.praneet.focuslayer;
import java.util.Set;
public final class ScreenPolicy {
    // Instagram's private UI IDs are not a stable API. Keep matching narrow.
    public static boolean allows(Set<String> ids) {
        if (ids.contains("clips_viewer_view_pager") || ids.contains("clips_viewer_recycler_view") || ids.contains("explore_grid")) return false;
        boolean inbox = ids.contains("direct_inbox_recycler_view") || ids.contains("direct_inbox_list");
        boolean thread = ids.contains("direct_thread_recycler_view") &&
            (ids.contains("row_thread_composer_edittext") || ids.contains("direct_thread_composer"));
        boolean call = ids.contains("rtc_call_controls") && ids.contains("rtc_call_end_button");
        return inbox || thread || call;
    }
}
