package rabun.oanda.rest.models;

public class TransactionSimple extends Transaction {
    public String instrument;
    public int units;
    public OandaTypes.Side side;
    public float price;
}
