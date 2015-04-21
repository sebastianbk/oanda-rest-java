package rabun.oanda.rest.models;

public class TransactionOrderUpdate extends Transaction {
    public int units;
    public float price;
    public OandaTypes.Reason reason;
    public Integer expiry;
}

