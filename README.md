# Securai Interceptor

## Overview

Securai Interceptor is a smart security layer for OkHttp that helps keep your network requests safe. XSS vulnerabilities can expose sensitive user data and compromise your application's security. Securai Interceptor acts as a first line of defense, proactively scanning requests for these threats. It automatically scans requests for XSS (Cross-Site Scripting) vulnerabilities using an embedded AI-powered classifier and either blocks or allows them based on the results.

### Features

-   **Catches XSS before it hits your server**: Uses AI to detect potential XSS threats in your requests before they reach your backend.
-   **Seamless Integration**: Works effortlessly with OkHttp and Retrofit, requiring minimal setup.
-   **Control which parts of your requests are checked**: The @Secured annotation lets you choose which parts of your requests should be analyzed.
-   **Optimized for ARM64**: The library is built for ARM64 devices and won’t work on x86 Android Studio emulators. Testing must be done on real ARM64 devices or compatible emulators.
-   **Fast and lightweight**: The embedded AI model is designed for speed, adding only **milliseconds** to request processing time.

## Installation

To add Securai Interceptor to your project, include the following in your `build.gradle` file:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation("com.github.yunusemrehalil:securai:1.0.0-beta")
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
OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new SecuraiInterceptor(applicationContext, true))
        .build();

Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://example.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
```

### 3. What Happens If a Threat Is Found?

If a security threat is detected, the interceptor blocks the request and denies it with 403. In future versions, custom handling for denied requests will be supported.

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

The project's license information will be available soon in the [LICENSE.md](./LICENSE.md) file.

## Get in Touch

Have questions or feedback? Open an issue on GitHub or contact me at mtmyunusemrehalil@gmail.com.
