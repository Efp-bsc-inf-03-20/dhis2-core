/*
 * Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.tracker.programrule.implementers.enrollment;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.tracker.bundle.TrackerBundle;
import org.hisp.dhis.tracker.domain.Enrollment;
import org.hisp.dhis.tracker.domain.EnrollmentStatus;
import org.hisp.dhis.tracker.programrule.IssueType;
import org.hisp.dhis.tracker.programrule.ProgramRuleIssue;
import org.hisp.dhis.tracker.validation.ValidationCode;

import com.google.common.collect.Lists;

/**
 * This implementer check if there are errors or warnings the
 * {@link TrackerBundle}
 *
 * @Author Enrico Colasante
 */
public interface ErrorWarningExecutor extends RuleActionExecutor
{
    boolean isOnComplete();

    IssueType getIssueType();

    default Optional<ProgramRuleIssue> validateEnrollment( ErrorWarningRuleAction enrollmentActionRules,
        Enrollment enrollment )
    {
        if ( needsToRun( enrollment ) )
        {
            return Optional.of( parseErrors( enrollmentActionRules ) );
        }
        return Optional.empty();
    }

    private ProgramRuleIssue parseErrors( ErrorWarningRuleAction ruleAction )
    {

        String field = ruleAction.getField();
        String content = ruleAction.getContent();
        String data = ruleAction.getData();

        StringBuilder stringBuilder = new StringBuilder( content );
        if ( !StringUtils.isEmpty( data ) )
        {
            stringBuilder.append( " " ).append( data );
        }
        if ( !StringUtils.isEmpty( field ) )
        {
            stringBuilder.append( " (" ).append( field ).append( ")" );
        }

        return new ProgramRuleIssue( ruleAction.getRuleUid(), ValidationCode.E1300,
            Lists.newArrayList( stringBuilder.toString() ), getIssueType() );
    }

    private boolean needsToRun( Enrollment enrollment )
    {
        if ( isOnComplete() )
        {
            return Objects.equals( EnrollmentStatus.COMPLETED, enrollment.getStatus() );
        }
        else
        {
            return true;
        }
    }
}
