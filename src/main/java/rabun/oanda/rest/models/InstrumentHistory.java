package rabun.oanda.rest.models;

public class InstrumentHistory {
    public String instrument;
    public OandaTypes.GranularityType granularity;
    public CandleMid[] candles;
}
