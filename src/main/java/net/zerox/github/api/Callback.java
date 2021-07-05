package net.zerox.github.api;

public interface Callback<V> {
	
	void done(final V v, final Throwable t);

}
