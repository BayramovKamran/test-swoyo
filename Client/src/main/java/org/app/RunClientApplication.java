package org.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

public class RunClientApplication {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client();

        int coutTry = 0;
        while((!client.hasConnect()) && (coutTry < 10)){
            sleep(500);
            coutTry++;
        }

        if (coutTry >= 10){
            System.out.println("Ошибка подключения");
            exit(-1);
        }

        System.out.println("Введите команду info для получения информации о командах");

        while (client.hasConnect()){
            String in = scanner.nextLine();
            if (in.startsWith("create vote -t=")) {
                String topic = in.substring("create vote -t=".length()).trim();
                String fullCommand = buildVoteCommand(topic, scanner);

                client.sendMessage(fullCommand);
            } else {
                client.sendMessage(in);
            }
        }
    }

    private static String buildVoteCommand(String topic, Scanner scanner) {
        System.out.println("Введите название голосования (уникальное имя):");
        String voteName = scanner.nextLine();

        System.out.println("Введите тему голосования (описание):");
        String voteDesc = scanner.nextLine();

        System.out.println("Введите количество вариантов ответа:");
        int optionCount = 0;
        while (true) {
            try {
                optionCount = Integer.parseInt(scanner.nextLine());
                if (optionCount > 0) break;
                else System.out.println("Количество вариантов должно быть больше нуля. Попробуйте ещё раз:");
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число:");
            }
        }

        List<String> options = new ArrayList<>();
        for (int i = 1; i <= optionCount; i++) {
            System.out.println("Введите вариант ответа " + i + ":");
            options.add(scanner.nextLine());
        }

        StringBuilder command = new StringBuilder("create vote -t=" + topic);
        command.append(" -name=").append(voteName);
        command.append(" -desc=").append(voteDesc);
        command.append(" -options=").append(String.join(",", options));

        return command.toString();
    }
}