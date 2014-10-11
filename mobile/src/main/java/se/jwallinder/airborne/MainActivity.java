package se.jwallinder.airborne;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;

import se.heinrisch.talkclient.TalkClient;
import se.heinrisch.talkclient.adapters.TalkMessageAdapter;


public class MainActivity extends Activity {


    private TextView mTextViewAirbornTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewAirbornTime = (TextView) findViewById(R.id.airborneTime);
        mTextViewAirbornTime.setText(getIntent().getStringExtra("value"));
  /*      mTalkClient.setTalkMessageAdapter(new TalkMessageAdapter() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                Log.e("talk", "messager recieved");
                final DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());
                if (dataMap.containsKey("airborneTime")) {

                    mTextViewAirbornTime.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewAirbornTime.setText(dataMap.getString("airborneTime") + "ms");
                        }
                    });
                }
            }

        });*/


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
