package com.praneet.focuslayer;
import android.accessibilityservice.AccessibilityService;
import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.view.accessibility.*;
import android.widget.*;
import java.util.*;

public class FocusService extends AccessibilityService {
    public static final String INSTAGRAM="com.instagram.android";
    private final Handler handler=new Handler(Looper.getMainLooper());
    private WindowManager manager; private View shield; private boolean inspecting; private boolean queued;
    private final Runnable refresh=()->{queued=false;inspect();};
    private final Runnable watchdog=new Runnable(){public void run(){inspect();if(shield!=null)handler.postDelayed(this,300);}};
    @Override protected void onServiceConnected(){if(!getSharedPreferences("focus",0).getBoolean("consent",false)){disableSelf();return;}
        manager=(WindowManager)getSystemService(WINDOW_SERVICE); inspect();}
    @Override public void onAccessibilityEvent(AccessibilityEvent e){if(!queued){queued=true;handler.postDelayed(refresh,60);}}
    private AccessibilityNodeInfo instagramRoot(){
        // Check active-window package metadata; traverse UI IDs only for Instagram.
        for(AccessibilityWindowInfo w:getWindows()) if(w.getType()==AccessibilityWindowInfo.TYPE_APPLICATION && w.isActive()){
            AccessibilityNodeInfo r=w.getRoot();
            if(r!=null && INSTAGRAM.contentEquals(r.getPackageName()==null?"":r.getPackageName()))return r;
            return null;
        }
        return null;
    }
    private void inspect(){
        if(inspecting)return; inspecting=true;
        try{
            AccessibilityNodeInfo root=instagramRoot();
            if(root==null){hide(); return;}
            Set<String> ids=new HashSet<>(); collect(root,ids,0,new int[]{0});
            if(ScreenPolicy.allows(ids))hide();else show();
        }catch(RuntimeException failure){hide();disableSelf();}finally{inspecting=false;}
    }
    private void collect(AccessibilityNodeInfo n,Set<String> ids,int depth,int[] count){
        if(n==null || depth>40 || ++count[0]>2500 || !n.isVisibleToUser())return;
        String id=n.getViewIdResourceName(); if(id!=null)ids.add(id.substring(id.lastIndexOf('/')+1));
        for(int i=0;i<n.getChildCount();i++)collect(n.getChild(i),ids,depth+1,count);
    }
    private void show(){
        if(shield!=null || manager==null)return;
        LinearLayout box=new LinearLayout(this); box.setOrientation(1); box.setGravity(Gravity.CENTER); box.setPadding(36,36,36,36); box.setBackgroundColor(Color.rgb(17,30,37)); box.setFitsSystemWindows(true);
        TextView title=new TextView(this); title.setText("Here to connect."); title.setTextSize(30); title.setTextColor(Color.WHITE); box.addView(title);
        TextView text=new TextView(this); text.setText("\nThis screen is blocked.\nOnly recognized messaging and calls are allowed.\n"); text.setTextColor(Color.WHITE); text.setTextSize(17); box.addView(text);
        Button dm=new Button(this); dm.setText("Go to messages"); box.addView(dm); dm.setOnClickListener(v->navigateInbox());
        Button back=new Button(this);back.setText("Back");box.addView(back);back.setOnClickListener(v->{performGlobalAction(GLOBAL_ACTION_BACK);handler.postDelayed(refresh,250);});
        Button exit=new Button(this);exit.setText("Exit Instagram");box.addView(exit);exit.setOnClickListener(v->{performGlobalAction(GLOBAL_ACTION_HOME);handler.postDelayed(refresh,250);});
        Button off=new Button(this);off.setText("Turn off protection");box.addView(off);off.setOnClickListener(v->{hide();disableSelf();});
        WindowManager.LayoutParams p=new WindowManager.LayoutParams(-1,-1,WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.OPAQUE);
        try{manager.addView(box,p);shield=box; handler.removeCallbacks(watchdog);handler.postDelayed(watchdog,300); android.content.SharedPreferences prefs=getSharedPreferences("focus",0);prefs.edit().putInt("blocks",prefs.getInt("blocks",0)+1).apply();}catch(RuntimeException failure){hide();disableSelf();}
    }
    private void navigateInbox(){
        try{
            AccessibilityNodeInfo r=instagramRoot();if(r==null)return;
            if(!clickInbox(r,0,new int[]{0}))Toast.makeText(this,"Inbox button unavailable. Try Back, or open a DM notification.",Toast.LENGTH_LONG).show();
            handler.postDelayed(refresh,250);
        }catch(RuntimeException failure){hide();disableSelf();}
    }
    private boolean clickInbox(AccessibilityNodeInfo n,int depth,int[] count){
        if(n==null || depth>40 || ++count[0]>2500 || !n.isVisibleToUser())return false;
        String id=n.getViewIdResourceName();
        if(id!=null && (id.endsWith("/action_bar_inbox_button") || id.endsWith("/tab_direct"))){
            AccessibilityNodeInfo target=n; for(int j=0;j<4 && target!=null;j++,target=target.getParent())if(target.isClickable())return target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        for(int i=0;i<n.getChildCount();i++)if(clickInbox(n.getChild(i),depth+1,count))return true;
        return false;
    }
    private void hide(){handler.removeCallbacks(watchdog);if(shield!=null){try{manager.removeView(shield);}catch(RuntimeException ignored){}shield=null;}}
    @Override public void onInterrupt(){hide();}
    @Override public void onDestroy(){handler.removeCallbacksAndMessages(null);hide();super.onDestroy();}
}
