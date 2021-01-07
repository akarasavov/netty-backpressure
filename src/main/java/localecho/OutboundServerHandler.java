package localecho;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutboundServerHandler extends ChannelOutboundHandlerAdapter
{
    @Override
    public void read( ChannelHandlerContext ctx ) throws Exception
    {
        super.read( ctx );
    }

    @Override
    public void write( ChannelHandlerContext ctx, Object msg, ChannelPromise promise ) throws Exception
    {
        System.out.println( "Server send=" + msg );
        super.write( ctx, msg, promise );
    }
}
