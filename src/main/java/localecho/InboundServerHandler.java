/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package localecho;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;

public class InboundServerHandler extends ChannelInboundHandlerAdapter
{

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg )
    {
        // Write back as received
        System.out.println("inbound_sendBuf=" + ctx.channel().config().getOption( ChannelOption.SO_SNDBUF ));
        System.out.println("inbound_rcvBuf=" + ctx.channel().config().getOption( ChannelOption.SO_RCVBUF ));
        System.out.println("waterhigh:"+ ctx.channel().config().getOption( ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK ));
        System.out.println("waterlow:"+ ctx.channel().config().getOption( ChannelOption.WRITE_BUFFER_LOW_WATER_MARK ));

//        System.out.println( "Server receive msgSize" + ((String) msg).getBytes().length );
        if ( ((String) msg).contains( "stop" ) )
        {
            final Integer sleepTimeout = Integer.valueOf( ((String) msg).split( "#" )[1] );
            System.out.println( "Turn off autoread for=" + sleepTimeout );
            ctx.channel().config().setAutoRead( false );
            new Thread( () ->
                        {
                            try
                            {
                                Thread.sleep( sleepTimeout );
                            }
                            catch ( InterruptedException e )
                            {
                                e.printStackTrace();
                            }
                            System.out.println( "change the auto read" );
                            ctx.channel().config().setAutoRead( true );
                        } ).start();
        }
        else if ( msg.equals( "start" ) )
        {
            ctx.channel().config().setAutoRead( true );
        }

        ctx.write( "ACK{" + msg + "}" );
    }

    @Override
    public void channelReadComplete( ChannelHandlerContext ctx )
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause )
    {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelWritabilityChanged( ChannelHandlerContext ctx ) throws Exception
    {
        System.out.println( "Server Writability is changed" );
        super.channelWritabilityChanged( ctx );
    }
}
