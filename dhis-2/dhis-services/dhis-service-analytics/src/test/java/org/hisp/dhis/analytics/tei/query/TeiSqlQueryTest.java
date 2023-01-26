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

import static org.hisp.dhis.utils.Assertions.assertContains;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.analytics.common.AnalyticsPagingParams;
import org.hisp.dhis.analytics.common.AnalyticsSortingParams;
import org.hisp.dhis.analytics.common.CommonParams;
import org.hisp.dhis.analytics.common.dimension.DimensionIdentifier;
import org.hisp.dhis.analytics.common.dimension.DimensionParam;
import org.hisp.dhis.analytics.common.dimension.DimensionParamType;
import org.hisp.dhis.analytics.common.dimension.ElementWithOffset;
import org.hisp.dhis.analytics.tei.TeiQueryParams;
import org.hisp.dhis.analytics.tei.query.context.QueryContext;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.webapi.controller.event.mapper.SortDirection;
import org.junit.jupiter.api.Test;

/**
 * TeiFullQuery unit tests.
 *
 * @author Dusan Bernat
 */
class TeiSqlQueryTest extends DhisConvenienceTest
{
    @Test
    void testSqlQueryRenderingWithOrgUnitNameObject()
    {
        // given
        TeiQueryParams teiQueryParams = TeiQueryParams.builder()
            .trackedEntityType( createTrackedEntityType( 'A' ) )
            .commonParams( stubSortingCommonParams( null, StringUtils.EMPTY, "ouname" ) )
            .build();

        TeiSqlQuery query = TeiSqlQuery.builder()
            .teiQueryParams( teiQueryParams )
            .queryContext( QueryContext.of( teiQueryParams ) )
            .build();

        // when
        String sql = query.render();

        // then
        assertTrue( sql.contains( "ouname" ) );
        assertContains( "order by t_1.\"ouname\" ASC", sql );
    }

    @Test
    void testSqlQueryRenderingWithCommonDimensionalObject()
    {
        // when
        DimensionalObject dimensionalObject = new BaseDimensionalObject( "abc" );

        TeiQueryParams teiQueryParams = TeiQueryParams.builder()
            .trackedEntityType( createTrackedEntityType( 'A' ) )
            .commonParams( stubSortingCommonParams( createProgram( 'A' ), "0", dimensionalObject ) )
            .build();

        TeiSqlQuery query = TeiSqlQuery.builder()
            .teiQueryParams( teiQueryParams )
            .queryContext( QueryContext.of( teiQueryParams ) )
            .build();

        // when
        String sql = query.render();

        // then
        assertTrue( sql.contains( "(\"abc_0\".\"eventdatavalues\" -> 'abc' ->> 'value')::TEXT ASC" ) );
    }

    private CommonParams stubSortingCommonParams( Program program, String offset, Object dimensionalObject )
    {
        ElementWithOffset<Program> prg = program == null
            ? ElementWithOffset.emptyElementWithOffset()
            : ElementWithOffset.of( program, offset );

        ElementWithOffset<ProgramStage> programStage = program == null
            ? ElementWithOffset.emptyElementWithOffset()
            : ElementWithOffset.of( createProgramStage( 'S', program ), offset );

        DimensionIdentifier<DimensionParam> dimensionIdentifier = DimensionIdentifier.of( prg,
            programStage,
            DimensionParam.ofObject( dimensionalObject, DimensionParamType.SORTING, List.of( StringUtils.EMPTY ) ) );

        AnalyticsSortingParams analyticsSortingParams = AnalyticsSortingParams.builder()
            .sortDirection( SortDirection.ASC )
            .orderBy( dimensionIdentifier )
            .build();

        AnalyticsPagingParams analyticsPagingParams = AnalyticsPagingParams.builder()
            .pageSize( 10 )
            .page( 1 )
            .build();

        return CommonParams.builder()
            .orderParams( List.of( analyticsSortingParams ) )
            .pagingParams( analyticsPagingParams )
            .build();
    }
}
