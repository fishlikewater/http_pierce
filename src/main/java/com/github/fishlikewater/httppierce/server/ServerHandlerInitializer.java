package com.github.fishlikewater.httppierce.server;

import com.github.fishlikewater.httppierce.codec.MessageCodec;
import com.github.fishlikewater.httppierce.config.HttpPierceServerConfig;
import com.github.fishlikewater.httppierce.handler.AuthHandler;
import com.github.fishlikewater.httppierce.handler.MessageTransferHandler;
import com.github.fishlikewater.httppierce.handler.ServerHeartBeatHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  客户端与服务端 通信 处理器初始化
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2019年02月26日 21:47
 **/
@Slf4j
public class ServerHandlerInitializer extends ChannelInitializer<Channel> {

    private final HttpPierceServerConfig httpPierceServerConfig;

    public ServerHandlerInitializer(HttpPierceServerConfig httpPierceServerConfig) {
        log.info("init transfer handler");
        this.httpPierceServerConfig = httpPierceServerConfig;
    }

    @Override
    protected void initChannel(Channel channel) {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new IdleStateHandler(0, 0, httpPierceServerConfig.getTimeout(), TimeUnit.SECONDS));
        p.addLast(new ServerHeartBeatHandler());
        /* 是否打开日志*/
        if (httpPierceServerConfig.isLogger()) {
            p.addLast(new LoggingHandler());
        }
        p
                .addLast(new LengthFieldBasedFrameDecoder(5*1024 * 1024, 0, 4))
                .addLast(new MessageCodec())
                .addLast(new AuthHandler(httpPierceServerConfig.getToken()))
                .addLast(new MessageTransferHandler());


    }
}
