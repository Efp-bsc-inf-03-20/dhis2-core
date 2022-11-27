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
package org.hisp.dhis.tracker.validation.exploration.func;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

/**
 * Validator applying all {@link Validator} irrespective of whether one fails or
 * not.
 *
 * @param <T> type to validate
 */
@RequiredArgsConstructor
public class All<T> implements Validator<T>
{

    private final List<Validator<T>> validators;

    public All( Validator<T> v1 )
    {
        this( List.of( v1 ) );
    }

    public All( Validator<T> v1, Validator<T> v2 )
    {
        this( List.of( v1, v2 ) );
    }

    public All( Validator<T> v1, Validator<T> v2, Validator<T> v3 )
    {
        this( List.of( v1, v2, v3 ) );
    }

    public All( Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4 )
    {
        this( List.of( v1, v2, v3, v4 ) );
    }

    public All( Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4, Validator<T> v5 )
    {
        this( List.of( v1, v2, v3, v4, v5 ) );
    }

    public All( Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4, Validator<T> v5, Validator<T> v6 )
    {
        this( List.of( v1, v2, v3, v4, v5, v6 ) );
    }

    public All( Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4, Validator<T> v5, Validator<T> v6,
        Validator<T> v7 )
    {
        this( List.of( v1, v2, v3, v4, v5, v6, v7 ) );
    }

    // TODO if I do not pass the class the compiler does not have enough
    // information to infer the type. So without it
    // the client code is working with an Object. Casting on the client could
    // also work but that's even more awkward.
    public static <T> All<T> all( Class<T> klass, Validator<T> v1 )
    {
        return new All<>( v1 );
    }

    public static <T> All<T> all( Class<T> klass, Validator<T> v1, Validator<T> v2 )
    {
        return new All<>( v1, v2 );
    }

    public static <T> All<T> all( Class<T> klass, Validator<T> v1, Validator<T> v2, Validator<T> v3 )
    {
        return new All<>( v1, v2, v3 );
    }

    public static <T> All<T> all( Class<T> klass, Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4 )
    {
        return new All<>( v1, v2, v3, v4 );
    }

    public static <T> All<T> all( Class<T> klass, Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4,
        Validator<T> v5 )
    {
        return new All<>( v1, v2, v3, v4, v5 );
    }

    public static <T> All<T> all( Class<T> klass, Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4,
        Validator<T> v5, Validator<T> v6 )
    {
        return new All<>( v1, v2, v3, v4, v5, v6 );
    }

    public static <T> All<T> all( Class<T> klass, Validator<T> v1, Validator<T> v2, Validator<T> v3, Validator<T> v4,
        Validator<T> v5, Validator<T> v6, Validator<T> v7 )
    {
        return new All<>( v1, v2, v3, v4, v5, v6, v7 );
    }

    public static <T> All<T> all( Class<T> klass, List<Validator<T>> validators )
    {
        return new All<>( validators );
    }

    @Override
    public Optional<Error> apply( T input )
    {
        Optional<Error> result = Optional.empty();

        for ( Validator<T> validator : validators )
        {
            Optional<Error> error = validator.apply( input );
            if ( error.isPresent() )
            {
                if ( result.isPresent() )
                {
                    result.get().append( error.get() );
                }
                else
                {
                    result = error;
                }
            }
        }

        return result;
    }
}
