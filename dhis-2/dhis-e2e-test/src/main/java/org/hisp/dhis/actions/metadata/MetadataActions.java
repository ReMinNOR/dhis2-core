package org.hisp.dhis.actions.metadata;




import com.google.gson.JsonObject;
import io.restassured.matcher.RestAssuredMatchers;
import org.hisp.dhis.actions.RestApiActions;
import org.hisp.dhis.dto.ApiResponse;
import org.hisp.dhis.helpers.QueryParamsBuilder;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class MetadataActions
    extends RestApiActions
{
    public MetadataActions()
    {
        super( "/metadata" );
    }

    public ApiResponse importMetadata( File file, String... queryParams )
    {
        QueryParamsBuilder queryParamsBuilder = new QueryParamsBuilder();
        queryParamsBuilder.addAll( queryParams );
        queryParamsBuilder.addAll( "importReportMode=FULL" );

        ApiResponse response = postFile( file, queryParamsBuilder );
        response.validate().statusCode( 200 );

        return response;
    }

    public ApiResponse importMetadata( JsonObject object, String... queryParams )
    {
        QueryParamsBuilder queryParamsBuilder = new QueryParamsBuilder();
        queryParamsBuilder.addAll( queryParams );
        queryParamsBuilder.addAll( "atomicMode=OBJECT", "importReportMode=FULL" );

        ApiResponse response = post( object, queryParamsBuilder );
        response.validate().statusCode( 200 );

        return response;
    }

    public ApiResponse importAndValidateMetadata( JsonObject object, String... queryParams )
    {
        ApiResponse response = importMetadata( object, queryParams );

        response.validate().body( "stats.ignored", not(
            equalTo( response.extract( "stats.total" ) ) ) );

        return response;
    }

    public ApiResponse importAndValidateMetadata( File file, String... queryParams )
    {
        ApiResponse response = importMetadata( file, queryParams );

        response.validate().body( "stats.ignored", not(
            equalTo( response.extract( "stats.total" ) ) ) );

        return response;
    }
}
