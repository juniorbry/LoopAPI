package loopdospru.loopapi_1.commandapi;

public class CommandEditor {
    private final String[] args;

    public CommandEditor(String[] args) {
        this.args = args;
    }

    public StringBuilder generateMessage(int startIndex) {
        StringBuilder message = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        return message;
    }

    public String getString(int index) {
        if (index >= 0 && index < args.length) {
            return args[index];
        } else {
            return "";
        }
    }

    public String[] getArgs() {
        return args;
    }
}
