package org.app.adapter.rest.server;

import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.app.app.api.ServerCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class ServerCommandDispatcher implements Runnable {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public ServerCommandDispatcher(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Завершаем работу сервера...");
                shutdownNetty();
                break;
            }

            String[] tokens = input.split("\\s+");
            if (tokens.length == 0) {
                continue;
            }
            String commandName = tokens[0];
            String paramValue = tokens[1];

            Map<String, String> params = new HashMap<>();
            params.put("filename", paramValue);

            ServerCommand command = ServerCommandRegistry.getInstance().getCommand(commandName);
            if (command != null) {
                command.execute(null, params, null);
            } else {
                System.out.println("Неизвестная команда: " + commandName);
            }
        }
        scanner.close();
    }

    private void shutdownNetty() {
        try {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }
}
