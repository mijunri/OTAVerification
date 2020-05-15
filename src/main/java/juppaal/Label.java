package juppaal;

public class Label {
    private String kind;
    private String text;

    public Label(String kind, String text) {
        this.kind = kind;
        this.text = text;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Label{" +
                "kind='" + kind + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
