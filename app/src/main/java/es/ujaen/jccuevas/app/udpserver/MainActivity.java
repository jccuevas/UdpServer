package es.ujaen.jccuevas.app.udpserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    TextView servertext=null;
    private Thread mMainThread = null;
    private Server mServer = null;
    private MulticastServer mMCServer = null;
    Handler mHandler =  new Handler(Looper.getMainLooper()) {

        /*
            * handleMessage() defines the operations to perform when
            * the Handler receives a new Message to process.
            */
        @Override
        public void handleMessage(Message inputMessage) {

            switch (inputMessage.what){
                case 1:
                    String message = inputMessage.getData().getString("message");
                    servertext.setText(message+"\r\n"+servertext.getText());
                    break;
            }
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        servertext = (TextView)findViewById(R.id.textView);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMainThread==null) {
                    servertext.setText("");
                    mMCServer=new MulticastServer(mHandler);
                    mMainThread = new Thread(mMCServer);
                    mMainThread.start();
                    fab.setImageResource(R.drawable.ic_action_stop);
                    Snackbar.make(view, getString(R.string.action_startserver), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    mMCServer.stopServer();
                    mMainThread=null;
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    Snackbar.make(view, getString(R.string.action_stopserver), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
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
