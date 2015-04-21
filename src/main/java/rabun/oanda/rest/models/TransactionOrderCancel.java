package rabun.oanda.rest.models;


public class TransactionOrderCancel extends Transaction {
    public int orderId;
    public OandaTypes.Reason reason;
}
