package rabun.oanda.rest.models;

public class Position {
    public String instrument;
    public int units;
    public OandaTypes.Side side;
    public float avgPrice;
}