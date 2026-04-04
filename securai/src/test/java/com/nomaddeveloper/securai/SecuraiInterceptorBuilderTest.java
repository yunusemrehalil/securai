package com.nomaddeveloper.securai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.content.Context;

import com.nomaddeveloper.securai.internal.exception.SecuraiException;
import com.nomaddeveloper.securai.internal.response.denied.DeniedResponseImpl;

import org.junit.Before;
import org.junit.Test;

public class SecuraiInterceptorBuilderTest {

    private Context mockContext;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_nullContext_throwsNullPointerException() {
        new SecuraiInterceptorBuilder(null);
    }

    @Test
    public void constructor_validContext_createsBuilder() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        assertNotNull(builder);
    }

    @Test(expected = SecuraiException.class)
    public void setThreshold_negativeValue_throwsSecuraiException() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setThreshold(-0.1f);
    }

    @Test(expected = SecuraiException.class)
    public void setThreshold_greaterThanOne_throwsSecuraiException() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setThreshold(1.1f);
    }

    @Test
    public void setThreshold_zero_accepted() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setThreshold(0f);
        assertEquals(0f, builder.getThreshold(), 0.001f);
    }

    @Test
    public void setThreshold_one_accepted() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setThreshold(1f);
        assertEquals(1f, builder.getThreshold(), 0.001f);
    }

    @Test
    public void setThreshold_validValue_setsThreshold() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setThreshold(0.75f);
        assertEquals(0.75f, builder.getThreshold(), 0.001f);
    }

    @Test
    public void setDeniedResponse_null_usesDefault() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setDeniedResponse(null);
        assertTrue(builder.getDeniedResponse() instanceof DeniedResponseImpl);
    }

    @Test
    public void setDeniedResponse_custom_usesCustom() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        final DeniedResponse customResponse = mock(DeniedResponse.class);
        builder.setDeniedResponse(customResponse);
        assertEquals(customResponse, builder.getDeniedResponse());
    }

    @Test
    public void setLoggingEnabled_true_isLoggingEnabledReturnsTrue() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setLoggingEnabled(true);
        assertTrue(builder.isLoggingEnabled());
    }

    @Test
    public void setFailClosed_false_isFailClosedReturnsFalse() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        builder.setFailClosed(false);
        assertFalse(builder.isFailClosed());
    }

    @Test
    public void defaults_loggingDisabled() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        assertFalse(builder.isLoggingEnabled());
    }

    @Test
    public void defaults_thresholdIs0Point8() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        assertEquals(0.8f, builder.getThreshold(), 0.001f);
    }

    @Test
    public void defaults_failClosedTrue() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        assertTrue(builder.isFailClosed());
    }

    @Test
    public void build_returnsSecuraiInterceptor() {
        final SecuraiInterceptorBuilder builder = new SecuraiInterceptorBuilder(mockContext);
        final SecuraiInterceptor interceptor = builder.build();
        assertNotNull(interceptor);
    }
}
