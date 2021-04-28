package model;

public class TableRow {

    private String value;
    private String you;
    private String op;

    public TableRow(String value, String you, String op) {
        this.value = value;
        this.you = you;
        this.op = op;
    }

    public String getValue() {
        return value;
    }

    public String getYou() {
        return you;
    }

    public String getOp() {
        return op;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setYou(String you) {
        this.you = you;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
