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

/**
 * POSITION ENDPOINTS
 */
public class PositionEndpoints extends Endpoint {

    private final String positionsRoute = "/v1/accounts/{account_id}/positions";
    private final String positionRoute = "/v1/accounts/{account_id}/positions/{instrument}";

    public PositionEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    /**
     * Get a list of all open positions
     * @param accountId account id
     * @return list of positions
     * @throws UnirestException
     */
    public List<Position> GetPositions(long accountId) throws UnirestException {

        String endpoint = makeEndpoint(accountType, positionsRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));

        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, null, endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        return fillPositions(jsonResponse);
    }

    /**
     * Get the position for an instrument
     *
     * @param accountId account id
     * @param instrument instrument
     * @return position
     * @throws UnirestException
     */
    public Position GetPosition(long accountId, String instrument) throws UnirestException {
        String endpoint = makeEndpoint(accountType, positionRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("instrument", instrument);

        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, null, endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        return fillPosition(jsonResponse);
    }

    /**
     * Close an existing position
     *
     * @param accountId account id
     * @param instrument instrument
     * @return positionClosed model
     * @throws UnirestException
     */
    public PositionClosed ClosePosition(long accountId, String instrument) throws UnirestException {
        String endpoint = makeEndpoint(accountType, positionRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("instrument", instrument);

        HttpResponse<JsonNode> response = this.Delete(routeParams, endpoint);

        if (response.getCode() > 299 || response.getCode() < 200)
            throw new UnirestException(response.getBody().toString());

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
