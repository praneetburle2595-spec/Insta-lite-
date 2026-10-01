import com.praneet.focuslayer.ScreenPolicy;
import java.util.Set;
public class ScreenPolicyTest {
 static void check(boolean result,String name){if(!result)throw new AssertionError(name);}
 public static void main(String[] args){
  check(!ScreenPolicy.allows(Set.of()),"unknown screen blocked");
  check(!ScreenPolicy.allows(Set.of("action_bar_inbox_button")),"feed inbox shortcut is not inbox");
  check(ScreenPolicy.allows(Set.of("direct_inbox_recycler_view")),"inbox allowed");
  check(!ScreenPolicy.allows(Set.of("direct_thread_recycler_view")),"partial thread blocked");
  check(ScreenPolicy.allows(Set.of("direct_thread_recycler_view","row_thread_composer_edittext")),"conversation allowed");
  check(!ScreenPolicy.allows(Set.of("direct_thread_recycler_view","row_thread_composer_edittext","clips_viewer_view_pager")),"Reel viewer takes precedence over conversation");
  check(ScreenPolicy.allows(Set.of("rtc_call_controls","rtc_call_end_button")),"verified call allowed");
  System.out.println("7 screen-policy checks passed");
 }
}
