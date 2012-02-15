package com.readytalk.staccato;

import com.google.inject.ImplementedBy;

@ImplementedBy(Staccato.class)
public interface StaccatoExecutor {
	/**
	 * Executes Staccato via the {@link com.readytalk.staccato.StaccatoOptions} provided.
	 *
	 * @param options
	 */
	void execute(StaccatoOptions options);
}
