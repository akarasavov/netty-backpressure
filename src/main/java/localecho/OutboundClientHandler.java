package localecho;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutboundClientHandler extends ChannelOutboundHandlerAdapter
{

    @Override
    public void write( ChannelHandlerContext ctx, Object msg, ChannelPromise promise ) throws Exception
    {
        System.out.println("outbound_sendBuf=" + ctx.channel().config().getOption( ChannelOption.SO_SNDBUF ));
        System.out.println("outbound_rcvBuf=" + ctx.channel().config().getOption( ChannelOption.SO_RCVBUF ));
        System.out.println("waterhigh:"+ ctx.channel().config().getOption( ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK ));
        System.out.println("waterlow:"+ ctx.channel().config().getOption( ChannelOption.WRITE_BUFFER_LOW_WATER_MARK ));

        final String str = (String) msg;
//        System.out.println( "Client send sizeBytes=" + str.getBytes().length);
        super.write( ctx, msg, promise );
    }
}
