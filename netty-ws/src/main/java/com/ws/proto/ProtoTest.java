package com.ws.proto;

import com.google.protobuf.util.JsonFormat;

import java.util.Arrays;

/**
 * @author sk
 * @time 2022/5/19
 **/
public class ProtoTest {

    public static void main(String[] args) {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setMessageType(MessageProto.Message.MessageType.CHAT)
                .setSequence(System.currentTimeMillis() + "")
                .setRequest(MessageProto.Message.Request.newBuilder().setCode(200).setData("{\"id\":\"22\"}").build());

        MessageProto.Message message = builder.build();
        System.err.println(message);
        byte[] b = message.toByteArray();
        System.err.println("proto []=" + Arrays.toString(b));
        System.err.println("proto [].length=" + b.length);

        try {
            MessageProto.Message message1 = MessageProto.Message.parseFrom(b);
            String ss = JsonFormat.printer().print(message);
            String ss1 = JsonFormat.printer().print(message1);
            System.err.println("json = " + ss);
            System.err.println("json.length = " + ss.length());
            System.err.println("json = " + ss1);
            System.err.println("json.length = " + ss1.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
