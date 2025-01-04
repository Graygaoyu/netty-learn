package com.gencent.main;

import com.gencent.handler.ResponseHandler;
import com.gencent.handler.SendMessageHandler;
import com.gencent.pojo.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Scanner;

public class MessageClient {

    private Channel channel;

    private SendMessageHandler sendMessageHandler;

    private ResponseHandler responseHandler;

    public void connect(String ip, int port) throws InterruptedException {
//        NioEventLoopGroup group = new NioEventLoopGroup();
//        sendMessageHandler = new SendMessageHandler();
//        responseHandler = new ResponseHandler();
//        try {
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(group)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new ChannelInitializer<NioSocketChannel>() {
//                        @Override
//                        protected void initChannel(NioSocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
//                            ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
//                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
//                            ch.pipeline().addLast(new ProtobufEncoder());
//                            ch.pipeline().addLast(sendMessageHandler);
////                            ch.pipeline().addLast(responseHandler);
//                        }
//                    });
//            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
//            channel = channelFuture.channel();
//
//            String command;
//            Scanner scanner = new Scanner(System.in);
//            while (scanner.hasNextLine()) {
////                System.out.println("command:");
//                command = scanner.nextLine();
////                System.out.println(command);
//                String[] item = command.split(":");
//                MessageProto.Message message = null;
//                if (item[1].equals("login")) {
//                    message = MessageProto.Message.newBuilder()
//                            .setUserId(Long.parseLong(item[0]))
//                            .setCommand(MessageProto.Command.LOGIN)
//                            .setTs(System.currentTimeMillis()).build();
//                } else if (item[1].equals("send")) {
//                    message = MessageProto.Message.newBuilder()
//                            .setUserId(Long.parseLong(item[0]))
//                            .setCommand(MessageProto.Command.SEND)
//                            .setData(item[2])
//                            .setTs(System.currentTimeMillis()).build();
//                }
//                channel.writeAndFlush(message);
//            }
//            channel.closeFuture().sync();
//
//        } finally {
//            group.shutdownGracefully();
//            Thread.sleep(5000);
//            try {
//                connect(ip, port);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public static void main(String[] args) throws InterruptedException {
        new MessageClient().connect("127.0.0.1", 9777);

    }


}
