import cn.hutool.json.JSONUtil;
import com.google.protobuf.util.JsonFormat;
import com.ws.proto.MessageProto;
import entity.Chat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * @author sk
 * @time 2022/5/19
 **/
public class MessageWebSocketHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        String ss = JsonFormat.printer().print(msg);
        logger.debug("收到消息：" + ss);
        // 返回应答消息
        MessageProto.Message.Request request = msg.getRequest();
        String data = request.getData();
        MessageProto.Message.Response response = MessageProto
                .Message.Response.newBuilder()
                .setCode(200)
                .setData(data)
                .build();
        MessageProto.Message to = MessageProto.Message.newBuilder()
                .setMessageType(MessageProto.Message.MessageType.ACTION)
                .setSequence(System.currentTimeMillis() + "")
                .setResponse(response)
                .build();
        Chat chat = JSONUtil.toBean(data, Chat.class);
        if (chat.getRoomId() != null) {
            ChannelSupervise.send(chat.getRoomId(), to);
        } else {
            ChannelSupervise.send2All(to);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        logger.debug("客户端加入连接：" + ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        logger.debug("客户端断开连接：" + ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


}
