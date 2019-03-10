package de.danoeh.antennapod.core.syndication.handler;

import de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type;

public class UnsupportedFeedtypeException extends Exception {
    private static final long serialVersionUID = 9105878964928170669L;
    private String message = null;
    private String rootElement;
    private final Type type;

    public UnsupportedFeedtypeException(Type type) {
        this.type = type;
    }

    public UnsupportedFeedtypeException(Type type, String rootElement) {
        this.type = type;
        this.rootElement = rootElement;
    }

    public UnsupportedFeedtypeException(String message) {
        this.message = message;
        this.type = Type.INVALID;
    }

    public Type getType() {
        return this.type;
    }

    public String getRootElement() {
        return this.rootElement;
    }

    public String getMessage() {
        String str = this.message;
        if (str != null) {
            return str;
        }
        if (this.type == Type.INVALID) {
            return "Invalid type";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Type ");
        stringBuilder.append(this.type);
        stringBuilder.append(" not supported");
        return stringBuilder.toString();
    }
}
