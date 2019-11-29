package acidsore;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.AttributeKey;
import java.io.File;
import java.net.InetSocketAddress;

public class NettyClient {
    private static Channel currentChannel;
    private NettyClientHandler handler;
    private static NettyClient ourInstance = new NettyClient();
    public static AttributeKey<String> fn = new AttributeKey<>("fn"); //FileName for client

   private NettyClient() { }

    public static NettyClient getInstance(){return ourInstance;}

    public static Channel getCurrentChannel() {
        return currentChannel;
    }

    public NettyClientHandler getNettyClientHandler(){return handler;}


    public void openConnection() throws Exception, InterruptedException {
        System.out.println("connection is opened");
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("localhost", 8081))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) throws Exception {
                            handler = new NettyClientHandler();
                            channel.pipeline()
                                    .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
                                            new ObjectEncoder(), handler);
                            currentChannel = channel;
                        }
                    });

            Channel channel = bootstrap.connect().syncUninterruptibly().channel();
//            ChannelFuture channelFuture = bootstrap.connect().sync();
//            channelFuture.channel().closeFuture().sync();
            channel.closeFuture().sync();


        }
         finally {
        //  loopgroup.shutdownGracefully().sync();
        }
}

    public void sendMsg(Object msg){
       currentChannel.writeAndFlush(msg);
    }

}

