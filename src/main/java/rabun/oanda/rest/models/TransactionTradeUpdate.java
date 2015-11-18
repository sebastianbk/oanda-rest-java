package rabun.oanda.rest.models;

public class TransactionTradeUpdate extends Transaction {
    public long units;
    public OandaTypes.Side side;
    public float stopLossPrice;
    public long tradeId;

}
