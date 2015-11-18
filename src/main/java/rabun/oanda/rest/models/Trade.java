package rabun.oanda.rest.models;

public class Trade {
    public long id;
    public long units;
    public OandaTypes.Side side;
    public String instrument;
    public String time;
    public float price;
    public float takeProfit;
    public float stopLoss;
    public float trailingStop;
    public float trailingAmount;
}
