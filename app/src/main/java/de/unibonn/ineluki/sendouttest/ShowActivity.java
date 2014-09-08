package de.unibonn.ineluki.sendouttest;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class ShowActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    boolean start = false;
    public TextView text1;
    public EditText inputPort;
    public EditText inputIP;
    public EditText input;
    public TextView logText;
    public Button btn;
    public Switch tcpSwitch;
    public boolean isTcp = false;

    BufferedReader readFromHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        text1=(TextView)findViewById(R.id.textView1);
        input=(EditText)findViewById(R.id.editText1);
        inputPort=(EditText)findViewById(R.id.editTextPort);
        inputIP = (EditText)findViewById(R.id.editTextIP);
        logText = (TextView)findViewById(R.id.textView2);

        btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(this);

        tcpSwitch = (Switch)findViewById(R.id.switch1);
        tcpSwitch.setOnCheckedChangeListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        String messageStr;
        int server_port;
        InetAddress local = null;
        DatagramSocket s = null;
        //byte[] message = new byte[1500];
        server_port = Integer.parseInt(inputPort.getText().toString());
        messageStr = input.getText().toString();

        if (!isTcp) {
            Log.d("SendOutTest", "try udp socket");
            try {
                s = new DatagramSocket();
                local = InetAddress.getByName(inputIP.getText().toString());
                Log.d("SendOutTest","udp local Port: " + s.getLocalPort());
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();

            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);

            //ByteBuffer dupl = ByteBuffer.wrap(p.getData());

            Log.d("SendOutTest", "------------------------------------------ ");
            /*
            byte buff;
            int i =1;
            while (dupl.position() < dupl.limit()) {
                buff = dupl.get();
                Log.d("SendOutTest", "byte " + String.format("%03d.", i) + ":" + String.format("%02x.", buff & 0xFF));
                i++;
                //if(i==headerLength) Log.d(TAG, "end of IP header------------------------------------------ ");
            }
            */
            Log.d("SendOutTest", "------------------------------------------ ");
            Log.d("SendOutTest","try sending");
            try {
                s.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("SendOutTest", "message:");
            //s.close();
            /*
            msg_length = messageStr.length()+5;
            message = new byte[msg_length*2];
            p = new DatagramPacket(message,msg_length);
            try {
                //s = new DatagramSocket(server_port);
                s.receive(p);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String text = new String(message, 0, p.getLength());
            Log.d("SendOutTest", "message:" + text);
            */
            s.close();
        }
        else {
            try {
                local = InetAddress.getByName(inputIP.getText().toString());
                Socket sock = new Socket(local, server_port);
                //outgoing stream redirect to socket
                OutputStream out = sock.getOutputStream();
                PrintWriter output = new PrintWriter(out);
                //output.println("Hello Python!");
                output.println(messageStr);
                output.flush();

                BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                //read line(s)
                String st = input.readLine();
                Log.d("SendOutTest TCP", "message received:" + st);
                //Close connection
                sock.close();
                } catch (UnknownHostException e) {
                       e.printStackTrace();
                } catch (IOException e) {
                e.printStackTrace();
                }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b) {
                isTcp = true;
            } else {
                isTcp = false;
            }
    }
}
