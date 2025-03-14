package org.app;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client extends ClientSettings{
    private SocketChannel socketChannel;

    public Client(){
        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                socketChannel = ch;
                                socketChannel.pipeline()
                                        .addLast(new StringDecoder(), new StringEncoder())
                                        .addLast( new SimpleChannelInboundHandler<String>(){
                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                                System.out.println(msg);
                                            }
                                        });
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            }catch (Exception e){

            }finally {
                workerGroup.shutdownGracefully();
            }

        }).start();
    }

    public void sendMessage(String str){
        socketChannel.writeAndFlush(str);
    }

    public Boolean hasConnect(){
        return this.socketChannel != null && this.socketChannel.isActive();
    }
}
