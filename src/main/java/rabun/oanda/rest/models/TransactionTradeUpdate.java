package rabun.oanda.rest.models;

public class TransactionTradeUpdate extends Transaction {
    public int units;
    public OandaTypes.Side side;
    public float stopLossPrice;
    public int tradeId;

}
