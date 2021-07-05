package net.zerox.github;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ConnectionThrottle {
	
	private final LoadingCache<InetAddress, AtomicInteger> throttle;
	private final int throttleLimit;
	
	public ConnectionThrottle(final int throttleTime, final int throttleLimit) {
		this(Ticker.systemTicker(), throttleTime, throttleLimit);
	}
	
	@VisibleForTesting
    ConnectionThrottle(final Ticker ticker, final int throttleTime, final int throttleLimit) {
        this.throttle = CacheBuilder.newBuilder().ticker(ticker).concurrencyLevel(Runtime.getRuntime().availableProcessors()).initialCapacity(100).expireAfterWrite(throttleTime, TimeUnit.MILLISECONDS).build((CacheLoader<? super InetAddress, AtomicInteger>)new CacheLoader<InetAddress, AtomicInteger>() {
            @Override
            public AtomicInteger load(final InetAddress key) throws Exception {
                return new AtomicInteger();
            }
        });
        this.throttleLimit = throttleLimit;
    }
	
	public void unthrottle(final SocketAddress socketAddress) {
		if(!(socketAddress instanceof InetSocketAddress)) {
			return;
		}
		final InetAddress address = ((InetSocketAddress)socketAddress).getAddress();
		final AtomicInteger throttleCount = this.throttle.getIfPresent(address);
		if(throttleCount != null) {
			throttleCount.decrementAndGet();
		}
	}
	
	public boolean throttle(final SocketAddress socketAddress) {
		if(!(socketAddress instanceof InetSocketAddress)) {
			return false;
		}
		final InetAddress address = ((InetSocketAddress)socketAddress).getAddress();
		final int throttleCount = this.throttle.getUnchecked(address).incrementAndGet();
		return throttleCount > this.throttleLimit;
	}

}
