package rabun.oanda.rest.models;

public class Order {
    public long id;
    public String instrument;
    public String time;
    public float price;
    public OandaTypes.OrderType type;
    public OandaTypes.Side side;
}
