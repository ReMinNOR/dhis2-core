package org.hisp.dhis.webapi.controller.dataitem;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

import java.util.Set;

public class Filter
{
    public enum Attribute
    {
        DIMENSION_TYPE( "dimensionItemType" ),
        VALUE_TYPE( "valueType" ),
        NAME( "name" ),
        DISPLAY_NAME( "displayName" );

        private String name;

        Attribute( final String name )
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public static Set<String> getNames()
        {
            return of( Attribute.values() ).map( Attribute::getName ).collect( toSet() );
        }
    }

    public enum Operation
    {
        EQ( "eq" ),
        IN( "in" ),
        ILIKE( "ilike" );

        private String abbreviation;

        Operation( String abbreviation )
        {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation()
        {
            return this.abbreviation;
        }

        public static Set<String> getAbbreviations()
        {
            return of( Operation.values() ).map( Operation::getAbbreviation ).collect( toSet() );
        }
    }
}
