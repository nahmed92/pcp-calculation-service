package com.deltadental.pcp.calculation.config;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableCaching
@Slf4j
@EnableAspectJAutoProxy
public class PCPConfigDataCacheConfig {

	public static final String CLAIM_STATUS_LIST_CACHE = "claimStatusListCache";
	public static final String EXPLANATION_CODES_CACHE = "explanationCodesCache";
	public static final String PROCEDURE_CODES_CACHE = "procedureCodesCache";

	@Value("${cache.time_to_live_in_seconds:1800}")
	protected String cacheTimeToLiveInSeconds;

	@Value("${cache.entries:1000}")
	protected String cacheEntries;

	@Bean
	public net.sf.ehcache.CacheManager pcpConfigDataCacheManager() {

		log.info("START PCPConfigDataCacheConfig.pcpConfigDataCacheManager()");
		if (null == cacheEntries) {
			cacheEntries = "1000";
		}
		log.info("Creating Cache for {}", CLAIM_STATUS_LIST_CACHE);
		CacheConfiguration claimStatusListCatcheConfig = createCacheConfig(CLAIM_STATUS_LIST_CACHE);
		log.info("Creating Cache for {}", EXPLANATION_CODES_CACHE);
		CacheConfiguration explanationCodesCatcheConfig = createCacheConfig(EXPLANATION_CODES_CACHE);
		log.info("Creating Cache for {}", PROCEDURE_CODES_CACHE);
		CacheConfiguration procedureCodesCacheConfig = createCacheConfig(PROCEDURE_CODES_CACHE);
		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
		log.info("Adding Cache to CacheManager for cache {}", CLAIM_STATUS_LIST_CACHE);
		config.addCache(claimStatusListCatcheConfig);
		log.info("Adding Cache to CacheManager for cache {}", EXPLANATION_CODES_CACHE);
		config.addCache(explanationCodesCatcheConfig);
		log.info("Adding Cache to CacheManager for cache {}", PROCEDURE_CODES_CACHE);
		config.addCache(procedureCodesCacheConfig);

		log.info("END PCPConfigDataCacheConfig.pcpConfigDataCacheManager()");
		return net.sf.ehcache.CacheManager.newInstance(config);
	}

	private CacheConfiguration createCacheConfig(String cacheName) {
		CacheConfiguration accessTokensEhCacheConfig = // cache name -- reference in service call
				new CacheConfiguration().eternal(false) // if true, timeouts are
						.timeToIdleSeconds(0) // time since last accessed before item is marked for removal
						.timeToLiveSeconds(Integer.parseInt(cacheTimeToLiveInSeconds)) // time since inserted before
																						// item is marked for
						// removal
						.memoryStoreEvictionPolicy("LRU") // eviction policy for when items exceed cache. LRU = Least
															// Recently Used
						.maxEntriesLocalHeap(Integer.parseInt(cacheEntries)) // max entries
						.transactionalMode("off") // transactional mode
						.name(cacheName);
		return accessTokensEhCacheConfig;
	}

	@Bean
	// @Override
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(pcpConfigDataCacheManager());
	}
}