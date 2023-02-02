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
package org.hisp.dhis.config;

import static org.hibernate.cfg.AvailableSettings.DIALECT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.SessionFactory;
import org.hisp.dhis.cache.DefaultHibernateCacheManager;
import org.hisp.dhis.dbms.DbmsManager;
import org.hisp.dhis.dbms.HibernateDbmsManager;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.hibernate.DefaultHibernateConfigurationProvider;
import org.hisp.dhis.hibernate.HibernateConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Luciano Fiandesio
 * @author Morten Svanæs
 */
@Configuration
@EnableTransactionManagement
public class HibernateConfig
{
    @Autowired
    private ApplicationContext applicationContext;

    @Bean( "hibernateConfigurationProvider" )
    public HibernateConfigurationProvider hibernateConfigurationProvider( DhisConfigurationProvider dhisConfig )
    {
        DefaultHibernateConfigurationProvider hibernateConfigurationProvider = new DefaultHibernateConfigurationProvider();
        hibernateConfigurationProvider.setConfigProvider( dhisConfig );
        return hibernateConfigurationProvider;
    }

    //    @Bean
    //    @DependsOn( "flyway" )
    //    public LocalSessionFactoryBean sessionFactory( DataSource dataSource,
    //        @Qualifier( "hibernateConfigurationProvider" ) HibernateConfigurationProvider hibernateConfigurationProvider )
    //    {
    //        Objects.requireNonNull( dataSource );
    //        Objects.requireNonNull( hibernateConfigurationProvider );
    //
    //        Properties hibernateProperties = hibernateConfigurationProvider.getConfiguration().getProperties();
    //        Objects.requireNonNull( hibernateProperties );
    //
    //        List<Resource> jarResources = hibernateConfigurationProvider.getJarResources();
    //        List<Resource> directoryResources = hibernateConfigurationProvider.getDirectoryResources();
    //
    //        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    //        sessionFactory.setDataSource( dataSource );
    //        sessionFactory.setMappingJarLocations( jarResources.toArray( new Resource[0] ) );
    //        sessionFactory.setMappingDirectoryLocations( directoryResources.toArray( new Resource[0] ) );
    //        sessionFactory.setAnnotatedClasses( DeletedObject.class );
    //        sessionFactory.setHibernateProperties( hibernateProperties );
    //
    //        return sessionFactory;
    //    }
    //
    //    @Bean
    //    public HibernateTransactionManager hibernateTransactionManager( DataSource dataSource,
    //        SessionFactory sessionFactory )
    //    {
    //        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    //        transactionManager.setSessionFactory( sessionFactory );
    //        transactionManager.setDataSource( dataSource );
    //
    //        return transactionManager;
    //    }

    @Bean
    public TransactionTemplate transactionTemplate( HibernateTransactionManager transactionManager )
    {
        return new TransactionTemplate( transactionManager );
    }

    @Bean
    public DefaultHibernateCacheManager cacheManager( SessionFactory sessionFactory )
    {
        DefaultHibernateCacheManager cacheManager = new DefaultHibernateCacheManager();
        cacheManager.setSessionFactory( sessionFactory );
        return cacheManager;
    }

    @Bean
    public DbmsManager dbmsManager( JdbcTemplate jdbcTemplate, SessionFactory sessionFactory,
        DefaultHibernateCacheManager cacheManager )
    {
        HibernateDbmsManager hibernateDbmsManager = new HibernateDbmsManager();
        hibernateDbmsManager.setCacheManager( cacheManager );
        hibernateDbmsManager.setSessionFactory( sessionFactory );
        hibernateDbmsManager.setJdbcTemplate( jdbcTemplate );
        return hibernateDbmsManager;
    }

    @Bean
    @DependsOn( "entityManagerFactoryBean" )
    public PlatformTransactionManager jpaTransactionManager( LocalContainerEntityManagerFactoryBean sessionFactory )
    {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory( sessionFactory.getObject() );
        return txManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate( PlatformTransactionManager transactionManager )
    {
        return new TransactionTemplate( transactionManager );
    }

    @Bean
    @DependsOn( "entityManagerFactoryBean" )
    public DefaultHibernateCacheManager cacheManager( LocalContainerEntityManagerFactoryBean entityManagerFactory )
    {
        DefaultHibernateCacheManager cacheManager = new DefaultHibernateCacheManager();
        cacheManager.setSessionFactory( entityManagerFactory.getObject().unwrap( SessionFactory.class ) );

        return cacheManager;
    }

    @Bean
    @DependsOn( "entityManagerFactoryBean" )
    public EntityManager entityManager( LocalContainerEntityManagerFactoryBean entityManagerFactory )
    {
        return entityManagerFactory.getObject().createEntityManager();
    }

    @Bean
    @DependsOn( "entityManagerFactoryBean" )
    public DbmsManager dbmsManager( JdbcTemplate jdbcTemplate,
        LocalContainerEntityManagerFactoryBean entityManagerFactory,
        DefaultHibernateCacheManager cacheManager )
    {
        HibernateDbmsManager hibernateDbmsManager = new HibernateDbmsManager();
        hibernateDbmsManager.setCacheManager( cacheManager );
        hibernateDbmsManager.setSessionFactory( entityManagerFactory.getObject().unwrap( SessionFactory.class ) );
        hibernateDbmsManager.setJdbcTemplate( jdbcTemplate );
        return hibernateDbmsManager;
    }

    @Bean
    @DependsOn( "entityManagerFactoryBean" )
    public SessionFactory hibernateSessionFactory(
        @Qualifier( "entityManagerFactoryBean" ) LocalContainerEntityManagerFactoryBean entityManagerFactory )
    {
        return entityManagerFactory.getObject().unwrap( SessionFactory.class );
    }

    @Bean( "entityManagerFactoryBean" )
    @DependsOn( { "flyway", "dataSource" } )
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean( DataSource dataSource,
        @Qualifier( "hibernateConfigurationProvider" ) HibernateConfigurationProvider hibernateConfigurationProvider )
        throws IOException
    {
        Objects.requireNonNull( dataSource );
        Objects.requireNonNull( hibernateConfigurationProvider );

        Map<String, Object> properties = new Hashtable<>();
        properties.put( "javax.persistence.schema-generation.database.action", "none" );
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform( hibernateConfigurationProvider.getConfiguration().getProperty( DIALECT ) );
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter( adapter );
        factory.setDataSource( dataSource );
        factory.setPackagesToScan( "org.hisp.dhis" );
        factory.setSharedCacheMode( SharedCacheMode.ENABLE_SELECTIVE );
        factory.setValidationMode( ValidationMode.NONE );
        factory.setJpaPropertyMap( properties );
        factory.setMappingResources( loadResources() );
        factory.afterPropertiesSet();
        return factory;
    }

    private String[] loadResources()
    {
        try
        {

            Resource[] resources = applicationContext.getResources( "classpath*:org/hisp/dhis/**/hibernate/*.hbm.xml" );

            List<String> list = new ArrayList<>();
            for ( Resource resource : resources )
            {
                String url = resource.getURL().toString();
                list.add( url );
            }
            return list.toArray( new String[0] );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    private String[] getMappingResources( List<Resource> jarResources )
        throws IOException
    {
        List<String> files = new ArrayList<>();
        for ( Resource resource : jarResources )
        {
            files.add( resource.getFile().getAbsolutePath() );
        }
        return files.toArray( new String[files.size()] );
    }
}
