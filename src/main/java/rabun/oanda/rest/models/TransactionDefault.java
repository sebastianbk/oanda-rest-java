package rabun.oanda.rest.models;

public class TransactionDefault extends TransactionSimple {
    public Integer expiry;
    public OandaTypes.Reason reason;
    public Float lowerBound;
    public Float upperBound;
    public Float takeProfitPrice;
    public Float stopLossPrice;
    public Float trailingStopLossDistance;
}
