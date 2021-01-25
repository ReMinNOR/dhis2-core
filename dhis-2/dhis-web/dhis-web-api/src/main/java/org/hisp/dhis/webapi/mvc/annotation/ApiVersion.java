package org.hisp.dhis.webapi.mvc.annotation;



import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Target( { ElementType.TYPE, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface ApiVersion
{
    @AliasFor( "include" )
    DhisApiVersion[] value() default DhisApiVersion.ALL;

    @AliasFor( "value" )
    DhisApiVersion[] include() default DhisApiVersion.ALL;

    DhisApiVersion[] exclude() default {};
}
