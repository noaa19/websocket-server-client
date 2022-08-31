package com.websocket.server;

import com.websocket.writer.TextFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@ServerEndpoint("/connect")
@Component
@Slf4j
public class MyWebsocketServer {
    private final Map<String, Session> clients = new ConcurrentHashMap<>();
    private TextFileWriter textFileWriter = new TextFileWriter();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @OnOpen
    public void onOpen(Session session) {
        log.info("new connection: id:"+session.getId());
        String msg = formatter.format(LocalDateTime.now())+"  new connection，id："+session.getId()+"\n";
        textFileWriter.buildDataList(msg);
        clients.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("connection closed, id:"+session.getId());
        String msg = formatter.format(LocalDateTime.now())+"  connection closed，id："+session.getId()+"\n";
        textFileWriter.buildDataList(msg);
        clients.remove(session.getId());
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("server received message: "+message);
        String msg = formatter.format(LocalDateTime.now())+"  server received message："+message+"\n";
        textFileWriter.buildDataList(msg);
        this.sendMsg(message);
    }

    private void sendMsg(String message) {
        // send message to every client
        for (Map.Entry<String, Session> sessionEntry : clients.entrySet()) {
            sessionEntry.getValue().getAsyncRemote().sendText(message);
        }
    }

}
