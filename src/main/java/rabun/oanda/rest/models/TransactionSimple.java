package rabun.oanda.rest.models;

public class TransactionSimple extends Transaction {
    public String instrument;
    public long units;
    public OandaTypes.Side side;
    public float price;
}
