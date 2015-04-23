package rabun.oanda.rest.models;

public abstract class Order {
    public int id;
    public String instrument;
    public String time;
    public float price;
    public OandaTypes.OrderType type;
    public OandaTypes.Side side;
}
