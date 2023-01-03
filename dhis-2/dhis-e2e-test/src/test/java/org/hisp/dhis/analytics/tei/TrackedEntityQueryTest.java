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
package org.hisp.dhis.analytics.tei;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hisp.dhis.analytics.ValidationHelper.validateHeader;

import org.hisp.dhis.AnalyticsApiTest;
import org.hisp.dhis.actions.analytics.AnalyticsTeiActions;
import org.hisp.dhis.dto.ApiResponse;
import org.hisp.dhis.helpers.QueryParamsBuilder;
import org.junit.jupiter.api.Test;

/**
 * Groups e2e tests for Tracked Entities "/query" endpoint.
 *
 * @author maikel arabori
 */
public class TrackedEntityQueryTest extends AnalyticsApiTest
{
    private AnalyticsTeiActions analyticsTeiActions = new AnalyticsTeiActions();

    @Test
    public void queryWithProgramAndProgramStageWhenTotalPagesIsFalse()
    {
        // Given
        final QueryParamsBuilder params = new QueryParamsBuilder();
        params.add( "dimension=ou:ImspTQPwCqd" )
            .add( "program=IpHINAT79UW" )
            .add( "asc=IpHINAT79UW.w75KJ2mc4zz" )
            .add( "totalPages=false" )
            .add( "pageSize=100" )
            .add( "page=1" )
            .add( "relativePeriodDate=2022-09-27" );

        // When
        final ApiResponse response = analyticsTeiActions.query().get( "nEenWmSyUEp", JSON, JSON, params );

        // Then
        response.validate()
            .statusCode( 200 )
            .body( "headers", hasSize( equalTo( 14 ) ) )
            .body( "rows", hasSize( equalTo( 0 ) ) )
            .body( "metaData.pager.page", equalTo( 1 ) )
            .body( "metaData.pager.pageSize", equalTo( 100 ) )
            .body( "metaData.pager.isLastPage", is( true ) )
            .body( "metaData.pager", not( hasKey( "total" ) ) )
            .body( "metaData.pager", not( hasKey( "pageCount" ) ) )
            .body( "metaData.items.ImspTQPwCqd.name", equalTo( "Sierra Leone" ) )
            .body( "metaData.dimensions", not( hasKey( "pe" ) ) )
            .body( "metaData.dimensions.ou", hasSize( equalTo( 1 ) ) )
            .body( "metaData.dimensions.ou", hasItem( "ImspTQPwCqd" ) )
            .body( "height", equalTo( 0 ) )
            .body( "width", equalTo( 0 ) )
            .body( "headerWidth", equalTo( 14 ) );

        // Validate headers
        validateHeader( response, 0, "trackedentityinstanceuid", "Tracked Entity Instance", "TEXT", "java.lang.String",
            false, true );
        validateHeader( response, 1, "lastupdated", "Last Updated", "DATETIME", "java.time.LocalDateTime", false, true );
        validateHeader( response, 2, "createdbydisplayname", "Created by (display name)", "TEXT", "java.lang.String",
            false, true );
        validateHeader( response, 3, "lastupdatedbydisplayname", "Last updated by (display name)", "TEXT",
            "java.lang.String", false, true );
        validateHeader( response, 4, "geometry", "Geometry", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 5, "longitude", "Longitude", "NUMBER", "java.lang.Double", false, true );
        validateHeader( response, 6, "latitude", "Latitude", "NUMBER", "java.lang.Double", false, true );
        validateHeader( response, 7, "ouname", "Organisation unit name", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 8, "oucode", "Organisation unit code", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 9, "enrollments", "Enrollments", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 10, "\"w75KJ2mc4zz\"", "", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 11, "\"zDhUuAYrxNC\"", "", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 12, "\"cejWyOfXge6\"", "", "TEXT", "java.lang.String", false, true );
        validateHeader( response, 13, "\"lZGmxYbs97q\"", "", "TEXT", "java.lang.String", false, true );

        // TODO:
        // Validate the first three rows, as samples.
        //        validateRow( response, 0,
        //            List.of( "EbRsJr8LSSO",
        //                "oi3PMIGYJH8",
        //                "2022-07-02 02:00:00.0",
        //                "2022-07-08 02:00:00.0",
        //                "",
        //                "",
        //                "",
        //                "2017-07-23 12:45:49.807",
        //                "",
        //                "",
        //                "",
        //                "Ngelehun CHC",
        //                "OU_559",
        //                "ACTIVE",
        //                "DiszpKrYNg8",
        //                "1231.0" ) );
    }
}
