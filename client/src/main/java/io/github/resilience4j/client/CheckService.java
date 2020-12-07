package io.github.resilience4j.client;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CheckService {

	public CheckService(BulkheadRegistry bulkheadRegistry,
	                    ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
	                    CircuitBreakerRegistry circuitBreakerRegistry,
	                    RateLimiterRegistry rateLimiterRegistry,
	                    RetryRegistry retryRegistry) {
		this.bulkheadRegistry = bulkheadRegistry;
		this.threadPoolBulkheadRegistry = threadPoolBulkheadRegistry;
		this.circuitBreakerRegistry = circuitBreakerRegistry;
		this.rateLimiterRegistry = rateLimiterRegistry;
		this.retryRegistry = retryRegistry;
	}

	private final BulkheadRegistry bulkheadRegistry;
	private final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;
	private final CircuitBreakerRegistry circuitBreakerRegistry;
	private final RateLimiterRegistry rateLimiterRegistry;
	private final RetryRegistry retryRegistry;

	public Map<String, Number> check() {
		Map<String, Number> result = new HashMap<>();
		result.put("Bulkhead maxConcurrentCalls", bulkheadRegistry.getDefaultConfig().getMaxConcurrentCalls());
		result.put("CircuitBreaker failureRateThreshold", circuitBreakerRegistry.getDefaultConfig().getFailureRateThreshold());
		result.put("RateLimiter limitForPeriod", rateLimiterRegistry.getDefaultConfig().getLimitForPeriod());
		result.put("ThreadPoolBulkhead max thread pool", threadPoolBulkheadRegistry.getDefaultConfig().getMaxThreadPoolSize());
		result.put("Retry max retry", retryRegistry.getDefaultConfig().getMaxAttempts());
		return result;
	}

	public Map<String, Object> tryTest() {
		Map<String, Object> results = new HashMap<>();
		results.put("result", retryRegistry.retry("default")
				.executeSupplier(this::threetimes));
		return results;
	}


	int retryCount = 1;

	public String threetimes() {
		if (retryCount <= 4) {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("재시도 회수: " + retryCount);
			retryCount++;
			throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
		}
		System.out.println("재시도 회수: " + retryCount);
		return "성공";
	}
}
