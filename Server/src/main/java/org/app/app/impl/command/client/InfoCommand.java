package org.app.app.impl.command.client;

import io.netty.channel.ChannelHandlerContext;
import org.app.adapter.rest.ServerHandler;
import org.app.app.api.Command;

import java.util.Map;

public class InfoCommand implements Command {
    @Override
    public void execute(ChannelHandlerContext ctx, Map<String, String> params, ServerHandler handler) {
        String info = """
                Доступные команды:
                -------------------------------------------------
                register -r=username
                    Зарегистрировать пользователи под указанными именем.
                
                login -u=username
                    Подключиться к серверу с указанным именем пользователя.
                
                create topic -n=Название
                    Создать новый топик с уникальным названием.
                
                create vote -t=Топик.
                    Создать голосование в указанном топике.
                
                view [-t=Топик] [-v=Голосование]
                    Просмотреть список топиков, либо список голосований в топике, либо подробную информацию о голосовании.
                
                vote -t=Топик -v=Голосование
                    Проголосовать.
                
                delete -t=Топик -v=Голосование
                    Удалить голосование (может удалить только создатель голосования).
                
                info
                    Вывести эту справочную информацию о командах.
                
                exit
                    Завершить работу приложения.
                -------------------------------------------------
                """;

        ctx.writeAndFlush(info);
    }
}
