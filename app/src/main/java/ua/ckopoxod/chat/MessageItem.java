package ua.ckopoxod.chat;

/**
 * Created by CK0P0X0D on 30.01.2015.
 */
public class MessageItem {
    String message;
    String youmy;


    MessageItem(String _message, String _youmy) {
        message = _message;   //сообщение
        youmy = _youmy;   //false-полученное, true-отправленное
    }
}