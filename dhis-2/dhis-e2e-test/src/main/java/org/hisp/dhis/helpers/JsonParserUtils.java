package org.hisp.dhis.helpers;




import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class JsonParserUtils
{
    private static JsonParser parser = new JsonParser();

    public static JsonObject toJsonObject( Object object )
    {
        if ( object instanceof String )
        {
            return parser.parse( (String) object ).getAsJsonObject();
        }

        JsonObject jsonObject = parser.parse( new Gson().toJson( object ) ).getAsJsonObject();

        return jsonObject;
    }
}
