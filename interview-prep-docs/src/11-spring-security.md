# 11. Spring Security

Spring Security is a powerful and customizable authentication and access-control framework for Java applications, especially Spring-based apps. It provides comprehensive security services for authentication, authorization, and protection against common attacks.

---

## Beginner's Introduction

**What is Security?**
- Security in web applications means making sure only the right people can access the right data and features, and protecting your app from attacks.

**Why Spring Security?**
- It handles all the hard parts of security (like login, permissions, password storage, and protection from common attacks) so you can focus on your business logic.

---

## 1. Core Concepts

### DelegatingFilterProxy
- A filter that connects the Servlet container's filter chain to Spring Security's filter chain. It delegates filter operations to a Spring-managed bean, ensuring all requests pass through Spring Security's logic. Registered in `web.xml` or auto-configured in Spring Boot, it delegates to the `FilterChainProxy` bean.

### Security Filter Chain
- A sequence of servlet filters that process every request, handling authentication, authorization, CSRF, session management, and more. The order of filters is important (e.g., `UsernamePasswordAuthenticationFilter`, `BasicAuthenticationFilter`, `ExceptionTranslationFilter`).
- **Example:** Custom filter addition
    ```java
    @Component
    public class MyCustomFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            // Custom logic
            filterChain.doFilter(request, response);
        }
    }
    ```

---

## 2. Authentication
- Authentication is the process of verifying the identity of a user or system, like showing your ID at the door. Common mechanisms include username/password, JWT, OAuth2, SAML, and LDAP. The `AuthenticationManager` is the central interface for authentication, and `UserDetailsService` loads user-specific data. Passwords should always be hashed using a `PasswordEncoder` (BCrypt is recommended).
- **Example:** Custom authentication provider
    ```java
    @Component
    public class CustomAuthProvider implements AuthenticationProvider {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            // Validate credentials (e.g., check against database)
            if ("user".equals(username) && "pass".equals(password)) {
                return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            } else {
                throw new BadCredentialsException("Invalid credentials");
            }
        }
        @Override
        public boolean supports(Class<?> authentication) {
            return authentication.equals(UsernamePasswordAuthenticationToken.class);
        }
    }
    ```
- **Spring Boot Example:**
    ```java
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
    ```

---

## 3. Authorization
- Authorization determines what an authenticated user is allowed to do, like having a VIP pass to enter certain rooms. Role-Based Access Control (RBAC) assigns permissions to roles, and roles to users. You can secure methods using annotations like `@Secured`, `@PreAuthorize`, `@PostAuthorize`, and `@RolesAllowed`. URL security is configured in `WebSecurityConfigurerAdapter` or `SecurityFilterChain`.
    ```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
    ```
- **Example:**
    ```java
    @RestController
    public class AdminController {
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin/dashboard")
        public String dashboard() {
            return "Admin Dashboard";
        }
    }
    ```

---

## 4. Password Security
- Never store or send plain-text passwords. Always hash passwords using a `PasswordEncoder` (BCrypt is a strong, adaptive hashing algorithm). This ensures that even if someone steals your database, they can't read the passwords.
    ```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    ```

---

## 5. SecurityContext & Principal
- After login, Spring Security keeps track of the current user (the Principal) and their roles in the `SecurityContext`. The `SecurityContextHolder` provides access to the current `Authentication` object, which contains user details and authorities.
    ```java
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    ```

---

## 6. CSRF Protection
- Cross-Site Request Forgery (CSRF) is an attack where a malicious site tricks your browser into making unwanted requests. Spring Security adds a secret token to forms to prevent this. CSRF protection is enabled by default for state-changing operations (POST, PUT, DELETE). For APIs using JWT or stateless authentication, CSRF can be disabled:
    ```java
    http.csrf().disable();
    ```

---

## 7. Session Management
- Sessions are like tickets that prove you’re logged in. For REST APIs, sessions are usually not used (stateless), but for web apps, they are. Spring Security provides session fixation protection, concurrent session control, and stateless session support (using JWT tokens for REST APIs).
    ```java
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    ```

