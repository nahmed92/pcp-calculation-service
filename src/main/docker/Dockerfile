#ARG DELTA_DOCKER_REGISTRY=dockerdev.deltads.ent:7070
#ARG BASE_IMAGE_NAME=delta
#ARG BASE_IMAGE_TAG=jdk11-jre-alpine-appd-contrast

FROM openjdk:11-jre

MAINTAINER Delta Dental Group Member Online - DCM
VOLUME /tmp
ADD pcp-calculation-service-1.0.0.jar pcp-calculation-service-1.0.0.jar
ADD contrast.jar contrast.jar
RUN sh -c 'touch /pcp-calculation-service-3.0.0.jar'
RUN sh -c 'touch /contrast.jar'
ENV JAVA_OPTS=""
ENTRYPOINT java $JAVA_OPTS \
     -javaagent:$APPD_ROOT/javaagent.jar \
     -javaagent:/contrast.jar \
     -Dcontrast.dir=/opt/docker/logs/contrast \
     -Dcontrast.override.appname=${SERVICE_NAME} \
     -Dcontrast.enabled=${CONTRAST_ENABLED} \
     -Dcontrast.env=${CONTRAST_ENV} \
     -Dcontrast.server.name=${CONTRAST_SERVER_NAME} \
     -Dappdynamics.controller.hostName=${APPD_CONTROLLER_HOSTNAME} \
     -Dappdynamics.controller.port=${APPD_PORT} \
     -Dappdynamics.agent.runtime.dir=${APPD_LOG_LOCATION} \
     -Dappdynamics.agent.applicationName=${APPD_APPLICATION_NAME} \
     -Dappdynamics.agent.nodeName=${APPD_NODE_NAME} \
     -Dappdynamics.agent.tierName=${APPD_TIER_NAME} \
     -Dappdynamics.agent.accountName=${APPD_ACCOUNT_NAME} \
     -Dappdynamics.agent.accountAccessKey=${APPD_ACCESS_KEY} \
     -Dappdynamics.agent.runtime.dir=${APPD_LOG_LOCATION} \
     -Dappdynamics.agent.uniqueHostId=${HOST_NAME} \
     -Dappdynamics.controller.ssl.enabled=${APPD_SSL_ENABLED} \
     -DplatformLogLevel=${PLATFORM_LOG_LEVEL} \
     -DsystemLogLevel=${SYSTEM_LOG_LEVEL} \
     -Dserver.port=${SERVER_PORT} \
     -Dservice.name=${SERVICE_NAME} \
     -Dservice.oauth.token.url=${SERVICE_OAUTH_TOKEN_URL} \
     -Dservice.oauth.client.key=${SERVICE_OAUTH_CLIENT_KEY} \
     -Dservice.oauth.client.secret=${SERVICE_OAUTH_CLIENT_SECRET} \
     -Dspring.datasource.url=${DATASOURCE_URL} \
     -Dspring.datasource.username=${DATASOURCE_USERNAME} \
     -Dspring.datasource.password=${DATASOURCE_PASSWORD} \
     -Dspring.datasource.hikari.connection-timeout=${DATASOURCE_CONNECTION_TIMEOUT} \
     -Dspring.datasource.driver-class=${DATASOURCE_DRIVER_CLASS} \
     -Dspring.jpa.hibernate.naming.implicit-strategy=${JPA_NAMING_IMPLICIT_STRATEGY} \
     -Dspring.jpa.hibernate.naming.physical-strategy=${JPA_NAMING_PHYSICAL_STRATEGY} \
     -Dspring.jpa.hibernate.c3p0.min.size=${JPA_MIN_SIZE} \
     -Dspring.jpa.hibernate.c3p0.max.size=${JPA_MAX_SIZE} \
     -Dspring.jpa.hibernate.c3p0.timeout=${JPA_TIMEOUT} \
     -Dservice.instance.id=${SERVICE_INSTANCE_ID} \
     -Dpcp.wash.rule.cutoff.day=${WASH_RULE_CUTOFF_DAY} \
     -Dpcp.config.service.url=${PCP_CONFIG_SERVICE_URL} \
     -Dpcp.mtv.sync.service.endpoint=${PCP_MTV_SYNC_SERVICE_ENDPOINT} \
     -Dpcp.config.data.refresh.corn.expression=${PCP_CONFIG_DATA_REFRESH_CORN_EXPRESSION} \
     -Dmanagement.endpoints.enabled-by-default=${MANAGEMENT_ENDPOINTS_ENABLED_BY_DEFAULT} \
     -Dmanagement.endpoints.web.path-mapping.health=${MANAGEMENT_ENDPOINTS_WEB_PATH_MAPPING_HEALTH} \
     -Dmanagement.endpoint.health.enabled=${MANAGEMENT_ENDPOINT_HEALTH_ENABLED} \
     -Dmanagement.endpoint.health.show-details=${MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS} \
     -Dpcp.assignment.process.workers.count=${PCP_ASSIGNMENT_PROCESS_WORKERS_COUNT} \
     -Denvironment=${ENVIRONMENT} \
     -Dscheduling.job.pcp.validation.delay=${SCHEDULING_JOB_PCP_VALIDATION_DELAY} \
     -Dscheduling.job.pcp.assignment.delay=${SCHEDULING_JOB_PCP_ASSIGNMENT_DELAY} \
     -Dscheduling.job.pcp.config.delay=${SCHEDULING_JOB_PCP_CONFIG_DELAY} \
     -Dpcp.calculation.workers=${PCP_CALCULATION_WORKERS} \
     -Djavax.net.ssl.trustStore=/usr/java/latest/jre/lib/security/cacerts \
     -jar /pcp-calculation-service-1.0.0.jar
