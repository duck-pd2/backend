package xyz.potatoez.utils

import org.mindrot.jbcrypt.BCrypt

fun hashPwd(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(12))

fun checkPwd(password: String, target: String): Boolean = BCrypt.checkpw(target, password)