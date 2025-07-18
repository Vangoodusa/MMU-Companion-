package com.aeci.mmucompanion.domain.usecase.form

import com.aeci.mmucompanion.domain.model.FormData
import com.aeci.mmucompanion.domain.repository.FormRepository
import javax.inject.Inject

class GetFormsByUserUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend operator fun invoke(userId: String): List<FormData> {
        return formRepository.getAllFormsByUser(userId)
    }
} 