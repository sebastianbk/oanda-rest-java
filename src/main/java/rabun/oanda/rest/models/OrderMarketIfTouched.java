package rabun.oanda.rest.models;

public class OrderMarketIfTouched extends Order {
    public long units;
    public float takeProfit;
    public float stopLoss;
    public String expiry;
    public float upperBound;
    public float lowerBound;
    public float trailingStop;
}
