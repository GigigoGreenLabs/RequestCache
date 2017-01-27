package com.zireck.requestcache.library.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodTypeTest {

  @Test public void shouldProperlyBuildMethodType() throws Exception {
    MethodType post = MethodType.POST;

    assertThat(post, notNullValue());
    assertThat(post.toString(), notNullValue());
    assertThat(post.toString(), instanceOf(String.class));
    assertThat(post.toString(), is("POST"));
  }
}