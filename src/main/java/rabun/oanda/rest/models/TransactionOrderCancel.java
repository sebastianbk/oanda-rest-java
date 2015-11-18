package rabun.oanda.rest.models;


public class TransactionOrderCancel extends Transaction {
    public long orderId;
    public OandaTypes.Reason reason;
}
