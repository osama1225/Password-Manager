Password-Manager
================

A password manager that stores the passwords for every domain the user wishes to.
The password manager satisfies the following requirements.

1. A master passwords should be used to generate the encryption and authentication keys needed using PBKDF2.

2. Password manager should not store any information about its master password, it is supplied by its user. The Password manager should detect when a wrong password is supplied.

3. Password manager should support the addition, modication and deletion of
a password for any given domain.

4. Password manager should store the passwords padded to a length of 32 bytes, so that no information is revealed about the length of password for a given domain, assuming that the maximum password length is 32 bytes. (32 Can be replaced by any number of your choice)

5. Passwords should be stored encrypted and authenticated using Galois Counter Mode (GCM).

6. Domain names are not stored. A MAC using HMAC is stored in the table instead. Subsequent lookups will be using this HMAC derived using HMAC on the domain name.
