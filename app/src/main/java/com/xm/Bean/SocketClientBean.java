package com.xm.Bean;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class SocketClientBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8899620357216991194L;
    private Socket socket;
    private String ID;
    private int heartBeatCount = 0;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public void setID(String ID) {
        // TODO Auto-generated method stub
        this.ID = ID;
    }

    public String getID() {
        // TODO Auto-generated method stub
        return this.ID;
    }

    public void setSocket(Socket socket) {
        // TODO Auto-generated method stub
        this.socket = socket;
    }

    public Socket getSocket() {
        // TODO Auto-generated method stub
        return this.socket;
    }

    public void setHeartBeatCount(int heartBeatCount) {
        // TODO Auto-generated method stub
        this.heartBeatCount = heartBeatCount;
    }

    public int getHeartBeatCount() {
        // TODO Auto-generated method stub
        return this.heartBeatCount;
    }

}
