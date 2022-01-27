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
package org.hisp.dhis.dxf2.datavalueset;

import java.util.Set;
import java.util.concurrent.Callable;

import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.category.CategoryOptionCombo;
import org.hisp.dhis.common.IdScheme;
import org.hisp.dhis.dxf2.util.InputUtils;

/**
 * @author viet@dhis2.org
 */
public class FindCategoryOptionComboCallable
    implements Callable
{
    private final InputUtils inputUtils;

    private IdScheme idScheme = IdScheme.UID;

    private CategoryCombo categoryCombo;

    private Set<String> categoryOptions;

    public FindCategoryOptionComboCallable( InputUtils inputUtils )
    {
        this.inputUtils = inputUtils;
    }

    @Override
    public CategoryOptionCombo call()
    {
        return inputUtils.getAttributeOptionCombo( categoryCombo, categoryOptions, idScheme );
    }

    public FindCategoryOptionComboCallable setCategoryCombo( CategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
        return this;
    }

    public FindCategoryOptionComboCallable setCategoryOptions( Set<String> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
        return this;
    }

    public IdScheme getIdScheme()
    {
        return idScheme;
    }
}
