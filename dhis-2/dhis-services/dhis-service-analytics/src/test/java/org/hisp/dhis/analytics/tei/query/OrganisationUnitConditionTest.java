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
package org.hisp.dhis.analytics.tei.query;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hisp.dhis.analytics.common.dimension.DimensionParamType.DIMENSIONS;
import static org.hisp.dhis.analytics.common.dimension.ElementWithOffset.emptyElementWithOffset;
import static org.hisp.dhis.common.DimensionType.ORGANISATION_UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.hisp.dhis.analytics.common.dimension.DimensionIdentifier;
import org.hisp.dhis.analytics.common.dimension.DimensionParam;
import org.hisp.dhis.analytics.common.dimension.ElementWithOffset;
import org.hisp.dhis.analytics.tei.TeiQueryParams;
import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.trackedentity.TrackedEntityType;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link OrganisationUnitCondition}.
 */
class OrganisationUnitConditionTest
{
    @Test
    void testTeiOuMultipleOusProduceCorrectSql()
    {
        // Given
        List<String> ous = List.of( "ou1", "ou2" );
        DimensionIdentifier<DimensionParam> dimensionIdentifier = stubDimensionIdentifier(
            ous, null, null );

        QueryContext queryContext = QueryContext.of( null );

        OrganisationUnitCondition organisationUnitCondition = OrganisationUnitCondition.of( dimensionIdentifier,
            queryContext );

        // When
        String render = organisationUnitCondition.render();

        // Then
        assertEquals( "\"ou\" in (:1)", render );
        assertEquals( ous, queryContext.getParametersPlaceHolder().get( "1" ) );
    }

    @Test
    void testTeiOuSingleOusProduceCorrectSql()
    {
        // Given
        List<String> ous = List.of( "ou1" );

        DimensionIdentifier<DimensionParam> dimensionIdentifier = stubDimensionIdentifier(
            ous, null, null );

        QueryContext queryContext = QueryContext.of( null );

        OrganisationUnitCondition organisationUnitCondition = OrganisationUnitCondition.of( dimensionIdentifier,
            queryContext );

        // When
        String render = organisationUnitCondition.render();

        // Then
        assertEquals( "\"ou\" = :1", render );
        assertEquals( ous.get( 0 ), queryContext.getParametersPlaceHolder().get( "1" ) );
    }

    @Test
    void testEventOuSingleOusProduceCorrectSql()
    {
        // Given
        List<String> ous = List.of( "ou1" );

        DimensionIdentifier<DimensionParam> dimensionIdentifier = stubDimensionIdentifier(
            ous, "Z8z5uu61HAb", "tO8L1aBitDm" );

        TeiQueryParams teiQueryParams = TeiQueryParams.builder()
            .trackedEntityType( stubTrackedEntityType( "T2d3uj69RAb" ) ).build();

        QueryContext queryContext = QueryContext.of( teiQueryParams );

        OrganisationUnitCondition organisationUnitCondition = OrganisationUnitCondition.of( dimensionIdentifier,
            queryContext );

        // When
        String statement = organisationUnitCondition.render();

        // Then
        assertEquals(
            "exists (select 1 from (select * from (select *, row_number() over " +
                "(partition by trackedentityinstanceuid order by enrollmentdate desc) as rn from " +
                "analytics_tei_enrollments_t2d3uj69rab where programuid = :1 and " +
                "t_1.trackedentityinstanceuid = trackedentityinstanceuid) innermost_enr where innermost_enr.rn = 1) " +
                "enr left join (select * from (select *, row_number() over (partition by programinstanceuid order " +
                "by executiondate desc) as rn from analytics_tei_events_t2d3uj69rab where programstageuid = :2 and " +
                "t_1.trackedentityinstanceuid = trackedentityinstanceuid) innermost_enr where innermost_enr.rn = 1) " +
                "evt on enr.programinstanceuid = evt.programinstanceuid where evt.\"ou\" = :3)",
            statement );
        assertEquals( dimensionIdentifier.getProgram().getElement().getUid(),
            queryContext.getParametersPlaceHolder().get( "1" ) );
        assertEquals( dimensionIdentifier.getProgramStage().getElement().getUid(),
            queryContext.getParametersPlaceHolder().get( "2" ) );
        assertEquals( ous.get( 0 ), queryContext.getParametersPlaceHolder().get( "3" ) );
    }

