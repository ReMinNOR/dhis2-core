package org.hisp.dhis.sms.incoming;

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

import java.util.Date;
import java.util.List;

import org.hisp.dhis.user.User;

/**
 * Service providing support for retrieving incoming SMSes.
 */
public interface IncomingSmsService
{
    String ID = IncomingSmsService.class.getName();

    void update( IncomingSms sms );

    IncomingSms get( long id );

    IncomingSms get( String uid );

    List<IncomingSms> getAll();

    List<IncomingSms> getAll( Integer min, Integer max, boolean hasPagination );

    void delete( long id );

    void delete( String uid );

    long save( IncomingSms sms );

    long save( String message, String originator, String gateway, Date receivedTime, User user );

    List<IncomingSms> getSmsByStatus( SmsMessageStatus status, String originator );

    List<IncomingSms> getSmsByStatus( SmsMessageStatus status, String keyword, Integer min, Integer max,
        boolean hasPagination );

    List<IncomingSms> getAllUnparsedMessages();
}
