package com.vangelnum.wisher.domain.repository

import com.vangelnum.wisher.data.model.UserRequest
import com.vangelnum.wisher.domain.model.User

interface UserRepository {
    suspend fun register(userRequest: UserRequest): Result<User>
    suspend fun getMe(): Result<User>
}