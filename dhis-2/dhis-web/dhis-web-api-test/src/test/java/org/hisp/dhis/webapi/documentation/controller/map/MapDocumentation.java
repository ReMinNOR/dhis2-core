package org.hisp.dhis.webapi.documentation.controller.map;



import org.apache.http.HttpStatus;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.webapi.documentation.controller.AbstractWebApiTest;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
public class MapDocumentation
    extends AbstractWebApiTest<Map>
{
    @Override
    protected void setStatues()
    {
        createdStatus = HttpStatus.SC_CREATED;
        updateStatus = HttpStatus.SC_NO_CONTENT;
    }
}

