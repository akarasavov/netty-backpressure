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

public class InboundServerHandler extends ChannelInboundHandlerAdapter
{

    @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg )
    {
        // Write back as received
        System.out.println( "Server receive msg=" + msg );
        if ( msg.equals( "stop" ) )
        {
            ctx.channel().config().setAutoRead( false );
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
