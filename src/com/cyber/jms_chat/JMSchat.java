package com.cyber.jms_chat;

import java.util.Scanner;

/**
 * Created by vadim on 30.12.14.
 */
public class JMSchat {

    public static void main(String[] args) {
        String nick = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите свой ник:");
        while (nick == null || nick.equals("")) nick = scanner.nextLine();

        JMSsender sender = new JMSsender(nick);
        JMSreceiver receiver = new JMSreceiver();

        String str = "";
        while (true) {
            str = scanner.nextLine();
            if (str.equals("exit")) {
                sender.stop();
                receiver.stop();
                break;
            }
            sender.send(str);
        }
    }
}
