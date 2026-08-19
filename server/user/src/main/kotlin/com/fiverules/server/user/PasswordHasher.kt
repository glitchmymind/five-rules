package com.fiverules.server.user

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val COST = 12

    fun hash(plainPassword: String): String =
        BCrypt.withDefaults().hashToString(COST, plainPassword.toCharArray())

    fun verify(plainPassword: String, storedHash: String): Boolean =
        BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash).verified
}
