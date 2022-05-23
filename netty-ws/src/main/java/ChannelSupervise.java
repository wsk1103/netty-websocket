import com.ws.proto.MessageProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author sk
 * @time 2022/5/16
 **/
public class ChannelSupervise {

    private static final ChannelGroup GLOBAL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ConcurrentMap<String, ChannelId> CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, ChannelGroup> CHANNEL_MAP_GROUP = new ConcurrentHashMap<>();

    public static void addChannel(Channel channel) {
        GLOBAL_GROUP.add(channel);
        CHANNEL_MAP.putIfAbsent(channel.id().asShortText(), channel.id());
    }

    public static void add(String roomId, Channel channel) {
        ChannelGroup group = CHANNEL_MAP_GROUP.get(roomId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            group.add(channel);
            CHANNEL_MAP_GROUP.put(roomId, group);
        } else {
            group.add(channel);
        }
    }

    public static void removeChannel(Channel channel) {
        GLOBAL_GROUP.remove(channel);
        CHANNEL_MAP.remove(channel.id().asShortText());
    }

    public static Channel findChannel(String id) {
        return GLOBAL_GROUP.find(CHANNEL_MAP.get(id));
    }

    public static void send2All(TextWebSocketFrame tws) {
        GLOBAL_GROUP.writeAndFlush(tws);
    }

    public static void send(String roomId, MessageProto.Message tws) {
        ChannelGroup group = CHANNEL_MAP_GROUP.get(roomId);
        if (group != null) {
            group.writeAndFlush(tws);
        }
    }


    public static void send2All(MessageProto.Message tws) {
        GLOBAL_GROUP.writeAndFlush(tws);
    }

}
