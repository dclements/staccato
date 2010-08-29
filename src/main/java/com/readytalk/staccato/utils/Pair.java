/*
 * Copyright (c) 2005-2006, Inversoft, All Rights Reserved
 */
package com.readytalk.staccato.utils;

import java.io.Serializable;

/**
 * <p>
 * This class is a simple tuple for storing a pair of values. This
 * defines the values as generics for type safety in 1.5 VMs.
 * </p>
 *
 * @author Brian Pontarelli
 * @since 1.0
 */
public class Pair<T, U> implements Serializable {
  private static final long serialVersionUID = 1;
  public final T first;
  public final U second;

  /**
   * Creates a new Pair using the values given.
   *
   * @param   t The first value.
   * @param   u The second value.
   * @return The Pair and never null.
   */
  public static <T, U> Pair<T, U> p(T t, U u) {
    return new Pair<T, U>(t, u);
  }

  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  public T getFirst() {
    return first;
  }

  public U getSecond() {
    return second;
  }

  public String toString() {
    return first + ":" + second;
  }
}