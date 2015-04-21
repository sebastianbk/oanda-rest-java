package rabun.oanda.rest.models;

public class TransactionStopOrderCreate extends TransactionSimple {
    public int expiry;
    public OandaTypes.Reason reason;
}
