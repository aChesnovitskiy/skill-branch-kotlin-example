package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = User.makeUser(fullName, email = email, password = password)
        .also { user ->
            if (user.login !in map.keys) map[user.login] = user
            else throw IllegalArgumentException("A user with this email already exists")
        }

    fun registerUserByPhone(fullName: String, rawPhone: String): User =
        if (validatePhone(rawPhone)) {
            User.makeUser(fullName, phone = rawPhone)
                .also { user ->
                    if (user.login !in map.keys) map[user.login] = user
                    else throw IllegalArgumentException("A user with this phone already exists")
                }
        } else {
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        }

    fun importUsers(list: List<String>): List<User> {
        val result = mutableListOf<User>()
        for (csvString in list) {
            val csvList = csvString.csvToList()
            println("csvList: $csvList")
            val fullName = csvList[0]
            val email = csvList[1].ifBlank { null }
            val saltAndHash = csvList[2]
            val phone = csvList[3].ifBlank { null }
            val user =
                User.makeUser(fullName, email = email, phone = phone, saltAndHash = saltAndHash)
            if (user.login !in map.keys) {
                map[user.login] = user
            }
            else {
                throw IllegalArgumentException("This user already exists")
            }
            println("User info:\n${user.userInfo}")
            result.add(user)
        }
        return result
    }

    fun loginUser(login: String, password: String): String? =
        map[login.trim().transformIfPhone()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    fun requestAccessCode(login: String) {
        if ("""^\+.*""".toRegex().matches(login)) {
            map[login.trim().transformIfPhone()]?.let {
                val oldCode = it.accessCode
                val newCode = it.generateAccessCode()
                it.changePassword(oldCode!!, newCode)
                it.sendAccessCodeToUser(login, newCode)
            }
        } else {
            throw IllegalArgumentException("Access code is available only for users, registered via phone")
        }
    }

    private fun String.transformIfPhone(): String =
        if ("""^\+.*""".toRegex().matches(this)) this.replace("""[^+\d]""".toRegex(), "")
        else this

    private fun validatePhone(rawPhone: String): Boolean =
        """^\+\d{11}""".toRegex().matches(rawPhone.replace("""[^+\d]""".toRegex(), ""))

    private fun String.csvToList(): List<String> = this.split(";")

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}