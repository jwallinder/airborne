package se.jwallinder.airborne;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import se.heinrisch.talkclient.TalkClient;

public class ListenerServiceMobile extends WearableListenerService {

    private TalkClient mTalkClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mTalkClient = new TalkClient(getApplicationContext());
        mTalkClient.connectClient();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {


        final DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());
        Log.e("sfgk", dataMap.getString("airborneTime"));

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("value", dataMap.getString("airborneTime"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        mTalkClient.disconnectClient();
        super.onDestroy();
    }
}
