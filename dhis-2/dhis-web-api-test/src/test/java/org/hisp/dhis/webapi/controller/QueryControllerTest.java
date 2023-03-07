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
package org.hisp.dhis.webapi.controller;

import static org.hisp.dhis.web.WebClientUtils.assertStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hisp.dhis.jsontree.JsonObject;
import org.hisp.dhis.web.HttpStatus;
import org.hisp.dhis.webapi.DhisControllerConvenienceTest;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link org.hisp.dhis.webapi.openapi.QueryController} with Mock MVC
 * tests.
 *
 * @author Austin McGee
 */
class QueryControllerTest extends DhisControllerConvenienceTest
{
    private static final String API_QUERY_ALIAS = "/query/alias";

    private static final String API_QUERY_ALIAS_REDIRECT = "/query/alias/redirect";

    private static final String testTargetUrl = "/api/me?fields=id,username,surname,firstName";

    private static final String testTargetAliasId = "671575eeea3d36a777efa6dcb48076083ff5cbbd";

    private static final String testTargetAliasPath = API_QUERY_ALIAS + "/" + testTargetAliasId;

    private static final String testTargetAliasFullPath = "/api" + testTargetAliasPath;

    private static final String testTargetAliasHref = "http://localhost" + testTargetAliasFullPath;

    @Test
    void testGetUninitializedAlias()
    {
        assertStatus( HttpStatus.NOT_FOUND, GET( testTargetAliasPath ) );
    }

    @Test
    void testCreateAlias()
    {
        JsonObject doc = POST( API_QUERY_ALIAS, "{ \"target\": \"" + testTargetUrl + "\" }" ).content();

        assertTrue( doc.isObject() );

        assertEquals( testTargetAliasId, doc.getString( "id" ).string() );
        assertEquals( testTargetAliasFullPath, doc.getString( "path" ).string() );
        assertEquals( testTargetAliasHref, doc.getString( "href" ).string() );
        assertEquals( testTargetUrl, doc.getString( "target" ).string() );

        /*
         * Testing the actual query implementation requires a valid cache
         * provider which is not available in integration test contexts
         */

        // JsonObject targetResponse = GET( testTargetUrl.substring( "/api".length() ) ).content();
        // JsonObject aliasResponse = GET( testTargetAliasPath ).content();
        // assertEquals( targetResponse.toString(), aliasResponse.toString() );
    }

    private void testInvalidPayloads( String apiEndpoint )
    {
        assertStatus( HttpStatus.BAD_REQUEST, POST( apiEndpoint, "/api/me" ) );
        assertStatus( HttpStatus.BAD_REQUEST, POST( apiEndpoint, "\"/api/me\"" ) );
        assertStatus( HttpStatus.BAD_REQUEST, POST( apiEndpoint, "{ \"destination\": \"/api/me\"" ) );
        assertStatus( HttpStatus.BAD_REQUEST, POST( apiEndpoint, "\"/api/me\"" ) );
        assertStatus( HttpStatus.BAD_REQUEST, POST( apiEndpoint, "{ \"target\": \"/me\" }" ) );
        assertStatus( HttpStatus.BAD_REQUEST, POST( apiEndpoint, "{ \"target\": \"api/me\" }" ) );
        assertStatus( HttpStatus.BAD_REQUEST,
            POST( apiEndpoint, "{ \"target\": \"http://localhost:8080/api/me\" }" ) );
        assertStatus( HttpStatus.BAD_REQUEST,
            POST( apiEndpoint, "{ \"target\": \"" + "/api/".repeat( 100000 ) + "\" }" ) );
    }

    @Test
    void testCreateAliasInvalidPayload()
    {
        testInvalidPayloads( API_QUERY_ALIAS );
    }

    @Test
    void tesAliasRedirect()
    {
        HttpResponse response = POST( API_QUERY_ALIAS_REDIRECT, "{ \"target\": \"" + testTargetUrl + "\" }" );
        assertStatus( HttpStatus.SEE_OTHER, response );
        assertEquals( testTargetAliasHref, response.header( "Location" ) );
    }

    @Test
    void testAliasRedirectInvalidPayload()
    {
        testInvalidPayloads( API_QUERY_ALIAS_REDIRECT );
    }
}
