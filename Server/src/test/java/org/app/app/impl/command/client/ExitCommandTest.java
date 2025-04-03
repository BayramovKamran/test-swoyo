package org.app.app.impl.command.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.rest.ServerHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExitCommandTest {

    @Test
    public void testExecute_ClosesChannel() {
        ExitCommand command = new ExitCommand();
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        ServerHandler handler = Mockito.mock(ServerHandler.class);
        Channel channel = Mockito.mock(Channel.class);
        ChannelFuture future = Mockito.mock(ChannelFuture.class);

        when(ctx.channel()).thenReturn(channel);
        when(channel.isActive()).thenReturn(true);
        when(ctx.writeAndFlush(anyString())).thenReturn(future);

        command.execute(ctx, Collections.emptyMap(), handler);

        verify(ctx).writeAndFlush("Завершаем работу программы...");
        verify(future).addListener(any());
    }
}
