package com.praneet.focuslayer;
import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.provider.Settings;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    TextView status;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout body = new LinearLayout(this); body.setOrientation(1); body.setPadding(32,64,32,32); body.setBackgroundColor(Color.rgb(244,247,248));
        setContentView(body);
        TextView title = new TextView(this); title.setText("Focus Layer"); title.setTextSize(32); body.addView(title);
        TextView intro = new TextView(this); intro.setText("Instagram for conversations.\n\nMessaging-only prototype: feed, Reels, Explore and unrecognized screens are covered. Interface changes can block messaging too.\n"); intro.setTextSize(17); body.addView(intro);
        status = new TextView(this); body.addView(status);
        Button enable = new Button(this); enable.setText("Set up protection"); body.addView(enable);
        enable.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Accessibility access")
            .setMessage("Focus Layer inspects Instagram interface IDs to recognize messaging and covers other screens. Android grants broad screen access; this app only inspects Instagram and does not save message text, use the internet, or ask for your password. Only the number of blocking sessions is saved locally. You can disable it at any time in Accessibility settings. Enable Focus Layer on the next screen to consent.")
            .setPositiveButton("Open settings", (d,w)->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
            .setNegativeButton("Cancel",null).show());
        Button open = new Button(this); open.setText("Open Instagram"); body.addView(open);
        open.setOnClickListener(v->{ Intent i=getPackageManager().getLaunchIntentForPackage(FocusService.INSTAGRAM); if(i!=null)startActivity(i); else Toast.makeText(this,"Install Instagram first",Toast.LENGTH_LONG).show(); });
        Button stop = new Button(this); stop.setText("Disable / recovery settings"); body.addView(stop); stop.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        TextView tip = new TextView(this); tip.setText("\nIf messaging stays covered, use Exit Instagram and disable Focus Layer. This prototype requires calibration against your Instagram version.\n\nNo root, login, VPN or notification access required."); body.addView(tip);
    }
    @Override public void onResume(){super.onResume(); String enabled=Settings.Secure.getString(getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES); boolean on=enabled!=null && enabled.contains(getPackageName()+"/"); status.setText((on?"Service enabled":"Service disabled")+" • blocking sessions: "+getSharedPreferences("focus",0).getInt("blocks",0)+"\n");}
}
