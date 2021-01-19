package org.hisp.dhis.programrule;

/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.programrule.ProgramRuleActionEvaluationTime.*;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Markus Bekken
 */
public enum ProgramRuleActionType
{
    DISPLAYTEXT( "displaytext" ),
    DISPLAYKEYVALUEPAIR( "displaykeyvaluepair" ),
    HIDEFIELD( "hidefield" ),
    HIDESECTION( "hidesection" ),
    HIDEPROGRAMSTAGE( "hideprogramstage" ),
    ASSIGN( "assign", ON_DATA_ENTRY, ON_COMPLETE ),
    SHOWWARNING( "showwarning" ),
    WARNINGONCOMPLETE( "warningoncomplete" ),
    SHOWERROR( "showerror" ),
    ERRORONCOMPLETE( "erroroncomplete" ),
    CREATEEVENT( "createevent" ),
    SETMANDATORYFIELD( "setmandatoryfield", ON_DATA_ENTRY ),
    SENDMESSAGE( "sendmessage", ON_DATA_ENTRY, ON_COMPLETE ),
    SCHEDULEMESSAGE( "schedulemessage", ON_DATA_ENTRY, ON_COMPLETE ),
    HIDEOPTION( "hideoption" ),
    SHOWOPTIONGROUP( "showoptiongroup" ),
    HIDEOPTIONGROUP( "hideoptiongroup" );

    final String value;

    final Set<ProgramRuleActionEvaluationTime> whenToRun;

    private static final Set<ProgramRuleActionType> IMPLEMENTED_ACTIONS = new ImmutableSet.Builder<ProgramRuleActionType>()
        .add( SENDMESSAGE, SCHEDULEMESSAGE, ASSIGN ).build(); // Actions having
                                                              // back end
                                                              // implementation

    private static final Set<ProgramRuleActionType> DATA_LINKED_TYPES = new ImmutableSet.Builder<ProgramRuleActionType>()
        .add( HIDEFIELD, SETMANDATORYFIELD, HIDEOPTION,
            HIDEOPTIONGROUP, SHOWOPTIONGROUP )
        .build(); // Actions associated with DataElement Or
                  // TrackedEntityAttribute

    private static final Set<ProgramRuleActionType> NOTIFICATION_LINKED_TYPES = new ImmutableSet.Builder<ProgramRuleActionType>()
        .add( SENDMESSAGE, SCHEDULEMESSAGE ).build(); // Actions associated with
                                                      // NotificationTemplate

    ProgramRuleActionType( String value )
    {
        this.value = value;
        this.whenToRun = getAll();
    }

    ProgramRuleActionType( String value, ProgramRuleActionEvaluationTime... whenToRun )
    {
        this.value = value;
        this.whenToRun = Sets.newHashSet( whenToRun );
    }

    public static ProgramRuleActionType fromValue( String value )
    {
        for ( ProgramRuleActionType type : ProgramRuleActionType.values() )
        {
            if ( type.value.equalsIgnoreCase( value ) )
            {
                return type;
            }
        }

        return null;
    }

    public boolean isImplementable()
    {
        return IMPLEMENTED_ACTIONS.contains( this );
    }

    public static Set<ProgramRuleActionType> getImplementedActions()
    {
        return IMPLEMENTED_ACTIONS;
    }

    public static Set<ProgramRuleActionType> getDataLinkedTypes()
    {
        return DATA_LINKED_TYPES;
    }

    public static Set<ProgramRuleActionType> getNotificationLinkedTypes()
    {
        return NOTIFICATION_LINKED_TYPES;
    }
}