---

## 8. Exception Handling
- Spring Security can show custom error pages or messages when someone tries to access something they shouldn’t, or isn’t logged in. Use `AccessDeniedHandler` for 403 errors and `AuthenticationEntryPoint` for 401 errors (unauthenticated access).
    ```java
    http.exceptionHandling()
        .authenticationEntryPoint(new CustomAuthEntryPoint())
        .accessDeniedHandler(new CustomAccessDeniedHandler());
    ```

---

## 9. Custom Filters
- You can add your own security checks (like checking a custom token) by creating a custom filter and adding it to the filter chain. Extend `OncePerRequestFilter` for custom logic (e.g., JWT validation) and use `http.addFilterBefore()` or `http.addFilterAfter()` to add it.
    ```java
    http.addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    ```

---

## 10. OAuth2 & JWT
- OAuth2 allows users to log in with providers like Google or Facebook (delegated login). JWT (JSON Web Token) is a compact, self-contained way to represent user identity and claims, used for stateless authentication in REST APIs. Spring Security has built-in support for OAuth2 login and resource servers.
    ```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2Login();
        return http.build();
    }
    ```
- **JWT Example:**
    - A JWT is a string like `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
    - It contains three parts: header, payload (user info), and signature.
    - Use libraries like `jjwt` or `java-jwt` to create/verify JWTs.

---

## 11. Security for REST APIs
- REST APIs should be stateless (no sessions), use JWT for authentication, and allow only trusted domains (CORS). Disable sessions and CSRF for stateless APIs. Configure allowed origins for cross-domain requests.
    ```java
    http.cors().and().csrf().disable();
    ```
- Use a custom `AuthenticationEntryPoint` to return JSON errors for unauthorized access.

---

## 12. Method Security Annotations
- You can put security rules directly on your methods so only certain users can call them. Use `@Secured` to restrict access by role, `@PreAuthorize` and `@PostAuthorize` for SpEL-based access control, and `@RolesAllowed` for JSR-250 role-based access.
    ```java
    @PreAuthorize("hasRole('ADMIN')")
    public void adminMethod() { ... }
    ```

---

## 13. Best Practices
- Always hash passwords (use BCrypt).
- Grant only necessary permissions (principle of least privilege).
- Use HTTPS everywhere.
- Regularly update dependencies to patch vulnerabilities.
- Log security events (logins, failed attempts, etc.).

---

## 14. Security Testing in Spring Boot
- You can write tests to make sure your security rules work as expected. Use `MockMvc` to test endpoints and verify access control.
    ```java
    @SpringBootTest
    @AutoConfigureMockMvc
    public class SecurityTests {
        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testUnauthorizedAccess() throws Exception {
            mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized());
        }
    }
    ```

---

## 15. Glossary of Common Terms
- **Authentication:** Proving who you are.
- **Authorization:** What you are allowed to do.
- **Principal:** The current logged-in user.
- **Role:** A named set of permissions (e.g., ADMIN, USER).
- **Authority:** A specific permission granted to a user.
- **CSRF:** Cross-Site Request Forgery, a type of attack.
- **JWT:** JSON Web Token, a way to securely transmit information.
- **CORS:** Cross-Origin Resource Sharing, controls which domains can access your API.

---

## 16. Common Pitfalls and How to Avoid Them
- Forgetting to hash passwords (always use a PasswordEncoder).
- Disabling CSRF without understanding the risks.
- Exposing sensitive endpoints without authentication.
- Not using HTTPS in production.
- Hardcoding secrets in code (use environment variables or config servers).

---

## 17. Common Interview Questions
- How does Spring Security filter chain work?
- How do you implement custom authentication?
- How do you secure REST APIs?
- What is CSRF and how do you protect against it?
- How do you use method-level security?
- How do you handle JWT authentication?

---
This comprehensive guide covers all major Spring Security concepts, with clear explanations, code snippets, and practical advice to help you answer interview questions confidently.
