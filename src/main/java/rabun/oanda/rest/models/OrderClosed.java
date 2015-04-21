package rabun.oanda.rest.models;

public class OrderClosed {
    public int id;
    public String instrument;
    public OandaTypes.Side side;
    public float price;
    public String time;
}
