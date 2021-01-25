package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.render.ObjectValueTypeRenderingOption;
import org.hisp.dhis.render.StaticRenderingConfiguration;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping( value = "/staticConfiguration/" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class StaticRenderingConfigurationController
{

    /**
     * Returns the constraints of ValueType renderingTypes defined in the StaticRenderingConfiguration
     * @return a Set of rules representing application constraints for ValueType/RenderingType combinations
     */
    @GetMapping( value = "renderingOptions" )
    public Set<ObjectValueTypeRenderingOption> getMapping()
    {
        return StaticRenderingConfiguration.RENDERING_OPTIONS_MAPPING;
    }

}
