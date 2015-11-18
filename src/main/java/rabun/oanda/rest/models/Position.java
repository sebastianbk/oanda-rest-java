package rabun.oanda.rest.models;

public class Position {
    public String instrument;
    public long units;
    public OandaTypes.Side side;
    public float avgPrice;
}