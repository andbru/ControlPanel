package com.andbru.controlpanel;

/**
 * Created by Anders on 2016-11-07.
 */

import android.util.Log;

import com.andbru.controlpanel.SettingsFragment.PassCmd;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {

    private String serverMessage;
    private PilotData mPilotData = new PilotData();

    public static final String SERVERIP = "192.168.1.157"; // your computer IP address at home
    //public static final String SERVERIP = "192.168.43.157"; // on the boat
    public static final int SERVERPORT = 37377;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    //private PilotData mPilotData;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {

        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
            Log.e("TCP Client", "C: sendMessage called.");
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");


                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    Log.e("TCP Client", "C: in.readLine called.");
                    serverMessage = in.readLine();
                    Log.e("TCP Client", "C: in.readLine finished.");
                    if (serverMessage != null && mMessageListener != null) {

                        String[] p = serverMessage.split(" ");
                        mPilotData.mode = p[0];
                        mPilotData.yawCmd = p[1];
                        mPilotData.yawIs = p[2];
                        mPilotData.rudderIs = p[3];
                        mPilotData.Kp = p[4];
                        mPilotData.Kd = p[5];
                        mPilotData.Ki = p[6];
                        mPilotData.Km = p[7];
                        mPilotData.gpsSpeed = p[8];
                        mPilotData.gpsCourse = p[9];
                        mPilotData.accGyroCount = p[10];
                        mPilotData.magCount = p[11];

                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mPilotData);
                        Log.e("TCP Client", "C: message received!");
                    }
                    serverMessage = null;

                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(PilotData mPilotData);
    }


    public void pilotCmd(String cmd) {
        switch(cmd) {
            case "bStdby":
                sendMessage("$SET 1,0.0,0.0,0.0,0.0,0.0");
                break;
            case "bHH":
                sendMessage("$SET 2,0.0,0.0,0.0,0.0,0.0");
                break;
            case "bMinus5":
                sendMessage("$SET 0,-5.0,0.0,0.0,0.0,0.0");
                break;
            case "bMinus1":
                sendMessage("$SET 0,-1.0,0.0,0.0,0.0,0.0");
                break;
            case "bPlus1":
                sendMessage("$SET 0,+1.0,0.0,0.0,0.0,0.0");
                break;
            case "bPlus5":
                sendMessage("$SET 0,+5.0,0.0,0.0,0.0,0.0");
                break;
            case "bKpDec":
                sendMessage("$SET 0,0.0,-0.1,0.0,0.0,0.0");
                break;
            case "bKpInc":
                sendMessage("$SET 0,0.0,+0.1,0.0,0.0,0.0");
                break;
            case "bKdDec":
                sendMessage("$SET 0,0.0,0.0,-0.1,0.0,0.0");
                break;
            case "bKdInc":
                sendMessage("$SET 0,0.0,0.0,+0.1,0.0,0.0");
                break;
            case "bKiDec":
                sendMessage("$SET 0,0.0,0.0,0.0,-0.01,0.0");
                break;
            case "bKiInc":
                sendMessage("$SET 0,0.0,0.0,0.0,+0.01,0.0");
                break;
            case "bKmDec":
                sendMessage("$SET 0,0.0,0.0,0.0,0.0,-0.1");
                break;
            case "bKmInc":
                sendMessage("$SET 0,0.0,0.0,0.0,0.0,+0.1");
                break;
            default:
                sendMessage("$SET 1,0.0,0.0,0.0,0.0,0.0");
        }

    }
}