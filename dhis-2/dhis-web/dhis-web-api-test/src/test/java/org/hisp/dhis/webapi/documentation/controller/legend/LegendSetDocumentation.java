package org.hisp.dhis.webapi.documentation.controller.legend;



import org.apache.http.HttpStatus;
import org.hisp.dhis.legend.LegendSet;
import org.hisp.dhis.webapi.documentation.controller.AbstractWebApiTest;

/**
 * @author Viet Nguyen <viet@dhis.org>
 */
public class LegendSetDocumentation
    extends AbstractWebApiTest<LegendSet>
{
    @Override
    protected void setStatues()
    {
        createdStatus = HttpStatus.SC_CREATED;
        updateStatus = HttpStatus.SC_NO_CONTENT;
        deleteStatus = HttpStatus.SC_NO_CONTENT;
    }
}

