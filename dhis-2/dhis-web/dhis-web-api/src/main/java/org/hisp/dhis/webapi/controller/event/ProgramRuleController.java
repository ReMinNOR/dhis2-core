package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.dxf2.webmessage.DescriptiveWebMessage;
import org.hisp.dhis.feedback.Status;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.programrule.ProgramRule;
import org.hisp.dhis.programrule.engine.ProgramRuleEngineService;
import org.hisp.dhis.rules.models.RuleValidationResult;
import org.hisp.dhis.schema.descriptors.ProgramRuleSchemaDescriptor;
import org.hisp.dhis.util.ObjectUtils;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author markusbekken
 */
@Controller
@RequestMapping( value = ProgramRuleSchemaDescriptor.API_ENDPOINT )
public class ProgramRuleController
    extends AbstractCrudController<ProgramRule>
{
    private final I18nManager i18nManager;

    private final ProgramRuleEngineService programRuleEngineService;

    public ProgramRuleController( I18nManager i18nManager, ProgramRuleEngineService programRuleEngineService )
    {
        this.i18nManager = i18nManager;
        this.programRuleEngineService = programRuleEngineService;
    }

    @RequestMapping( value = "/condition/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void validateCondition( @RequestBody String condition, @RequestParam String programId, HttpServletResponse response )
    {
        I18n i18n = i18nManager.getI18n();

        DescriptiveWebMessage message = new DescriptiveWebMessage();


        RuleValidationResult result = programRuleEngineService.getDescription( condition, programId );

        if ( result.isValid() )
        {
            message.setDescription( result.getDescription() );

            message.setStatus( Status.OK );

            message.setMessage( i18n.getString( ProgramIndicator.VALID ) );
        }
        else
        {
            message.setDescription( ObjectUtils.firstNonNull( result.getErrorMessage(), result.getException().getMessage() ) );

            message.setStatus( Status.ERROR );

            message.setMessage( i18n.getString( ProgramIndicator.EXPRESSION_NOT_VALID ) );
        }

        webMessageService.sendJson( message, response );
    }
}
