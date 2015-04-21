package rabun.oanda.rest.models;

public class OrderMarket extends Order {
    public int id;
    public int units;
    public OandaTypes.Side side;
    public float takeProfit;
    public float stopLoss;
    public float trailingStop;
}