package rabun.oanda.rest.models;

import java.util.ArrayList;
import java.util.List;

public class Candle<T> {

    public Candle(){
        this.candles = new ArrayList<>();
    }

    public String instrument;
    public OandaTypes.GranularityType granularity;
    public List<T> candles;
}
