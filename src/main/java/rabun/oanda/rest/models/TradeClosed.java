package rabun.oanda.rest.models;

public class TradeClosed {
    public long id;
    public float price;
    public String instrument;
    public float profit;
    public OandaTypes.Side side;
    public String time;
}
