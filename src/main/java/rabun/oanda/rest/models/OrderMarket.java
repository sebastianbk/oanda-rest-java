package rabun.oanda.rest.models;

public class OrderMarket extends Order {
    public long units;
    public float takeProfit;
    public float stopLoss;
    public float trailingStop;
}