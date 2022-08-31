package com.websocket.writer;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.FileWriter;

public class TextFileWriter {

    private Long intervalTime = new Integer(30000).longValue(); // Timer interval

    private List<String> dataList = new ArrayList(); // Temporarily store messages from websocket, avoiding writing document frequently

    private Timer timer = null; // A timer to writing data into a text document

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // The time formatter

    /**
     *  Create a timer, writing data to the text document
     */
    public void createTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                writeText();
            }
        },1000,intervalTime);
    }

    /**
     *  Add messages from websocket to a ArrayList
     */
    public void buildDataList(String data) {
        if(timer == null){
            // if without Timer now, create a Timer
            createTimer();
        }
        dataList.add(data);
    }

    /**
     *  writing data to the text document
     */
    public void writeText () {
        FileWriter writer = null;
        try {
            // please rewrite the file pathname if you need
            // if you use docker, please use [find / -name][filename] to find where it is
            // ex. find / -name websocketMessageHistory2022-08-31.txt
            File file = new File("websocketMessageHistory"+formatter.format(LocalDateTime.now())+".txt");
            if(!file.exists()){
                // if the application hasn't created a text document yet today, create one
                file.createNewFile();
            }
            // append:true
            writer = new FileWriter(file.getPath(),true);
            for(int i = 0;i<dataList.size();i++) {
                writer.write(dataList.get(i));
                if(i == (dataList.size()-1)){
                    System.out.println("======================write data successfully, ["+dataList.size()+"] message in total===================");
                }
            }
            dataList.clear();
            writer.close();
        } catch (Exception e) {
            System.out.println("===========================error occurs when writing data into document！======================================");
            e.printStackTrace();
        }
    }
}










