package org.hisp.dhis.webapi.documentation.common;



import com.google.common.collect.Lists;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public final class ResponseDocumentation
{
    public static List<FieldDescriptor> identifiableObject()
    {
        return Lists.newArrayList(
            fieldWithPath( "id" ).description( "Identifier" ),
            fieldWithPath( "name" ).description( "Name" ),
            fieldWithPath( "displayName" ).description( "Property" ),
            fieldWithPath( "code" ).description( "Code" ),
            fieldWithPath( "created" ).description( "Property" ),
            fieldWithPath( "lastUpdated" ).description( "Property" ),
            fieldWithPath( "href" ).description( "Property" ),
            fieldWithPath( "publicAccess" ).description( "Property" ),
            fieldWithPath( "externalAccess" ).description( "Property" ),
            fieldWithPath( "access" ).description( "Property" ),
            fieldWithPath( "userGroupAccesses" ).description( "Property" ),
            fieldWithPath( "attributeValues" ).description( "Property" ),
            fieldWithPath( "translations" ).description( "Property" )
        );
    }

    public static List<FieldDescriptor> nameableObject()
    {
        return Lists.newArrayList(
            fieldWithPath( "shortName" ).description( "Property" ),
            fieldWithPath( "displayShortName" ).description( "Property" ),
            fieldWithPath( "description" ).description( "Property" ),
            fieldWithPath( "displayDescription" ).description( "Property" )
        );
    }

    public static List<FieldDescriptor> pager()
    {
        return Lists.newArrayList(
            fieldWithPath( "pager.page" ).description( "Property" ),
            fieldWithPath( "pager.pageCount" ).description( "Property" ),
            fieldWithPath( "pager.total" ).description( "Property" ),
            fieldWithPath( "pager.pageSize" ).description( "Property" )
        );
    }
}
