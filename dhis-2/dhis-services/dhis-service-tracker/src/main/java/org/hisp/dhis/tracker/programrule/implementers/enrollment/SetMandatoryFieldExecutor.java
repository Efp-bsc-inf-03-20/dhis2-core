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

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.tracker.TrackerIdSchemeParams;
import org.hisp.dhis.tracker.bundle.TrackerBundle;
import org.hisp.dhis.tracker.domain.Attribute;
import org.hisp.dhis.tracker.domain.Enrollment;
import org.hisp.dhis.tracker.preheat.TrackerPreheat;
import org.hisp.dhis.tracker.programrule.IssueType;
import org.hisp.dhis.tracker.programrule.ProgramRuleIssue;
import org.hisp.dhis.tracker.validation.ValidationCode;

import com.google.common.collect.Lists;

/**
 * This executor checks if a field is not empty in the {@link TrackerBundle}
 *
 * @Author Enrico Colasante
 */
@RequiredArgsConstructor
public class SetMandatoryFieldExecutor implements RuleActionExecutor
{

    private final MandatoryRuleAction ruleAction;

    @Override
    public Optional<ProgramRuleIssue> validateEnrollment( TrackerBundle bundle, Enrollment enrollment )
    {
        return checkMandatoryEnrollmentAttribute( enrollment, bundle.getPreheat() );
    }

    private Optional<ProgramRuleIssue> checkMandatoryEnrollmentAttribute( Enrollment enrollment,
        TrackerPreheat preheat )
    {
        TrackerIdSchemeParams idSchemes = preheat.getIdSchemes();
        TrackedEntityAttribute ruleAttribute = preheat.getTrackedEntityAttribute( ruleAction.getAttribute() );
        Optional<Attribute> any = enrollment.getAttributes().stream()
            .filter( attribute -> attribute.getAttribute().isEqualTo( ruleAttribute ) )
            .findAny();
        if ( any.isEmpty() || StringUtils.isEmpty( any.get().getValue() ) )
        {
            return Optional.of( new ProgramRuleIssue( ruleAction.getRuleUid(),
                ValidationCode.E1306,
                Lists.newArrayList(
                    idSchemes.toMetadataIdentifier( ruleAttribute ).getIdentifierOrAttributeValue() ),
                IssueType.ERROR ) );
        }
        else
        {
            return Optional.empty();
        }
    }
}
