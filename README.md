# Securai Interceptor

## Overview

Securai Interceptor is an advanced security interceptor for OkHttp that analyzes HTTP requests for
potential security vulnerabilities. It leverages AI-based security validation to detect XSS (Cross-Site Scripting) 
attacks in network requests.

### Features

- **AI-Based Security Analysis**: Uses an integrated XSS classifier to detect threats in request
  data.
- **Annotation-Based Security Enforcement**: The `@Secured` annotation allows developers to mark
  specific endpoints for security validation.
- **Logging Support**: Enables detailed logging for debugging and monitoring security events.
- **Minimal Overhead**: Designed to integrate seamlessly with OkHttp without significant performance
  impact.

## Installation

To install Securai Interceptor, add the following dependency to your project's `build.gradle` file:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation("com.github.yunusemrehalil:securai:1.0.0-beta")
}
```

## Usage

### 1. Add the `@Secured` Annotation

To enable security validation on an API endpoint, annotate the Retrofit method with `@Secured` and
specify which parts of the request should be analyzed.

```java
public interface ApiService {
    @Secured(fields = {Field.BODY, Field.PARAM})
    @POST("/submit")
    Call<ResponseBody> submitData(@Body RequestBody body);
}
```

- The above example ensures that both the request body and parameters are checked for security
  vulnerabilities.

### 2. Attach the Interceptor to OkHttp

Integrate `SecuraiInterceptor` into your OkHttp client to enforce security validation.

```java
OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new SecuraiInterceptor(context, true))
        .build();

Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://example.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
```

### 3. Handling Security Violations

If a security threat is detected, the interceptor triggers a blocked response handled by
`DeniedResponseImpl`. The default behavior is returning an HTTP 403 error.

```java

@Override
public Response<ResponseBody> onSecurityViolation(Request request, String summary) {
    return createErrorResponse(request, 403, "Security Threat Detected");
}
```

## API Overview

### **SecuraiInterceptor**

- Intercepts outgoing network requests.
- Extracts request data (body, headers, parameters).
- Analyzes the request using an AI-powered XSS classifier.
- Blocks requests that contain security threats.

### **@Secured Annotation**

- Used to mark API methods for security validation.
- Supports field-level security checking (BODY, HEADER, PARAM, or ALL).

## Contribution

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch.
3. Implement your feature or fix.
4. Submit a pull request.

## License

The project's license information can be found in the [LICENSE.md](./LICENSE.md) file.

## Contact

For issues or inquiries, open an issue on GitHub or contact me at mtmyunusemrehalil@gmail.com

---
