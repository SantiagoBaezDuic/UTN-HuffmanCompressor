package huffman.util;

public class MessageReplacer {
    private static final StringBuilder messageContainer = new StringBuilder();

    public static String replaceProgressMessage(String baseMessage, long percentage){
        messageContainer.setLength(0);
        messageContainer.append(baseMessage);
        messageContainer.append(percentage);
        return messageContainer.toString();
    }

    public static String replaceInfoMessage(String newMessage){
        messageContainer.setLength(0);
        messageContainer.append(newMessage);
        return messageContainer.toString();
    }
}
