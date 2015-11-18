package rabun.oanda.rest.models;

public class TransactionStopOrderCreate extends TransactionSimple {
    public long expiry;
    public OandaTypes.Reason reason;
}
