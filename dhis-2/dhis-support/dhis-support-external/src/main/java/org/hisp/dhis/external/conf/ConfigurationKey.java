package org.hisp.dhis.external.conf;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

/**
 * @author Lars Helge Overland
 */
public enum ConfigurationKey
{
    SYSTEM_READ_ONLY_MODE( "system.read_only_mode", "off", false ),
    SYSTEM_SESSION_TIMEOUT( "system.session.timeout", "3600", false ),
    SYSTEM_INTERNAL_SERVICE_API( "system.internal_service_api", "off", false ),
    SYSTEM_MONITORING_URL( "system.monitoring.url" ),
    SYSTEM_MONITORING_USERNAME( "system.monitoring.username" ),
    SYSTEM_MONITORING_PASSWORD( "system.monitoring.password" ),
    SYSTEM_SQL_VIEW_TABLE_PROTECTION( "system.sql_view_table_protection", "on", false ),
    NODE_ID( "node.id", "", false ),
    ENCRYPTION_PASSWORD( "encryption.password", "", true ),
    CONNECTION_DIALECT( "connection.dialect", "", false ),
    CONNECTION_DRIVER_CLASS( "connection.driver_class", "", false ),
    CONNECTION_URL( "connection.url", "", false ),
    CONNECTION_USERNAME( "connection.username", "", false ),
    CONNECTION_PASSWORD( "connection.password", "", true ),
    CONNECTION_SCHEMA( "connection.schema", "", false ),
    CONNECTION_POOL_MAX_SIZE( "connection.pool.max_size", "80", false ),
    LDAP_URL( "ldap.url", "ldaps://0:1", false ),
    LDAP_MANAGER_DN( "ldap.manager.dn", "", false ),
    LDAP_MANAGER_PASSWORD( "ldap.manager.password", "", true ),
    LDAP_SEARCH_BASE( "ldap.search.base", "", false ),
    LDAP_SEARCH_FILTER( "ldap.search.filter", "(cn={0})", false ),
    FILESTORE_PROVIDER( "filestore.provider", "filesystem", false ),
    FILESTORE_CONTAINER( "filestore.container", "files", false ),
    FILESTORE_LOCATION( "filestore.location", "", false ),
    FILESTORE_IDENTITY( "filestore.identity", "", false ),
    FILESTORE_SECRET( "filestore.secret", "", true ),
    GOOGLE_SERVICE_ACCOUNT_CLIENT_ID( "google.service.account.client.id", "", false ),
    META_DATA_SYNC_RETRY( "metadata.sync.retry", "3", false ),
    META_DATA_SYNC_RETRY_TIME_FREQUENCY_MILLISEC( "metadata.sync.retry.time.frequency.millisec", "30000", false ),
    CLUSTER_HOSTNAME( "cluster.hostname", "", false ),
    CLUSTER_MEMBERS( "cluster.members", "", false ),
    CLUSTER_CACHE_PORT( "cluster.cache.port", "4001", false ),
    CLUSTER_CACHE_REMOTE_OBJECT_PORT( "cluster.cache.remote.object.port", "0", false ),
    CACHE_PROVIDER( "cache.provider", "ehcache", false ),
    CACHE_SERVERS( "cache.servers", "localhost:11211", false ),
    CACHE_TIME( "cache.time", "600", false ),
    METADATA_AUDIT_PERSIST( "metadata.audit.persist", "off", false ),
    METADATA_AUDIT_LOG( "metadata.audit.log", "off", false ),
    RABBITMQ_HOST( "rabbitmq.host" ),
    RABBITMQ_ADDRESSES( "rabbitmq.addresses" ),
    RABBITMQ_VIRTUAL_HOST( "rabbitmq.virtual-host", "/", false ),
    RABBITMQ_PORT( "rabbitmq.port", "5672", false ),
    RABBITMQ_EXCHANGE( "rabbitmq.exchange", "dhis2", false ),
    RABBITMQ_USERNAME( "rabbitmq.username", "guest", false ),
    RABBITMQ_PASSWORD( "rabbitmq.password", "guest", true ),
    KAFKA_BOOTSTRAP_SERVERS( "kafka.bootstrap-servers", null, false ),
    KAFKA_CLIENT_ID( "kafka.client-id", "dhis2", false ),
    KAFKA_RETRIES( "kafka.retries", "10", false ),
    KAFKA_MAX_POLL_RECORDS( "kafka.max-poll-records", "1000", false ),
    REDIS_HOST( "redis.host", "localhost", false ),
    REDIS_PORT( "redis.port", "6379", false ),
    REDIS_PASSWORD( "redis.password", "", true ),
    REDIS_ENABLED( "redis.enabled", "false", false ),
    REDIS_USE_SSL( "redis.use.ssl", "false", false ),
    FLYWAY_OUT_OF_ORDER_MIGRATION( "flyway.migrate_out_of_order", "false", false ),
    PROGRAM_TEMPORARY_OWNERSHIP_TIMEOUT( "tracker.temporary.ownership.timeout", "3", false ),
    LEADER_TIME_TO_LIVE( "leader.time.to.live.minutes", "2", false ),
    RABBITMQ_CONNECTION_TIMEOUT( "rabbitmq.connection-timeout", "60000", false ),
    ANALYTICS_CACHE_EXPIRATION( "analytics.cache.expiration", "0" ),
    LOGGING_LEVEL( "logging.level", "INFO" ),
    LOGGING_FORMAT( "logging.format", "TEXT" ),
    LOGGING_ADAPTER_CONSOLE( "logging.console", "true" ),
    LOGGING_ADAPTER_CONSOLE_LEVEL( "logging.console.level", "INFO" ),
    LOGGING_ADAPTER_CONSOLE_FORMAT( "logging.console.format", "TEXT" ),
    LOGGING_ADAPTER_KAFKA( "logging.kafka", "false" ),
    LOGGING_ADAPTER_KAFKA_LEVEL( "logging.kafka.level", "INFO" ),
    LOGGING_ADAPTER_KAFKA_FORMAT( "logging.kafka.format", "JSON" ),
    LOGGING_ADAPTER_KAFKA_TOPIC( "logging.kafka.topic", "dhis2-log" ),
    LOGGING_FILE_MAX_SIZE( "logging.file.max_size", "100MB" ),
    LOGGING_FILE_MAX_ARCHIVES( "logging.file.max_archives", "0" ),
    SERVER_HTTPS( "server.https", "off" ),
    CHANGELOG_AGGREGATE( "changelog.aggregate", "on" ),
    CHANGELOG_TRACKER( "changelog.tracker", "on" );

    private final String key;

    private final String defaultValue;

    private final boolean confidential;

    ConfigurationKey( String key )
    {
        this.key = key;
        this.defaultValue = null;
        this.confidential = false;
    }

    ConfigurationKey( String key, String defaultValue )
    {
        this.key = key;
        this.defaultValue = defaultValue;
        this.confidential = false;
    }

    ConfigurationKey( String key, String defaultValue, boolean confidential )
    {
        this.key = key;
        this.defaultValue = defaultValue;
        this.confidential = confidential;
    }

    public String getKey()
    {
        return key;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public boolean isConfidential()
    {
        return confidential;
    }
}
