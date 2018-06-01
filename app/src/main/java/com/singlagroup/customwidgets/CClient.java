package com.singlagroup.customwidgets;

import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CClient implements Runnable {
    private Socket socket;
    private String ServerIP = "192.168.8.24";//"<my server ip goes here>";
    private int ServerPort = 4999;
    BufferedReader  in;
    PrintWriter     out;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    public CClient(String IP, int Port){
        ServerIP = IP;
        ServerPort = Port;
    }
    @Override
    public void run()
    {
        try{
            InetAddress inetAddress = InetAddress.getByName(ServerIP);
            socket = new Socket(inetAddress, ServerPort);
            System.out.println("Call Socket:"+socket.getOutputStream());
        }
        catch(Exception e){
            System.out.println("Whoops! It didn't work!:");
            System.out.println("Socket Exception:"+e.toString());
            System.out.println("\n");
        }
    }

    public void Send(String s){
        try
        {
            if (socket != null)
                System.out.println("Hexa Code: "+s);
            //PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            //out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            //Create BufferedReader object for receiving messages from server.
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF(s);
            //textIn.setText(dataInputStream.readUTF());

            Log.d("TAG", "In/Out created:");
//            out.print(s);
//            out.flush();
//            out.close();
            //in.close();
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        catch (UnknownHostException e) {
            System.out.print("Unknown Host Exception:"+e.toString());
        } catch (IOException e) {
            System.out.print("IO Exception:"+e.toString());
        }catch (Exception e) {
            System.out.print("Send Exception:"+e.toString());
        }

    }
}