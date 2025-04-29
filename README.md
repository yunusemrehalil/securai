# Securai Interceptor

## Overview

Securai Interceptor is a smart security layer for OkHttp that helps keep your network requests safe. XSS vulnerabilities can expose sensitive user data and compromise your application's security. Securai Interceptor acts as a first line of defense, proactively scanning requests for these threats. It automatically scans requests for XSS (Cross-Site Scripting) vulnerabilities using an embedded AI-powered classifier and either blocks or allows them based on the results.

### Features

| Feature                        | Description                                                                                                                                          |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Smart XSS Protection**       | Securai Interceptor adds an extra layer of security to your OkHttp and Retrofit requests by scanning for potential XSS attacks before they reach your backend.|
| **Embedded AI Model**          | It uses a lightweight, embedded AI model to detect suspicious input on the fly, with minimal impact on performance.|
| **Field-Level Control**        | You can choose exactly which request fields to analyze using the `@Secured` annotation.|
| **ARM64-Only Compatibility**   |  The interceptor is built specifically for ARM64 devices and isn’t compatible with x86-based Android Studio emulators, so testing needs to happen on real hardware or supported environments.|

## Installation

To add Securai Interceptor to your project, include the following in your `build.gradle` file:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation("com.github.yunusemrehalil:securai:1.0.2-beta")
}
```

## How It Works

### 1. Secure API Endpoints with `@Secured`

Simply add the `@Secured` annotation to Retrofit API methods to enable security checks.

```java
public interface ApiService {
    @Secured(fields = {Field.BODY, Field.PARAM})
    @POST("/submit")
    Call<ResponseBody> submitData(@Body RequestBody body);
}
```

- This ensures that both the request body and parameters are checked for security vulnerabilities.

### 2. Attach the Interceptor to OkHttp

Register `SecuraiInterceptor` in your OkHttp client to enforce security validation.

```java
SecuraiInterceptor interceptor = new SecuraiInterceptorBuilder(context)
        .setLoggingEnabled(true)  // Enable logging
        .setThreshold(0.75f)      // Custom security threshold
        .setDeniedResponse(new CustomDeniedResponse()) // Custom response handling
        .build();

OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();

Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://example.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
```

## Performance

To be filled after.

## API Overview

### **SecuraiInterceptor**

- Intercepts outgoing network requests.
- Extracts request data (body, headers, parameters).
- Uses an AI-powered XSS classifier to analyze the request.
- Blocks requests containing security threats.

### **@Secured Annotation**

- Marks API methods for security validation.
- Supports selective security checking (BODY, HEADER, PARAM, or ALL).

## Contributing

Have ideas or improvements? Feel free to open a pull request or create an issue on GitHub!

## License

[LICENSE](./LICENSE.md)

## Get in Touch

Have questions or feedback? Open an issue on GitHub or contact me at mtmyunusemrehalil@gmail.com.
