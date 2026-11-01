# Behavior Changes (STEP 7)

This list records the intentional behavior changes introduced in STEP 7.

1. **HR registration password length:** Registration now accepts passwords from 8 through 128 characters (inclusive), instead of exactly 8 characters. Login remains unchanged.
2. **Consistent API errors:** Auth, employee, and asset services now return non-empty `message` values for unexpected failures, malformed JSON, and field validation errors. Validation responses include field-level details; malformed JSON returns a client error.
3. **Asset write permissions:** HR and IT can create/update assets, assign assets, and return assets. ADMIN retains full access. HR does not gain access to maintenance operations; other route permissions remain as before.
4. **Assignment dependency ordering:** Asset assignment checks the employee service before opening the asset database transaction. A missing employee remains a 404 and does not count as a circuit-breaker failure; actual dependency outages continue to map to 503.
