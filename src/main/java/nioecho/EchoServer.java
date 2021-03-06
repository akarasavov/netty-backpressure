/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package nioecho;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import localecho.InboundServerHandler;
import localecho.OutboundServerHandler;

public final class EchoServer
{

    static final int PORT = Integer.parseInt( System.getProperty( "port", "8007" ) );

    public static void main( String[] args ) throws Exception
    {

        // Configure the server.
        int sendBuffer = 32;
        int rcvBuffer = 32;
        int lowWaterMark = 32;
        int highWaterMark = 64;
        EventLoopGroup bossGroup = new NioEventLoopGroup( 1 );
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group( bossGroup, workerGroup )
             .channel( NioServerSocketChannel.class )
             .handler( new LoggingHandler( LogLevel.INFO ) )
             .childHandler( new ChannelInitializer<SocketChannel>()
             {
                 @Override
                 public void initChannel( SocketChannel ch ) throws Exception
                 {
                     ch.pipeline().addLast(
                             new StringDecoder(),
                             new StringEncoder(),
                             new LoggingHandler( LogLevel.INFO ),
                             new InboundServerHandler(),
                             new OutboundServerHandler()

                     );

                     System.out.println( "senbuf:" + ch.config().getSendBufferSize() );
                     System.out.println( "waterhigh:" + ch.config().getWriteBufferHighWaterMark() );
                     System.out.println( "waterlow:" + ch.config().getWriteBufferLowWaterMark() );
                     System.out.println( "recbuf:" + ch.config().getReceiveBufferSize() );
                 }
             } );
            b.childOption( ChannelOption.SO_SNDBUF, sendBuffer * 1024 );
            b.childOption( ChannelOption.SO_RCVBUF, rcvBuffer * 1024 );
            b.childOption( ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, lowWaterMark * 1024 );
            b.childOption( ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, highWaterMark * 1024 );

            // Start the server.
            ChannelFuture f = b.bind( PORT ).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        }
        finally
        {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
