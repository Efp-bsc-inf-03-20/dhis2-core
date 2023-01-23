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
package org.hisp.dhis.dxf2.events.importer.shared.preprocess;

import static org.hisp.dhis.dxf2.events.importer.shared.DataValueFilteringTestSupport.DATA_ELEMENT_1;
import static org.hisp.dhis.dxf2.events.importer.shared.DataValueFilteringTestSupport.DATA_ELEMENT_2;
import static org.hisp.dhis.dxf2.events.importer.shared.DataValueFilteringTestSupport.PROGRAMSTAGE;
import static org.hisp.dhis.dxf2.events.importer.shared.DataValueFilteringTestSupport.getProgramMap;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dxf2.common.ImportOptions;
import org.hisp.dhis.dxf2.events.event.DataValue;
import org.hisp.dhis.dxf2.events.event.Event;
import org.hisp.dhis.dxf2.events.importer.context.EventDataValueAggregator;
import org.hisp.dhis.dxf2.events.importer.context.WorkContext;
import org.hisp.dhis.eventdatavalue.EventDataValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

/**
 * @author Giuseppe Nespolino <g.nespolino@gmail.com>
 */
class FilteringOutUndeclaredDataElementsProcessorTest
{

    private FilteringOutUndeclaredDataElementsProcessor preProcessor;

    @BeforeEach
    void setUp()
    {
        preProcessor = new FilteringOutUndeclaredDataElementsProcessor();
    }

    @Test
    void testNotLinkedDataElementsAreRemovedFromEvent()
    {
        Event event = new Event();
        event.setProgramStage( PROGRAMSTAGE );
        HashSet<DataValue> dataValues = Sets.newHashSet( new DataValue( DATA_ELEMENT_1, "whatever" ),
            new DataValue( DATA_ELEMENT_2, "another value" ) );
        event.setDataValues( dataValues );
        WorkContext ctx = WorkContext.builder().importOptions( ImportOptions.getDefaultImportOptions() )
            .programsMap( getProgramMap() )
            .eventDataValueMap( new EventDataValueAggregator().aggregateDataValues( List.of( event ),
                Collections.emptyMap(), ImportOptions.getDefaultImportOptions() ) )
            .build();
        preProcessor.process( event, ctx );
        Set<String> allowedDataValues = ctx
            .getProgramStage( ctx.getImportOptions().getIdSchemes().getProgramStageIdScheme(), PROGRAMSTAGE )
            .getDataElements().stream().map( BaseIdentifiableObject::getUid ).collect( Collectors.toSet() );
        Set<String> filteredEventDataValues = ctx.getEventDataValueMap().values().stream().flatMap( Collection::stream )
            .map( EventDataValue::getDataElement ).collect( Collectors.toSet() );
        assertTrue( allowedDataValues.containsAll( filteredEventDataValues ) );
    }
}
