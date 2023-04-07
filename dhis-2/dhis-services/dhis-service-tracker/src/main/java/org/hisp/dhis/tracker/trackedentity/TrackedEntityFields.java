/*
 * Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.tracker.trackedentity;

import java.util.Optional;
import java.util.function.Predicate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.hisp.dhis.tracker.enrollment.EnrollmentFields;

@Getter
@ToString
@EqualsAndHashCode
public class TrackedEntityFields
{

    boolean includeRelationships;

    boolean includeAttributes;

    boolean includeProgramOwners;

    // TODO(ivo) this should not be part of fields but the class representing the request parameters. This fields class should then be part of the class representing the request parameters as fields is one request parameter.
    boolean includeDeleted;

    Optional<EnrollmentFields> enrollmentFields;

    public TrackedEntityFields( Predicate<String> includesFields )
    {
        this.includeAttributes = includesFields.test( "attributes" );
        this.includeRelationships = includesFields.test( "relationships" );
        this.includeProgramOwners = includesFields.test( "programOwners" );
        if ( includesFields.test( "enrollments" ) )
        {
            this.enrollmentFields = Optional
                .of( new EnrollmentFields( s -> includesFields.test( "enrollments." + s ) ) );
        }
        else
        {
            this.enrollmentFields = Optional.empty();
        }
    }

    public boolean isIncludeEnrollments()
    {
        return this.enrollmentFields.isPresent();
    }
}
