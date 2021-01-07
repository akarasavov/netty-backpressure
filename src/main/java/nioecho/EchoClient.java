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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import localecho.InboundClientHandler;
import localecho.OutboundClientHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class EchoClient
{

    static final String HOST = System.getProperty( "host", "127.0.0.1" );
    static final int PORT = Integer.parseInt( System.getProperty( "port", "8007" ) );

    public static void main( String[] args ) throws Exception
    {

        // Configure the client.
        int sendBuffer = 32;
        int rcvBuffer = 32;
        int lowWaterMark = 32;
        int highWaterMark = 64;
        EventLoopGroup group = new NioEventLoopGroup();
        final String data = DataGenerator.generateNBytes( Integer.MAX_VALUE - 10 );
        try
        {
            Bootstrap b = new Bootstrap();
            b.group( group )
             .channel( NioSocketChannel.class )
             .handler( new ChannelInitializer<SocketChannel>()
             {
                 @Override
                 public void initChannel( SocketChannel ch ) throws Exception
                 {
                     ch.pipeline().addLast(
                             new StringDecoder(),
                             new StringEncoder(),
                             new LoggingHandler( LogLevel.INFO ),
                             new InboundClientHandler(),
                             new OutboundClientHandler()
                     );

                     System.out.println("senbuf:"+ ch.config().getSendBufferSize());
                     System.out.println("waterhigh:"+ ch.config().getWriteBufferHighWaterMark());
                     System.out.println("waterlow:"+ ch.config().getWriteBufferLowWaterMark());
                     System.out.println("recbuf:"+ ch.config().getReceiveBufferSize());
                 }
             } );
            b.option( ChannelOption.SO_SNDBUF, sendBuffer * 1024 );
            b.option( ChannelOption.SO_RCVBUF, rcvBuffer * 1024 );
            b.option( ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, lowWaterMark * 1024 );
            b.option( ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, highWaterMark * 1024 );
            // Start the client.
            Channel ch = b.connect( HOST, PORT ).sync().channel();

            System.out.println( "Enter text (quit to end)" );
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

            for ( ; ; )
            {
                String line = in.readLine();
                if ( line == null || "quit".equalsIgnoreCase( line ) )
                {
                    break;
                }

                // Sends the received line to the server.
                if ( !ch.isWritable() )
                {
                    System.out.println( "Client can't write" );
                }
                else
                {
                    if ( line.contains( "stop" ) )
                    {
                        lastWriteFuture = ch.writeAndFlush( line ).sync();
                    }
                    else
                    {
                        final Integer amount = Integer.valueOf( line );
                        final String chunk = data.substring( 0, amount );
                        ch.writeAndFlush( chunk );
                    }
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if ( lastWriteFuture != null )
            {
                lastWriteFuture.awaitUninterruptibly();
            }
        }
        finally
        {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
