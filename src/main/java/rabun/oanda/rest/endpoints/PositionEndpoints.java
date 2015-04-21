package rabun.oanda.rest.endpoints;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionEndpoints extends Endpoint {

    private final String positionsRoute = "/v1/accounts/{account_id}/positions";
    private final String positionRoute = "/v1/accounts/{account_id}/positions/{instrument}";

    public PositionEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    public List<Position> GetPositions(int accountId) throws UnirestException {

        String endpoint = makeEndpoint(accountType, positionsRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));

        HttpResponse<JsonNode> response = this.Get(routeParams, null, endpoint);
        return fillPositions(response);
    }

    public Position GetPosition(int accountId, String instrument) throws UnirestException {
        String endpoint = makeEndpoint(accountType, positionRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("instrument", instrument);

        HttpResponse<JsonNode> response = this.Get(routeParams, null, endpoint);
        return fillPosition(response);
    }

    public PositionClosed ClosePosition(int accountId, String instrument) throws UnirestException {
        String endpoint = makeEndpoint(accountType, positionRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("instrument", instrument);

        HttpResponse<JsonNode> response = this.Delete(routeParams, endpoint);
        return fillPositionClosed(response);
    }

    private List<Position> fillPositions(HttpResponse<JsonNode> response){
        JSONObject object = response.getBody().getObject();
        JSONArray array = object.getJSONArray("positions");
        List<Position> positions = new ArrayList<>();

        for (int i=0; i< array.length(); i++) {

            Position position = new Position();
            position.avgPrice = (float) object.getDouble("avgPrice");
            position.instrument = object.getString("instrument");
            position.side = OandaTypes.Side.valueOf(object.getString("side"));
            position.units = object.getInt("units");

            positions.add(position);
        }

        return positions;

    }

    private Position fillPosition(HttpResponse<JsonNode> response){
        JSONObject object = response.getBody().getObject();
        Position position = new Position();

        position.avgPrice = (float) object.getDouble("avgPrice");
        position.instrument = object.getString("instrument");
        position.side = OandaTypes.Side.valueOf(object.getString("side"));
        position.units = object.getInt("units");

        return position;
    }

    private PositionClosed fillPositionClosed(HttpResponse<JsonNode> response){
        JSONObject object = response.getBody().getObject();
        PositionClosed positionClosed = new PositionClosed();

        positionClosed.instrument = object.getString("instrument");
        positionClosed.price = (float) object.getDouble("price");
        positionClosed.totalUnits = object.getInt("totalUnits");

        JSONArray array = object.getJSONArray("ids");
        positionClosed.ids = new int[array.length()];

        for(int i = 0; i < array.length(); i++){
            positionClosed.ids[i] = array.getInt(i);
        }

        return positionClosed;
    }



}
