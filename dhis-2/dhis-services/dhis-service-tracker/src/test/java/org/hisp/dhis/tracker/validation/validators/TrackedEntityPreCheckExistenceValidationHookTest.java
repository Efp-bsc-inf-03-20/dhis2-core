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
package org.hisp.dhis.tracker.validation.validators;

import static org.hisp.dhis.tracker.TrackerType.TRACKED_ENTITY;
import static org.hisp.dhis.tracker.report.TrackerErrorCode.E1002;
import static org.hisp.dhis.tracker.report.TrackerErrorCode.E1063;
import static org.hisp.dhis.tracker.report.TrackerErrorCode.E1114;
import static org.hisp.dhis.tracker.validation.validators.AssertValidationErrorReporter.hasTrackerError;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.tracker.TrackerIdSchemeParams;
import org.hisp.dhis.tracker.TrackerImportStrategy;
import org.hisp.dhis.tracker.bundle.TrackerBundle;
import org.hisp.dhis.tracker.domain.TrackedEntity;
import org.hisp.dhis.tracker.preheat.TrackerPreheat;
import org.hisp.dhis.tracker.validation.ValidationErrorReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Enrico Colasante
 */
@ExtendWith( MockitoExtension.class )
class TrackedEntityPreCheckExistenceValidationHookTest
{
    private final static String SOFT_DELETED_TEI_UID = "SoftDeletedTEIId";

    private final static String TEI_UID = "TEIId";

    private final static String NOT_PRESENT_TEI_UID = "NotPresentTEIId";

    @Mock
    private TrackerBundle bundle;

    @Mock
    private TrackerPreheat preheat;

    private TrackedEntityPreCheckExistenceValidationHook validationHook = new TrackedEntityPreCheckExistenceValidationHook();

    private ValidationErrorReporter reporter;

    @BeforeEach
    void setUp()
    {
        TrackerIdSchemeParams idSchemes = TrackerIdSchemeParams.builder().build();
        reporter = new ValidationErrorReporter( idSchemes );
    }

    @Test
    void verifyTrackedEntityValidationSuccessWhenIsCreateAndTeiIsNotPresent()
    {
        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity( NOT_PRESENT_TEI_UID )
            .build();

        when( bundle.getStrategy( trackedEntity ) ).thenReturn( TrackerImportStrategy.CREATE );

        validationHook.validate( reporter, bundle, trackedEntity );

        assertFalse( reporter.hasErrors() );
    }

    @Test
    void verifyTrackedEntityValidationSuccessWhenTeiIsNotPresent()
    {
        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity( NOT_PRESENT_TEI_UID )
            .build();

        when( bundle.getStrategy( any( TrackedEntity.class ) ) ).thenReturn( TrackerImportStrategy.CREATE_AND_UPDATE );
        validationHook.validate( reporter, bundle, trackedEntity );

        assertFalse( reporter.hasErrors() );
    }

    @Test
    void verifyTrackedEntityValidationSuccessWhenIsUpdate()
    {
        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity( TEI_UID )
            .build();

        when( bundle.getTrackedEntityInstance( TEI_UID ) ).thenReturn( getTei() );
        when( bundle.getStrategy( any( TrackedEntity.class ) ) ).thenReturn( TrackerImportStrategy.CREATE_AND_UPDATE );
        validationHook.validate( reporter, bundle, trackedEntity );

        assertFalse( reporter.hasErrors() );
    }

    @Test
    void verifyTrackedEntityValidationFailsWhenIsSoftDeleted()
    {
        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity( SOFT_DELETED_TEI_UID )
            .build();

        when( bundle.getTrackedEntityInstance( SOFT_DELETED_TEI_UID ) ).thenReturn( getSoftDeletedTei() );
        when( bundle.getStrategy( any( TrackedEntity.class ) ) ).thenReturn( TrackerImportStrategy.CREATE_AND_UPDATE );
        validationHook.validate( reporter, bundle, trackedEntity );

        hasTrackerError( reporter, E1114, TRACKED_ENTITY, trackedEntity.getUid() );
    }

    @Test
    void verifyTrackedEntityValidationFailsWhenIsCreateAndTEIIsAlreadyPresent()
    {
        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity( TEI_UID )
            .build();

        when( bundle.getTrackedEntityInstance( TEI_UID ) ).thenReturn( getTei() );
        when( bundle.getStrategy( trackedEntity ) ).thenReturn( TrackerImportStrategy.CREATE );

        validationHook.validate( reporter, bundle, trackedEntity );

        hasTrackerError( reporter, E1002, TRACKED_ENTITY, trackedEntity.getUid() );
    }

    @Test
    void verifyTrackedEntityValidationFailsWhenIsUpdateAndTEIIsNotPresent()
    {
        TrackedEntity trackedEntity = TrackedEntity.builder()
            .trackedEntity( NOT_PRESENT_TEI_UID )
            .build();

        when( bundle.getStrategy( trackedEntity ) ).thenReturn( TrackerImportStrategy.UPDATE );

        validationHook.validate( reporter, bundle, trackedEntity );

        hasTrackerError( reporter, E1063, TRACKED_ENTITY, trackedEntity.getUid() );
    }

    private TrackedEntityInstance getSoftDeletedTei()
    {
        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setUid( SOFT_DELETED_TEI_UID );
        trackedEntityInstance.setDeleted( true );
        return trackedEntityInstance;
    }

    private TrackedEntityInstance getTei()
    {
        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setUid( TEI_UID );
        trackedEntityInstance.setDeleted( false );
        return trackedEntityInstance;
    }
}