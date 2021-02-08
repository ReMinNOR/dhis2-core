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
        DISPLAY_NAME( "displayName" ),
        PROGRAM_ID( "programId" ),
        ID( "id" );

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

        Operation( final String abbreviation )
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

    public enum Combination
    {
        DIMENSION_TYPE_IN( "dimensionItemType:in:" ),
        DIMENSION_TYPE_EQUAL( "dimensionItemType:eq:" ),
        VALUE_TYPE_IN( "valueType:in:" ),
        VALUE_TYPE_EQUAL( "valueType:eq:" ),
        NAME_ILIKE( "name:ilike:" ),
        DISPLAY_NAME_ILIKE( "displayName:ilike:" ),
        PROGRAM_ID_EQUAL( "programId:eq:" ),
        ID_ILIKE( "id:ilike:" );

        private String combination;

        Combination( final String combination )
        {
            this.combination = combination;
        }

        public String getCombination()
        {
            return this.combination;
        }

        public static Set<String> getCombinations()
        {
            return of( Combination.values() ).map( Combination::getCombination ).collect( toSet() );
        }
    }
}
