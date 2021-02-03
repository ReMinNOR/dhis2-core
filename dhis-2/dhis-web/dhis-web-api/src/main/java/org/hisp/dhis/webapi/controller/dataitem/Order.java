package org.hisp.dhis.webapi.controller.dataitem;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

import java.util.Set;

public class Order
{
    public enum Attribute
    {
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
            return of( Order.Attribute.values() ).map( Order.Attribute::getName ).collect( toSet() );
        }
    }

    public enum Nature
    {
        ASC( "asc" ),
        DESC( "desc" );

        private String value;

        Nature( final String value )
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public static Set<String> getValues()
        {
            return of( Order.Nature.values() ).map( Order.Nature::getValue ).collect( toSet() );
        }
    }
}
