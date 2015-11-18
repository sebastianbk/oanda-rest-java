package rabun.oanda.rest.models;

public class TransactionDefault extends TransactionSimple {
    public long expiry;
    public OandaTypes.Reason reason;
    public Float lowerBound;
    public Float upperBound;
    public Float takeProfitPrice;
    public Float stopLossPrice;
    public Float trailingStopLossDistance;
}