    @Test
    void testEventOuMultipleOusProduceCorrectSql()
    {
        // Given
        List<String> ous = List.of( "ou1", "ou2" );

        DimensionIdentifier<DimensionParam> dimensionIdentifier = stubDimensionIdentifier(
            ous, "Z8z5uu61HAb", "tO8L1aBitDm" );

        TeiQueryParams teiQueryParams = TeiQueryParams.builder()
            .trackedEntityType( stubTrackedEntityType( "T2d3uj69RAb" ) ).build();

        QueryContext queryContext = QueryContext.of( teiQueryParams );

        OrganisationUnitCondition organisationUnitCondition = OrganisationUnitCondition.of( dimensionIdentifier,
            queryContext );

        // When
        String statement = organisationUnitCondition.render();

        // Then
        assertEquals(
            "exists (select 1 from (select * from (select *, row_number() over " +
                "(partition by trackedentityinstanceuid order by enrollmentdate desc) as rn from " +
                "analytics_tei_enrollments_t2d3uj69rab where programuid = :1 and " +
                "t_1.trackedentityinstanceuid = trackedentityinstanceuid) innermost_enr where innermost_enr.rn = 1) " +
                "enr left join (select * from (select *, row_number() over (partition by programinstanceuid order " +
                "by executiondate desc) as rn from analytics_tei_events_t2d3uj69rab where programstageuid = :2 and " +
                "t_1.trackedentityinstanceuid = trackedentityinstanceuid) innermost_enr where innermost_enr.rn = 1) " +
                "evt on enr.programinstanceuid = evt.programinstanceuid where evt.\"ou\" in (:3))",
            statement );
        assertEquals( dimensionIdentifier.getProgram().getElement().getUid(),
            queryContext.getParametersPlaceHolder().get( "1" ) );
        assertEquals( dimensionIdentifier.getProgramStage().getElement().getUid(),
            queryContext.getParametersPlaceHolder().get( "2" ) );
        assertEquals( ous, queryContext.getParametersPlaceHolder().get( "3" ) );
    }

    @Test
    void testEnrollmentOuSingleOusProduceCorrectSql()
    {
        // Given
        List<String> ous = List.of( "ou1" );

        DimensionIdentifier<DimensionParam> dimensionIdentifier = stubDimensionIdentifier(
            ous, "Z8z5uu61HAb", null );

        TeiQueryParams teiQueryParams = TeiQueryParams.builder()
            .trackedEntityType( stubTrackedEntityType( "T2d3uj69RAb" ) ).build();

        QueryContext queryContext = QueryContext.of( teiQueryParams );

        OrganisationUnitCondition organisationUnitCondition = OrganisationUnitCondition.of( dimensionIdentifier,
            queryContext );

        // When
        String statement = organisationUnitCondition.render();

        // Then
        assertEquals(
            "exists (select 1 from (select * from (select *, row_number() over " +
                "(partition by trackedentityinstanceuid order by enrollmentdate desc) as rn from " +
                "analytics_tei_enrollments_t2d3uj69rab where programuid = :1 and " +
                "t_1.trackedentityinstanceuid = trackedentityinstanceuid) innermost_enr where innermost_enr.rn = 1) " +
                "enr where enr.\"ou\" = :2)",
            statement );
        assertEquals( dimensionIdentifier.getProgram().getElement().getUid(),
            queryContext.getParametersPlaceHolder().get( "1" ) );
        assertEquals( ous.get( 0 ), queryContext.getParametersPlaceHolder().get( "2" ) );
    }

    @Test
    void testEnrollmentOuMultipleOusProduceCorrectSql()
    {
        // Given
        List<String> ous = List.of( "ou1", "ou2" );

        DimensionIdentifier<DimensionParam> dimensionIdentifier = stubDimensionIdentifier(
            ous, "Z8z5uu61HAb", null );

        TeiQueryParams teiQueryParams = TeiQueryParams.builder()
            .trackedEntityType( stubTrackedEntityType( "T2d3uj69RAb" ) ).build();

        QueryContext queryContext = QueryContext.of( teiQueryParams );

        OrganisationUnitCondition organisationUnitCondition = OrganisationUnitCondition.of( dimensionIdentifier,
            queryContext );

        // When
        String statement = organisationUnitCondition.render();

        // Then
        assertEquals(
            "exists (select 1 from (select * from (select *, row_number() over " +
                "(partition by trackedentityinstanceuid order by enrollmentdate desc) as rn from " +
                "analytics_tei_enrollments_t2d3uj69rab where programuid = :1 and " +
                "t_1.trackedentityinstanceuid = trackedentityinstanceuid) innermost_enr where innermost_enr.rn = 1) " +
                "enr where enr.\"ou\" in (:2))",
            statement );
        assertEquals( dimensionIdentifier.getProgram().getElement().getUid(),
            queryContext.getParametersPlaceHolder().get( "1" ) );
        assertEquals( ous, queryContext.getParametersPlaceHolder().get( "2" ) );
    }

    private TrackedEntityType stubTrackedEntityType( String uid )
    {
        TrackedEntityType tet = new TrackedEntityType();
        tet.setUid( uid );

        return tet;
    }

    private DimensionIdentifier<DimensionParam> stubDimensionIdentifier( List<String> ous,
        String programUid, String programStageUid )
    {
        DimensionParam dimensionParam = DimensionParam.ofObject(
            new BaseDimensionalObject( "ou", ORGANISATION_UNIT,
                ous.stream()
                    .map( BaseDimensionalItemObject::new )
                    .collect( Collectors.toList() ) ),
            DIMENSIONS,
            ous );

        ElementWithOffset program = emptyElementWithOffset();
        ElementWithOffset programStage = emptyElementWithOffset();

        if ( isNotBlank( programUid ) )
        {
            Program p = new Program();
            p.setUid( programUid );
            program = ElementWithOffset.of( p, null );
        }

        if ( isNotBlank( programStageUid ) )
        {
            ProgramStage ps = new ProgramStage();
            ps.setUid( programStageUid );
            programStage = ElementWithOffset.of( ps, null );
        }

        return DimensionIdentifier.of( program, programStage, dimensionParam );
    }
}
