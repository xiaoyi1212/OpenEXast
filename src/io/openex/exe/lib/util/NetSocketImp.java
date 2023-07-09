package io.openex.exe.lib.util;

import io.openex.Main;
import io.openex.exe.core.Executor;
import io.openex.exe.obj.*;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.VMRuntimeException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class NetSocketImp extends WebSocketClient {

    static NetSocketImp instance;

    private volatile int sendFlag = 0;
    private String result = null;
    public static NetSocketImp getInstance() {
        return instance;
    }


    public NetSocketImp(int port) throws URISyntaxException {
        super(new URI("ws://localhost:"+port));
        NetSocketImp.instance = this;
    }

    public ExObject invokeSocketFunction(String lib, String function, ArrayList<ExObject> objects, Executor executor) throws VMRuntimeException {
        synchronized (this) {

            sendFlag = 1;

            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();

            for (ExObject obj : objects) {
                ExObject o = obj;
                ExValue buf = null;
                if(o instanceof ExVarName){
                    for(ExValue v: ThreadManager.getValues()){
                        if(v.getData().equals(o.getData())){
                            buf = v;
                            break;
                        }
                    }
                    for(ExValue v:executor.getExecuting().getValues()){
                        if(v.getData().equals(o.getData())){
                            buf = v;
                            break;
                        }
                    }
                    if(buf == null)throw new VMRuntimeException("找不到指定变量:"+o.getData(),executor.getThread());

                    if(buf.getType()==ExObject.ARRAY){
                        o = buf;
                    }else o = buf.getVar();
                }
                obj = o;

                object.put("type", obj.getType());
                object.put("data", obj.getData());
                array.put(object);
                object = new JSONObject();
            }

            object = new JSONObject();

            object.put("library", lib);
            object.put("function", function);
            object.put("vars", array);
            instance.send(object.toString());

            while (sendFlag != 0) Thread.onSpinWait();

            object = new JSONObject(result);


            switch ((Integer) object.get("type")){
                case ExObject.BOOLEAN -> {
                    return new ExBool(Boolean.parseBoolean((String) object.get("data")));
                }
                case ExObject.DOUBLE -> {
                    return new ExDouble(Double.parseDouble((String) object.get("data")));
                }
                case ExObject.INTEGER -> {
                    return new ExInt(Integer.parseInt((String) object.get("data")));
                }
                case ExObject.STRING -> {
                    return new ExString((String) object.get("data"));
                }
                case ExObject.ARRAY -> {
                    JSONArray array1 = object.getJSONArray("data");
                    ArrayList<ExObject> obj = new ArrayList<>();
                    for (Object o : array1) {
                        JSONObject object1 = (JSONObject) o;
                        obj.add(getObject(object1));
                    }
                    return new ExArray("<return>",obj);
                }
                default -> {
                    return new ExNull();
                }
            }
        }
    }

    private static ExObject getObject(JSONObject object){
        switch ((Integer) object.get("type")){
            case ExObject.BOOLEAN -> {
                return new ExBool(Boolean.parseBoolean((String) object.get("data")));
            }
            case ExObject.DOUBLE -> {
                return new ExDouble(Double.parseDouble((String) object.get("data")));
            }
            case ExObject.INTEGER -> {
                return new ExInt(Integer.parseInt((String) object.get("data")));
            }
            case ExObject.STRING -> {
                return new ExString((String) object.get("data"));
            }
            case ExObject.ARRAY -> {
                JSONArray array1 = object.getJSONArray("data");
                ArrayList<ExObject> obj = new ArrayList<>();
                for (Object o : array1) {
                    JSONObject object1 = (JSONObject) o;
                    obj.add(getObject(object1));
                }
                return new ExArray("<return>",obj);
            }
            default -> {
                return new ExNull();
            }
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Main.getOutput().info("[OpenEX-WebSocket]: Started socket thread.");
    }

    @Override
    public void onMessage(String s) {
        result = s;
        sendFlag = 0;
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        result = null;
        sendFlag = 0;
    }

    @Override
    public void onError(Exception e) {
        result = null;
        sendFlag = 0;
        e.printStackTrace();
    }
}
