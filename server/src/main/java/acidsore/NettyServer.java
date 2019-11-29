package acidsore;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;


public class NettyServer implements Runnable {
    private final int port;
    public static AttributeKey<String> folder = new AttributeKey<>("folder");//personal clients folder

    public NettyServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        boolean dbAnswer = AuthService.connect();
        if (dbAnswer) System.out.println("Сервер запущен!");
        else System.out.println("Server error");

        EventLoopGroup eventWork = new NioEventLoopGroup();
        EventLoopGroup eventBoss = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(eventBoss, eventWork)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer());

            bootstrap.bind(this.port).sync().channel().closeFuture().sync();
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
        finally {
            eventBoss.shutdownGracefully();
            eventWork.shutdownGracefully();
            AuthService.disconnect();
        }
    }
}
