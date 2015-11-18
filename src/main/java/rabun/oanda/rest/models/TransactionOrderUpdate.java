package rabun.oanda.rest.models;

public class TransactionOrderUpdate extends Transaction {
    public long units;
    public float price;
    public OandaTypes.Reason reason;
    public long expiry;
}

