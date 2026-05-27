package com.example.uniroomies.data.mapper

import com.example.uniroomies.data.remote.dto.UserProfileDto
import com.example.uniroomies.domain.model.UserProfile

fun UserProfileDto.toDomain() = UserProfile(
    uid = uid,
    name = name,
    age = age,
    sex = sex,
    city = city,
    university = university,
    email = email
)

fun UserProfile.toDto() = UserProfileDto(
    uid = uid,
    name = name,
    age = age,
    sex = sex,
    city = city,
    university = university,
    email = email
)
