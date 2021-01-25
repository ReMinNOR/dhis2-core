package org.hisp.dhis.security.authority;



import org.hisp.dhis.schema.SchemaService;

import java.util.Collection;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class SchemaAuthoritiesProvider
    implements SystemAuthoritiesProvider
{
    private SchemaService schemaService;

    public SchemaAuthoritiesProvider( SchemaService schemaService )
    {
        this.schemaService = schemaService;
    }

    @Override
    public Collection<String> getSystemAuthorities()
    {
        return schemaService.collectAuthorities();
    }
}
